package com.totti.nioReactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioReactorClient {
    private static SocketChannel client;
    private static ByteBuffer buffer;
    private static NioReactorClient instance;

    private NioReactorClient() {
        try {
            client = SocketChannel.open(new InetSocketAddress("localhost", 9090));
            buffer = ByteBuffer.allocate(1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static NioReactorClient start() {
        if (instance == null)
            instance = new NioReactorClient();

        return instance;
    }

    public static void stop() throws IOException {
        client.close();
        buffer = null;
    }

    public static void main(String[] args) {
        NioReactorClient.start().sendMessage("test");
    }

    public String sendMessage(String msg) {
        buffer = ByteBuffer.wrap(msg.getBytes());
        String response = null;
        try {
            client.write(buffer);
            buffer.clear();
            client.read(buffer);
            response = new String(buffer.array()).trim();
            System.out.println("response=" + response);
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;

    }
}
