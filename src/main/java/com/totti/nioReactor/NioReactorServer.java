package com.totti.nioReactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioReactorServer {
    static ExecutorService executorService =
        Executors.newFixedThreadPool(2 * Runtime.getRuntime().availableProcessors());

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(9090));
        serverSocketChannel.configureBlocking(false);

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (selector.select() > 0) {
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();

                if (selectionKey.isAcceptable()) {
                    iterator.remove();

                    final SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    executorService.submit(() -> {
                        try {
                            socketChannel.register(selector, SelectionKey.OP_READ);

                            while (selector.select() > 0) {
                                Iterator<SelectionKey> innerIterator = selector.selectedKeys().iterator();

                                while (innerIterator.hasNext()) {
                                    SelectionKey selectionKey1 = innerIterator.next();

                                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                    byteBuffer.clear();
                                    if (selectionKey1.isReadable()) {
                                        System.out.println("begin to read data...");
                                        ((SocketChannel)selectionKey1.channel()).read(byteBuffer);

                                        byteBuffer.flip();

                                        byte[] bytes = new byte[byteBuffer.remaining()];
                                        byteBuffer.get(bytes, 0, bytes.length);

                                        System.out.println("data is " + new String(bytes).trim());

                                        socketChannel.register(selector, SelectionKey.OP_WRITE);

                                        selector.wakeup();

                                        innerIterator.remove();
                                    }

                                    if (selectionKey1.isWritable()) {
                                        ((SocketChannel)selectionKey1.channel()).write(byteBuffer);
                                        System.out.println("send data.....");
                                        socketChannel.register(selector, SelectionKey.OP_READ);
                                        selector.wakeup();
                                        innerIterator.remove();
                                    }
                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
    }
}
