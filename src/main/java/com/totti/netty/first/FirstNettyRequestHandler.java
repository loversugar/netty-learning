package com.totti.netty.first;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class FirstNettyRequestHandler extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // input full request message
        System.out.println(in.retain().toString(StandardCharsets.UTF_8));

        // TODO write your business
        
        ctx.writeAndFlush("get all message");
    }
}
