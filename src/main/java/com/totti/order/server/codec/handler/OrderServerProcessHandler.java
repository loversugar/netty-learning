package com.totti.order.server.codec.handler;

import com.totti.order.common.Operation;
import com.totti.order.common.OperationResult;
import com.totti.order.common.RequestMessage;
import com.totti.order.common.ResponseMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 使用simpleInboundHandler帮助释放ByteBuf
 */
public class OrderServerProcessHandler extends SimpleChannelInboundHandler<RequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) throws Exception {
        Operation operation = msg.getMessageBody();
        OperationResult operationResult = operation.execute();

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessageHeader(msg.getMessageHeader());
        responseMessage.setMessageBody(operationResult);

        ctx.writeAndFlush(responseMessage);
    }
}
