package com.totti.chat.client;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import com.totti.chat.codec.ChatHandler;
import com.totti.chat.server.codec.ChatFrameDecode;
import com.totti.chat.server.codec.ChatFrameEncode;
import com.totti.chat.server.codec.ChatProtocolDecode;
import com.totti.chat.server.codec.ChatProtocolEncode;
import com.totti.chat.server.info.ChatInfo;
import com.totti.chat.server.info.Message;
import com.totti.chat.server.info.Transport;
import com.totti.chat.util.IdUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ChatClient {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1000);

        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast("decode", new ChatFrameDecode());
                pipeline.addLast("encode", new ChatFrameEncode());
                pipeline.addLast(new ChatProtocolDecode());
                pipeline.addLast(new ChatProtocolEncode());
                pipeline.addLast(new ChatHandler());
            }
        });

        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9090);

        channelFuture.sync();

        Transport transport = new Transport('G', 1, IdUtil.nextId());

        Scanner scanner = new Scanner(System.in);
        System.out.println("输入用户名和信息，以逗号间隔: ");
        while (scanner.hasNext()) {
            String[] inputString = scanner.nextLine().split(",");
            ChatInfo chatInfo = new ChatInfo(inputString[0], inputString[1]);
            Message message = new Message(transport, chatInfo);
            channelFuture.channel().writeAndFlush(message);
        }

        channelFuture.channel().closeFuture().get();
    }
}
