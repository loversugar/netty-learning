package com.totti.socketChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SocketChannelServer {
    public static void main(String[] args)
        throws IOException {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(9090));
            
            Selector selector = Selector.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            
            while (true) {
                int readyChannel = selector.select();
                
                if (readyChannel == 0) {
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                
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
                        ByteBuffer byteBuffer = ByteBuffer.allocate(3);
                        
                        ((SocketChannel)selectionKey.channel()).read(byteBuffer);
                        byteBuffer.flip();
                        
                        byte[] buffer = new byte[byteBuffer.limit()];
                        byteBuffer.get(buffer);
                        
                        System.out.println(new String(buffer).trim());
                        selectionKey.channel().register(selector, SelectionKey.OP_WRITE);
                    }
                    
                    if (selectionKey.isWritable()) {
                        System.out.println("writing....");
                        ((SocketChannel)selectionKey.channel()).write(ByteBuffer.wrap("s1".getBytes()));
                        selectionKey.channel().close();
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
