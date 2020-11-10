package com.totti.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MultiReactor {
    private static final int POOL_SIZE = 3;
    // Reactor（Selector） 线程池，其中一个线程被 mainReactor 使用，剩余线程都被 subReactor 使用
    static Executor selectorPool = Executors.newFixedThreadPool(POOL_SIZE);
    int next = 0;
    // 主 Reactor，接收连接，把 SocketChannel 注册到从 Reactor 上
    private Reactor mainReactor;

    // 从 Reactors，用于处理 I/O，可使用 BasicHandler 和 MultithreadHandler 两种处理方式
    private Reactor[] subReactors = new Reactor[POOL_SIZE - 1];
    private int port;

    public MultiReactor(int port) {
        try {
            this.port = port;
            mainReactor = new Reactor();

            for (int i = 0; i < subReactors.length; i++) {
                subReactors[i] = new Reactor();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        MultiReactor mr = new MultiReactor(9090);
        mr.start();
    }

    /**
     * 启动主从 Reactor，初始化并注册 Acceptor 到主 Reactor
     */
    public void start() throws IOException {
        Thread mrThread = new Thread(mainReactor);
        mrThread.setName("mainReactor");
        new Acceptor(mainReactor.getSelector(), port); // 将 ServerSocketChannel 注册到 mainReactor

        selectorPool.execute(mrThread);

        for (int i = 0; i < subReactors.length; i++) {
            Thread srThread = new Thread(subReactors[i]);
            srThread.setName("subReactor-" + i);
            selectorPool.execute(srThread);
        }
    }

    static class Reactor implements Runnable {
        final Selector selector;
        private ConcurrentLinkedQueue<BasicHandler> events = new ConcurrentLinkedQueue<>();

        public Reactor() throws IOException {
            selector = Selector.open();
        }

        public Selector getSelector() {
            return selector;
        }

        @Override
        public void run() { // normally in a new Thread
            try {
                while (true) { // 死循环
                    BasicHandler handler = null;
                    while ((handler = events.poll()) != null) {
                        handler.socket.configureBlocking(false); // 设置非阻塞
                        // Optionally try first read now
                        handler.sk = handler.socket.register(selector, SelectionKey.OP_READ); // 注册通道
                        handler.sk.attach(handler); // 管理事件的处理程序
                    }

                    selector.select(); // 阻塞，直到有通道事件就绪
                    Set<SelectionKey> selected = selector.selectedKeys(); // 拿到就绪通道 SelectionKey 的集合
                    Iterator<SelectionKey> it = selected.iterator();
                    while (it.hasNext()) {
                        SelectionKey skTmp = it.next();
                        dispatch(skTmp); // 根据 key 的事件类型进行分发
                        it.remove();
                    }
                    // selected.clear(); // 清空就绪通道的 key
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        void dispatch(SelectionKey k) {
            Runnable r = (Runnable)(k.attachment()); // 拿到通道注册时附加的对象
            if (r != null)
                r.run();
        }

        void reigster(BasicHandler basicHandler) {
            events.offer(basicHandler);
            selector.wakeup();
        }

    }

    /**
     * 初始化并配置 ServerSocketChannel，注册到 mainReactor 的 Selector 上
     */
    class Acceptor implements Runnable {
        final Selector sel;
        final ServerSocketChannel serverSocket;

        public Acceptor(Selector sel, int port) throws IOException {
            this.sel = sel;
            serverSocket = ServerSocketChannel.open();
            serverSocket.socket().bind(new InetSocketAddress(port)); // 绑定端口
            // 设置成非阻塞模式
            serverSocket.configureBlocking(false);
            // 注册到 选择器 并设置处理 socket 连接事件
            SelectionKey sk = serverSocket.register(sel, SelectionKey.OP_ACCEPT);
            sk.attach(this);
            System.out.println("mainReactor-" + "Acceptor: Listening on port: " + port);
        }

        @Override
        public synchronized void run() {
            try {
                // 接收连接，非阻塞模式下，没有连接直接返回 null
                SocketChannel sc = serverSocket.accept();
                if (sc != null) {
                    // 把提示发到界面
                    sc.write(ByteBuffer
                        .wrap("Implementation of Reactor Design Partten by tonwu.net\r\nreactor> ".getBytes()));

                    System.out.println("mainReactor-" + "Acceptor: " + sc.socket().getLocalSocketAddress()
                        + " 注册到 subReactor-" + next);
                    // 将接收的连接注册到从 Reactor 上

                    // 发现无法直接注册，一直获取不到锁，这是由于 从 Reactor 目前正阻塞在 select() 方法上，此方法已经
                    // 锁定了 publicKeys（已注册的key)，直接注册会造成死锁

                    // 如何解决呢，直接调用 wakeup，有可能还没有注册成功又阻塞了。这是一个多线程同步的问题，可以借助队列进行处理
                    Reactor subReactor = subReactors[next];
                    subReactor.reigster(new BasicHandler(sc));
                    // new MultithreadHandler(subSel, sc);
                    if (++next == subReactors.length)
                        next = 0;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
