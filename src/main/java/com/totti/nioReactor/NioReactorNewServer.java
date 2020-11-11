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
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NioReactorNewServer {
    private static ConcurrentLinkedQueue<SocketChannel> queue = new ConcurrentLinkedQueue();

    public static void main(String[] args) throws Exception {
        Selector mainSelector = Selector.open();
        Selector subSelector = Selector.open();
        new Thread(() -> {
            try {
                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.socket().bind(new InetSocketAddress(9090));
                serverSocketChannel.configureBlocking(false);

                serverSocketChannel.register(mainSelector, SelectionKey.OP_ACCEPT);

                while (mainSelector.select() > 0) {
                    Set<SelectionKey> selectionKeySet = mainSelector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeySet.iterator();

                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        if (key.isAcceptable()) {
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            // System.out.println("get a new connection");
                            // socketChannel.configureBlocking(false);
                            // SelectionKey selectionKey = socketChannel.register(mainSelector, SelectionKey.OP_READ);
                            queue.offer(socketChannel);
                            subSelector.wakeup();
                        }
                        iterator.remove();

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();

        new Thread(() -> {
            System.out.println("subReactor...");
            try {
                while (true) {
                    if (subSelector.select() > 0) {
                        Set<SelectionKey> selectionKeySet = subSelector.selectedKeys();
                        Iterator<SelectionKey> iterator = selectionKeySet.iterator();

                        while (iterator.hasNext()) {
                            System.out.println("1111");
                            SelectionKey innerSelectionKey = iterator.next();
                            iterator.remove();

                            if (innerSelectionKey.isReadable()) {
                                SocketChannel readChannel = (SocketChannel)innerSelectionKey.channel();
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
                                innerSelectionKey.attach(baos);

                                innerSelectionKey.interestOps(SelectionKey.OP_WRITE);
                                innerSelectionKey.selector().wakeup();
                            } else if (innerSelectionKey.isWritable()) {
                                SocketChannel writeChannel = (SocketChannel)innerSelectionKey.channel();
                                ByteArrayOutputStream attachment =
                                    (ByteArrayOutputStream)innerSelectionKey.attachment();
                                System.out.println("客户端发送来的数据：" + new String(attachment.toByteArray()));
                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                String message = "你好，我是服务器！！";
                                buffer.put(message.getBytes());
                                buffer.flip();
                                writeChannel.write(buffer);
                                writeChannel.close();
                            }

                        }
                    } else {
                        SocketChannel channel;
                        while ((channel = queue.poll()) != null) {
                            channel.configureBlocking(false);
                            channel.register(subSelector, SelectionKey.OP_READ);
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }
}
