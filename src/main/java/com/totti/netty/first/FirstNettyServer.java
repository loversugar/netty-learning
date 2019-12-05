package com.totti.netty.first;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class FirstNettyServer {
    public static void main(String[] args) {
        // 1.first need two event loop - boss, worker
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 2. create start-up class, for the ease of to manage channel
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 3.
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // add chain handler
                    ch.pipeline().addLast(new CustomizedHandler1());
                    ch.pipeline().addLast(new CustomizedResponseHandler());
                    ch.pipeline().addLast(new CustomizedHandler2());

//                    ch.pipeline().addLast("handler", new FirstNettyServerHandler());
//                    ch.pipeline().addLast("handler", new HttpServerCodec());
//                    ch.pipeline().addLast("handler1", new HttpObjectAggregator(Short.MAX_VALUE));
//                    ch.pipeline().addLast("handler2", new FirstNettyServerHandler1());

                }
            });

            // I found there are two options, so why?
            // but childOption is that after acceptor accepted the channel
            // For now I think the method of option() is use for bossGroup
            // and the method of childOption is use for workerGroup
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = serverBootstrap.bind(7000).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
