package com.totti.order.server;

import java.util.concurrent.ExecutionException;

import com.totti.order.server.codec.MetricHandler;
import com.totti.order.server.codec.OrderFrameDecoder;
import com.totti.order.server.codec.OrderFrameEncoder;
import com.totti.order.server.codec.OrderProtocolDecoder;
import com.totti.order.server.codec.OrderProtocolEncoder;
import com.totti.order.server.codec.ServerIdleCheckHandler;
import com.totti.order.server.codec.handler.AuthHandler;
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
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;

public class Server {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("boss"));
        EventLoopGroup workerGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("worker"));
        EventLoopGroup trafficShapingGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("trafficShaping"));

        MetricHandler metricHandler = new MetricHandler();
        GlobalTrafficShapingHandler globalTrafficShapingHandler =
            new GlobalTrafficShapingHandler(trafficShapingGroup, 10 * 1024 * 1024, 10 * 1024 * 1024);

        UnorderedThreadPoolEventExecutor unorderedThreadPoolEventExecutor =
            new UnorderedThreadPoolEventExecutor(10, new DefaultThreadFactory("bussiness"));

        AuthHandler authHandler = new AuthHandler();

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
                pipeline.addLast("tsHandler", globalTrafficShapingHandler);
                pipeline.addLast("metricHandler", metricHandler);
                pipeline.addLast("idleCheck", new ServerIdleCheckHandler());

                pipeline.addLast("frameDecoder", new OrderFrameDecoder());
                pipeline.addLast("frameEncoder", new OrderFrameEncoder());

                pipeline.addLast(new OrderProtocolEncoder());
                pipeline.addLast(new OrderProtocolDecoder());

                pipeline.addLast(new LoggingHandler(LogLevel.INFO));

                pipeline.addLast(new FlushConsolidationHandler(10, true));

                pipeline.addLast("authHandler", authHandler);

                // 因为该线程池的next()方法返回this，但是NioEventLoopGroup的next()方法返回的是chooser.next()
                pipeline.addLast(unorderedThreadPoolEventExecutor, new OrderServerProcessHandler());
            }
        });

        ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();
        channelFuture.channel().closeFuture().get();
    }
}
