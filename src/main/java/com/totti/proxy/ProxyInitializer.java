package com.totti.proxy;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class ProxyInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ProxyInBoundHandler());
    }
}
