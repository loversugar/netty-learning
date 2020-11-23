package com.totti.order.client.codec;

import java.util.List;

import com.totti.order.common.Operation;
import com.totti.order.common.RequestMessage;
import com.totti.order.common.util.IdUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class OperationToRequestMessageEncoder extends MessageToMessageEncoder<Operation> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Operation operation, List<Object> list)
        throws Exception {

        RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), operation);
        list.add(requestMessage);
    }
}
