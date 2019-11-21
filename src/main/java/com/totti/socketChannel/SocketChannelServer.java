package com.totti.socketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class SocketChannelServer {
    public static void main(String[] args) throws IOException {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(9090));

            Selector selector = Selector.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);


            while(true) {
                int readyChannel = selector.select();

                if (readyChannel == 0) {
                    continue;
                }
                Iterator<SelectionKey> iterator= selector.selectedKeys().iterator();
                System.out.println("1size is " + selector.keys().size());

                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    if (selectionKey.isAcceptable()) {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        System.out.println("get a new connection");
                        System.out.println("size is " + selector.keys().size());
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }
                    if (selectionKey.isReadable()) {
                        System.out.println("reading...");
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                        ((SocketChannel)selectionKey.channel()).read(byteBuffer);

                        System.out.println(new String(byteBuffer.array()).trim());
                    }
                }
            }
        } catch(Exception e) {
           e.printStackTrace();
        }

    }
}
