package com.totti.order.server;

import java.util.concurrent.ExecutionException;

import com.totti.order.server.codec.OrderFrameDecoder;
import com.totti.order.server.codec.OrderFrameEncoder;
import com.totti.order.server.codec.OrderProtocolDecoder;
import com.totti.order.server.codec.OrderProtocolEncoder;
import com.totti.order.server.codec.handler.OrderServerProcessHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class Server {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.group(new NioEventLoopGroup());
        serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);

        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new OrderFrameDecoder());
                pipeline.addLast(new OrderFrameEncoder());
                pipeline.addLast(new OrderProtocolEncoder());
                pipeline.addLast(new OrderProtocolDecoder());
                pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                pipeline.addLast(new OrderServerProcessHandler());
            }
        });

        ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();
        channelFuture.channel().closeFuture().get();
    }
}
