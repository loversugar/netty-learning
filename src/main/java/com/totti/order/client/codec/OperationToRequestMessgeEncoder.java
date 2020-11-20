package com.totti.order.client.codec;

import java.util.List;

import com.totti.order.common.Operation;
import com.totti.order.common.RequestMessage;
import com.totti.order.common.util.Idutil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class OperationToRequestMessgeEncoder extends MessageToMessageEncoder<Operation> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Operation operation, List<Object> list)
        throws Exception {

        RequestMessage requestMessage = new RequestMessage(Idutil.nextId(), operation);
        list.add(requestMessage);
    }
}