package com.totti.order.client;

import java.util.concurrent.ExecutionException;

import com.totti.order.client.codec.OperationToRequestMessgeEncoder;
import com.totti.order.client.codec.OrderFrameDecoder;
import com.totti.order.client.codec.OrderFrameEncoder;
import com.totti.order.client.codec.OrderProtocolDecoder;
import com.totti.order.client.codec.OrderProtocolEncoder;
import com.totti.order.client.codec.dispatcher.OperationResultFuture;
import com.totti.order.client.codec.dispatcher.RequestPendingCenter;
import com.totti.order.client.codec.dispatcher.ResponseDispatcherHandler;
import com.totti.order.common.OperationResult;
import com.totti.order.common.RequestMessage;
import com.totti.order.common.order.OrderOperation;
import com.totti.order.common.util.IdUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ClientV2 {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);

        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            bootstrap.group(group);
            RequestPendingCenter requestPendingCenter = new RequestPendingCenter();

            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new OrderFrameDecoder());
                    pipeline.addLast(new OrderFrameEncoder());

                    pipeline.addLast(new OrderProtocolEncoder());
                    pipeline.addLast(new OrderProtocolDecoder());

                    pipeline.addLast(new ResponseDispatcherHandler(requestPendingCenter));

                    pipeline.addLast(new OperationToRequestMessgeEncoder());

                    pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                }
            });

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090);

            channelFuture.sync();

            long streamId = IdUtil.nextId();

            RequestMessage requestMessage = new RequestMessage(streamId, new OrderOperation(1001, "tudou"));

            OperationResultFuture operationResultFuture = new OperationResultFuture();

            requestPendingCenter.add(streamId, operationResultFuture);

            channelFuture.channel().writeAndFlush(requestMessage);

            OperationResult operationResult = operationResultFuture.get();

            System.out.println(operationResult);

            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
