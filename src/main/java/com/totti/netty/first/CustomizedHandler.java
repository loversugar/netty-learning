package com.totti.netty.first;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class CustomizedHandler extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println(in.retain().toString(StandardCharsets.UTF_8));
        ctx.fireChannelActive();

//        StringBuilder stringBuilder = new StringBuilder();
       // for (int begin = in.readerIndex(); begin < in.writerIndex(); begin++) {
       //     char nextByte = (char) (in.getByte(begin) & 0xFF);
//     //       char nextByte = (char) in.getByte(begin);
       //     if (nextByte == '{') {
       //         System.out.println(nextByte);
       //     }
       //     stringBuilder.append(nextByte);
       // }

       // System.out.println(stringBuilder);
    }

    public static void main(String[] args) {
        byte a = -127;
        char c = (char) (a & 0xFF);
        System.out.println(c);
    }
}
