package com.totti.nioReactor;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioReactorServer {
    static Selector selector;
    
    public static void main(String[] args) {
        new Thread(() -> {
            
            try {
                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.socket().bind(new InetSocketAddress(9090));
                
                selector = Selector.open();
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                
                while (selector.select() > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        iterator.remove();
                        
                        if (selectionKey.isAcceptable()) {
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            System.out.println("get a new connection");
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                        }
                        
                        if (selectionKey.isReadable()) {
                            System.out.println("read.....");
                            new Thread(() -> {
                                try {
                                    read(selectionKey);
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        }
                        
                        if (selectionKey.isWritable()) {
                            new Thread(() -> {
                                try {
                                    write(selectionKey);
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        }
                    }
                }
            }
            catch (Exception e) {
                System.out.println("exception.....");
                e.printStackTrace();
            }
        }).start();
    }
    
    public static void write(SelectionKey selectionKey)
        throws Exception {
        System.out.println("===== Write Event Handler =====");
        
        SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
        ByteBuffer bb = ByteBuffer.wrap("222".getBytes());
        socketChannel.write(bb);
        
        selectionKey.channel().close();
    }
    
    public static void read(SelectionKey selectionKey)
        throws Exception {
        System.out.println("reading...");
        ByteBuffer byteBuffer = ByteBuffer.allocate(3);
        
        ((SocketChannel)selectionKey.channel()).read(byteBuffer);
        byteBuffer.flip();
        
        byte[] buffer = new byte[byteBuffer.limit()];
        byteBuffer.get(buffer);
        
        System.out.println(new String(buffer).trim());
        selectionKey.channel().register(selector, SelectionKey.OP_WRITE);
    }
}
