package com.totti.netty.first;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class CustomizedHandler extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        for (int begin = in.readerIndex(); begin < in.writerIndex(); begin++) {
            char nextByte = (char) in.getByte(begin);
            stringBuilder.append(nextByte);
        }
        System.out.println(stringBuilder);
    }
}

