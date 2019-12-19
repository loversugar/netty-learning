package com.totti.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ProxyNettyServer {
    public static void main(String[] args) {
        try {
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workGroup = new NioEventLoopGroup();

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ProxyInitializer());

            ChannelFuture future = serverBootstrap.bind(7100).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
