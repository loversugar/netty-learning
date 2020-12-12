package com.totti.chat.server.codec;

import java.util.List;

import com.totti.chat.server.info.Message;
import com.totti.chat.util.MessageCodec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class ChatProtocolDecode extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        Message message = MessageCodec.decode(msg);
        out.add(message);
    }
}
