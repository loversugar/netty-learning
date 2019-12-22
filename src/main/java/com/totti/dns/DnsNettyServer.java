package com.totti.dns;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.dns.DatagramDnsQueryDecoder;
import io.netty.handler.codec.dns.DatagramDnsResponseEncoder;

public class DnsNettyServer {
    public static void main(String[] args) {
        try {
            EventLoopGroup workGroup = new NioEventLoopGroup();

            Bootstrap serverBootstrap = new Bootstrap();
            serverBootstrap.group(workGroup)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel ch) throws Exception {
                            ch.pipeline().addLast(new DatagramDnsQueryDecoder());
                            ch.pipeline().addLast(new DatagramDnsResponseEncoder());
                            ch.pipeline().addLast(new DnsParser());
                        }
                    });

            ChannelFuture future = serverBootstrap.bind(7100).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
