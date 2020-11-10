package com.totti.test1;

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

public class NIOServerSocket {

    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    // 存储SelectionKey的队列
    private static List<SelectionKey> writeQueue = new ArrayList<SelectionKey>();
    private static Selector selector = null;

    // 添加SelectionKey到队列
    public static void addWriteQueue(SelectionKey key) {
        synchronized (writeQueue) {
            writeQueue.add(key);
            // 唤醒主线程
            selector.wakeup();
        }
    }

    public static void main(String[] args) throws IOException {

        // 1.创建ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 2.绑定端口
        serverSocketChannel.bind(new InetSocketAddress(9090));
        // 3.设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        // 4.创建通道选择器
        selector = Selector.open();
        /*
         * 5.注册事件类型
         *
         *  sel:通道选择器
         *  ops:事件类型 ==>SelectionKey:包装类，包含事件类型和通道本身。四个常量类型表示四种事件类型
         *  SelectionKey.OP_ACCEPT 获取报文      SelectionKey.OP_CONNECT 连接
         *  SelectionKey.OP_READ 读           SelectionKey.OP_WRITE 写
         */
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            System.out.println("服务器端：正在监听9090端口");
            // 6.获取可用I/O通道,获得有多少可用的通道
            int num = selector.select();
            if (num > 0) { // 判断是否存在可用的通道
                // 获得所有的keys
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                // 使用iterator遍历所有的keys
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                // 迭代遍历当前I/O通道
                while (iterator.hasNext()) {
                    // 获得当前key
                    SelectionKey key = iterator.next();
                    // 调用iterator的remove()方法，并不是移除当前I/O通道，标识当前I/O通道已经处理。
                    iterator.remove();
                    // 判断事件类型，做对应的处理
                    if (key.isAcceptable()) {
                        ServerSocketChannel ssChannel = (ServerSocketChannel)key.channel();
                        SocketChannel socketChannel = ssChannel.accept();

                        System.out.println("处理请求：" + socketChannel.getRemoteAddress());
                        // 获取客户端的数据
                        // 设置非阻塞状态
                        socketChannel.configureBlocking(false);
                        // 注册到selector(通道选择器)
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        System.out.println("读事件");
                        // 取消读事件的监控
                        key.cancel();
                        // 调用读操作工具类
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
                    } else if (key.isWritable()) {
                        System.out.println("写事件");
                        // 取消读事件的监控
                        key.cancel();
                        // 调用写操作工具类
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
            } else {
                synchronized (writeQueue) {
                    while (writeQueue.size() > 0) {
                        SelectionKey key = writeQueue.remove(0);
                        // 注册写事件
                        SocketChannel channel = (SocketChannel)key.channel();
                        Object attachment = key.attachment();
                        channel.register(selector, SelectionKey.OP_WRITE, attachment);
                    }
                }
            }
        }
    }

}
