package com.totti.order.client;

import java.util.concurrent.ExecutionException;

import com.totti.order.client.codec.ClientIdlCheckHandler;
import com.totti.order.client.codec.KeepaliveHandler;
import com.totti.order.client.codec.OrderFrameDecoder;
import com.totti.order.client.codec.OrderFrameEncoder;
import com.totti.order.client.codec.OrderProtocolDecoder;
import com.totti.order.client.codec.OrderProtocolEncoder;
import com.totti.order.common.RequestMessage;
import com.totti.order.common.order.OrderOperation;
import com.totti.order.common.util.IdUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class Client {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1000);

        KeepaliveHandler keepaliveHandler = new KeepaliveHandler();

        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new ClientIdlCheckHandler());

                pipeline.addLast(new OrderFrameDecoder());
                pipeline.addLast(new OrderFrameEncoder());
                pipeline.addLast(new OrderProtocolEncoder());
                pipeline.addLast(new OrderProtocolDecoder());

                pipeline.addLast(keepaliveHandler);
                pipeline.addLast(new LoggingHandler(LogLevel.INFO));
            }
        });

        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090);

        channelFuture.sync();

        RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), new OrderOperation(1001, "tudou"));
        channelFuture.channel().writeAndFlush(requestMessage);

        channelFuture.channel().closeFuture().get();
    }
}
