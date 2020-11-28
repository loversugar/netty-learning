package com.totti.order.client.codec;

import java.util.List;

import com.totti.order.common.RequestMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class OrderProtocolEncoder extends MessageToMessageEncoder<RequestMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RequestMessage requestMessage, List<Object> list)
        throws Exception {
        //
        // ByteBuf buffer1 = ByteBufAllocator.DEFAULT.buffer();
        ByteBuf buffer = channelHandlerContext.alloc().buffer();
        requestMessage.encode(buffer);

        list.add(buffer);
    }
}
