package com.totti.nioReactor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioReactorServer {
    static Selector selector;
    private static ConcurrentLinkedQueue<SelectionKey> writeQueue = new ConcurrentLinkedQueue();
    // private static List<SelectionKey> writeQueue = new ArrayList<SelectionKey>();

    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    private static Object lock = new Object();

    // 添加SelectionKey到队列
    public static void addWriteQueue(SelectionKey key) {
        writeQueue.offer(key);
        // 唤醒主线程
        selector.wakeup();
    }

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(9090));
        serverSocketChannel.configureBlocking(false);
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            int n = selector.select();
            if (n > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();

                    if (selectionKey.isAcceptable()) {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        System.out.println("get a new connection");
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    } else if (selectionKey.isReadable()) {
                        selectionKey.cancel();
                        executorService.submit(() -> {
                            try {
                                read(selectionKey);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

                    } else if (selectionKey.isWritable()) {
                        selectionKey.cancel();
                        executorService.submit(() -> {
                            try {
                                write(selectionKey);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            } else {
                synchronized (writeQueue) {
                    SelectionKey key = null;
                    while ((key = writeQueue.poll()) != null) {
                        // 注册写事件
                        SocketChannel channel = (SocketChannel)key.channel();
                        Object attachment = key.attachment();
                        channel.register(selector, SelectionKey.OP_WRITE, attachment);
                    }
                }
            }

        }

    }

    public static void read(SelectionKey key) throws Exception {
        SocketChannel readChannel = (SocketChannel)key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(3);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len = 0;
        while (true) {
            buffer.clear();
            len = readChannel.read(buffer);
            if (len == -1)
                break;
            buffer.flip();
            while (buffer.hasRemaining()) {
                baos.write(buffer.get());
            }
        }

        String request = new String(baos.toByteArray());
        System.out.println("服务器端接收到的数据：" + request);
        key.attach(baos);
        addWriteQueue(key);
    }

    public static void write(SelectionKey key) throws Exception {
        SocketChannel writeChannel = (SocketChannel)key.channel();
        ByteArrayOutputStream attachment = (ByteArrayOutputStream)key.attachment();
        System.out.println("客户端发送来的数据：" + new String(attachment.toByteArray()));
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        String message = "你好，我是服务器！！";
        buffer.put(message.getBytes());
        buffer.flip();
        writeChannel.write(buffer);
        writeChannel.close();
    }

}
