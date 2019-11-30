package com.totti.netty.first;

import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResultProvider;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;

import java.nio.charset.StandardCharsets;

public class FirstNettyServerHandler1 extends SimpleChannelInboundHandler {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        CompositeByteBuf content = ctx.alloc().compositeBuffer(1024);
        System.out.println(content.retain().toString(StandardCharsets.UTF_8));
    }
}
