package com.totti.chat.server.codec;

import java.util.List;

import com.totti.chat.server.info.Message;
import com.totti.chat.util.MessageCodec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class ChatProtocolEncode extends MessageToMessageEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer();
        MessageCodec.encode(msg, buffer);

        out.add(buffer);
    }
}
