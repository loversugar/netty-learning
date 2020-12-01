package com.totti.order.server;

import java.util.concurrent.ExecutionException;

import com.totti.order.server.codec.MetricHandler;
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
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;

public class Server {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("boss"));
        EventLoopGroup workerGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("worker"));

        MetricHandler metricHandler = new MetricHandler();

        UnorderedThreadPoolEventExecutor unorderedThreadPoolEventExecutor =
            new UnorderedThreadPoolEventExecutor(10, new DefaultThreadFactory("bussiness"));

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.group(bossGroup, workerGroup);
        serverBootstrap.handler(new LoggingHandler(LogLevel.DEBUG));
        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);

        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("metricHandler", metricHandler);
                pipeline.addLast("frameDecoder", new OrderFrameDecoder());
                pipeline.addLast("frameEncoder", new OrderFrameEncoder());
                pipeline.addLast(new OrderProtocolEncoder());
                pipeline.addLast(new OrderProtocolDecoder());
                pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                pipeline.addLast(unorderedThreadPoolEventExecutor, new OrderServerProcessHandler());
            }
        });

        ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();
        channelFuture.channel().closeFuture().get();
    }
}
