package com.totti.nioReactor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {

    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    private static List<SelectionKey> writeQueue = new ArrayList<SelectionKey>();
    private static Selector selector = null;

    public static void addWriteQueue(SelectionKey key) {
        synchronized (writeQueue) {
            writeQueue.add(key);
            selector.wakeup();
        }
    }

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(9090));
        serverSocketChannel.configureBlocking(false);
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            System.out.println("服务器端：正在监听9090端口");
            int num = selector.select();
            if (num > 0) { // 判断是否存在可用的通道
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = serverSocketChannel.accept();

                        System.out.println("处理请求：" + socketChannel.getRemoteAddress());
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }

                    if (key.isReadable()) {
                        System.out.println("读事件");
                        // key.cancel();
                        executorService.submit(() -> {
                            try {
                                read(key);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }

                    if (key.isWritable()) {
                        System.out.println("写事件");
                        // key.cancel();
                        executorService.submit(() -> {
                            try {
                                write(key);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            } else {
                synchronized (writeQueue) {
                    while (writeQueue.size() > 0) {
                        SelectionKey key = writeQueue.remove(0);
                        SocketChannel channel = (SocketChannel)key.channel();
                        Object attachment = key.attachment();
                        // channel.register(selector, SelectionKey.OP_WRITE, attachment);
                        key.interestOps(SelectionKey.OP_WRITE);
                    }
                }
            }
        }
    }

    public static void read(SelectionKey key) throws IOException {
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
        System.out.println("服务器端接收到的数据：" + new String(baos.toByteArray()));
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
