package com.totti.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

public class HttpServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("boss"));
        EventLoopGroup workerGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("worker"));

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
            // .option(ChannelOption.SO_BACKLOG, 1024).childOption(ChannelOption.TCP_NODELAY, true)
            .handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                    pipeline.addLast("decode", new HttpRequestDecoder());
                    pipeline.addLast("encode", new HttpResponseEncoder());
                    pipeline.addLast("aggregator", new HttpObjectAggregator(512 * 1024));
                    pipeline.addLast("handler", new HttpServerHandler());
                }
            });

        ChannelFuture channelFuture = serverBootstrap.bind(9090).sync();
        channelFuture.channel().closeFuture().sync();
    }
}
