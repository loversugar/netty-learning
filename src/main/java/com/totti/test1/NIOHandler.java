package com.totti.test1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NIOHandler {

    // 构造线程池
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void read(final SelectionKey key) {
        // 获得线程并执行
        executorService.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    SocketChannel readChannel = (SocketChannel)key.channel();
                    // I/O读数据操作
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
                    // 将数据添加到key中
                    key.attach(baos);
                    // 将注册写操作添加到队列中
                    NIOServerSocket.addWriteQueue(key);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void write(final SelectionKey key) {
        // 拿到线程并执行
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    // 写操作
                    SocketChannel writeChannel = (SocketChannel)key.channel();
                    // 拿到客户端传递的数据
                    ByteArrayOutputStream attachment = (ByteArrayOutputStream)key.attachment();
                    System.out.println("客户端发送来的数据：" + new String(attachment.toByteArray()));
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    String message = "你好，我是服务器！！";
                    buffer.put(message.getBytes());
                    buffer.flip();
                    writeChannel.write(buffer);
                    writeChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
