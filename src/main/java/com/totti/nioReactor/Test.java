package com.totti.nioReactor;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Test {
    public static void main(String[] args)
        throws IOException {
        Test mr = new Test(9090);
        mr.start();
    }

    private static final int POOL_SIZE = 3;

    // Reactor（Selector） 线程池，其中一个线程被 mainReactor 使用，剩余线程都被 subReactor 使用
    static Executor selectorPool = Executors.newFixedThreadPool(POOL_SIZE);

    // 主 Reactor，接收连接，把 SocketChannel 注册到从 Reactor 上
    private Reactor mainReactor;
    
    // 从 Reactors，用于处理 I/O，可使用 BasicHandler 和 MultithreadHandler 两种处理方式
    private Reactor[] subReactors = new Reactor[POOL_SIZE - 1];

    int next = 0;
    
    public Test(int port) {
        try {
            this.port = port;
            mainReactor = new Reactor();
            
            for (int i = 0; i < subReactors.length; i++) {
                subReactors[i] = new Reactor();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private int port;
    
    /**
     * 启动主从 Reactor，初始化并注册 Acceptor 到主 Reactor
     */
    public void start()
        throws IOException {
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
    
    /**
     * 初始化并配置 ServerSocketChannel，注册到 mainReactor 的 Selector 上
     */
    class Acceptor implements Runnable {
        final Selector sel;

        final ServerSocketChannel serverSocket;

        public Acceptor(Selector sel, int port)
            throws IOException {
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
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    static class Reactor implements Runnable {
        private ConcurrentLinkedQueue<BasicHandler> events = new ConcurrentLinkedQueue<>();

        final Selector selector;

        public Reactor()
            throws IOException {
            selector = Selector.open();
        }

        public Selector getSelector() {
            return selector;
        }

        @Override
        public void run() { // normally in a new Thread
            try {
                while (!Thread.interrupted()) { // 死循环
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
                    }
                    selected.clear(); // 清空就绪通道的 key
                }
            }
            catch (IOException ex) {
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
}

class BasicHandler implements Runnable {
    private static final int MAXIN = 1024;

    private static final int MAXOUT = 1024;

    public SocketChannel socket;

    public SelectionKey sk;

    ByteBuffer input = ByteBuffer.allocate(MAXIN);

    ByteBuffer output = ByteBuffer.allocate(MAXOUT);

    // 定义服务的逻辑状态
    static final int READING = 0, SENDING = 1, CLOSED = 2;

    int state = READING;
    
    public BasicHandler(Selector sel, SocketChannel sc)
        throws IOException {
        socket = sc;
        sc.configureBlocking(false); // 设置非阻塞
        // Optionally try first read now
        sk = socket.register(sel, 0); // 注册通道
        sk.interestOps(SelectionKey.OP_READ); // 绑定要处理的事件
        sk.attach(this); // 管理事件的处理程序

        sel.wakeup(); // 唤醒 select() 方法
    }

    public BasicHandler(SocketChannel sc) {
        socket = sc;
    }

    @Override
    public void run() {
        try {
            if (state == READING)
                read(); // 此时通道已经准备好读取字节
            else if (state == SENDING)
                send(); // 此时通道已经准备好写入字节
        }
        catch (IOException ex) {
            // 关闭连接
            try {
                sk.channel().close();
            }
            catch (IOException ignore) {
            }
        }
    }

    /**
     * 从通道读取字节
     */
    protected void read()
        throws IOException {
        System.out.println("read....");
        input.clear(); // 清空接收缓冲区
        int n = socket.read(input);
        if (inputIsComplete(n)) {// 如果读取了完整的数据
            process();
            // 待发送的数据已经放入发送缓冲区中

            // 更改服务的逻辑状态以及要处理的事件类型
            sk.interestOps(SelectionKey.OP_WRITE);
        }
    }

    // 缓存每次读取的内容
    StringBuilder request = new StringBuilder();

    /**
     * 当读取到 \r\n 时表示结束
     * 
     * @param bytes 读取的字节数，-1 通常是连接被关闭，0 非阻塞模式可能返回
     * @throws IOException
     */
    protected boolean inputIsComplete(int bytes)
        throws IOException {
        if (bytes > 0) {
            input.flip(); // 切换成读取模式
            while (input.hasRemaining()) {
                byte ch = input.get();
                
                if (ch == 3) { // ctrl+c 关闭连接
                    state = CLOSED;
                    return true;
                }
                else if (ch == '\r') { // continue
                }
                else if (ch == '\n') {
                    // 读取到了 \r\n 读取结束
                    state = SENDING;
                    return true;
                }
                else {
                    request.append((char)ch);
                }
            }
        }
        else if (bytes == -1) {
            // -1 客户端关闭了连接
            throw new EOFException();
        }
        else {
        } // bytes == 0 继续读取
        return false;
    }
    
    /**
     * 根据业务处理结果，判断如何响应
     * 
     * @throws EOFException 用户输入 ctrl+c 主动关闭
     */
    protected void process()
        throws EOFException {
        if (state == CLOSED) {
            throw new EOFException();
        }
        else if (state == SENDING) {
            String requestContent = request.toString(); // 请求内容
            System.out.println("请求内容是。。。" + requestContent);
            byte[] response = requestContent.getBytes(StandardCharsets.UTF_8);
            output.put(response);
        }
    }
    
    protected void send()
        throws IOException {
        int written = -1;
        output.flip();// 切换到读取模式，判断是否有数据要发送
        if (output.hasRemaining()) {
            written = socket.write(output);
        }
        
        // 检查连接是否处理完毕，是否断开连接
        if (outputIsComplete(written)) {
            sk.channel().close();
        }
        else {
            // 否则继续读取
            state = READING;
            // 把提示发到界面
            socket.write(ByteBuffer.wrap("\r\nreactor> ".getBytes()));
            sk.interestOps(SelectionKey.OP_READ);
        }
        
    }
    
    /**
     * 当用户输入了一个空行，表示连接可以关闭了
     */
    protected boolean outputIsComplete(int written) {
        if (written <= 0) {
            // 用户只敲了个回车， 断开连接
            return true;
        }
        
        // 清空旧数据，接着处理后续的请求
        output.clear();
        request.delete(0, request.length());
        return false;
    }
}
