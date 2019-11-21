package com.totti.socketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketChannelClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel =
                SocketChannel.open(new InetSocketAddress("127.0.0.1", 9090));
        socketChannel.configureBlocking(false);

            System.out.println("finishing connection");
            ByteBuffer byteBuffer = ByteBuffer.wrap("12345".getBytes());
            socketChannel.write(byteBuffer);
    }
}
