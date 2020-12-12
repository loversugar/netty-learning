package com.totti.chat.server.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;

public class AuthHandler extends ByteToMessageDecoder {

    private static boolean isChatProtocol(int magic) {
        return magic == 'G';
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 1) {
            return;
        }

        final int magic = in.getInt(0);
        if (isChatProtocol(magic)) {
            ChannelPipeline pipeline = ctx.pipeline();

            pipeline.addLast(new ChatProtocolDecode());
            pipeline.addLast(new ChatProtocolEncode());
            pipeline.addLast(new ChatHandler());

            pipeline.remove(this);
        } else {
            ctx.writeAndFlush("wrong protocol");
            ctx.channel().close();
        }
    }
}
