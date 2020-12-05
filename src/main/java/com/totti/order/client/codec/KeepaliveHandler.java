package com.totti.order.client.codec;

import org.apache.log4j.Logger;

import com.totti.order.common.RequestMessage;
import com.totti.order.common.keepAlive.KeepaliveOperation;
import com.totti.order.common.order.OrderOperation;
import com.totti.order.common.util.IdUtil;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

@ChannelHandler.Sharable
public class KeepaliveHandler extends ChannelDuplexHandler {
    private static Logger logger = Logger.getLogger(OrderOperation.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT) {
            logger.info("write idle happen, so need to send keepalive to keep connection");
            KeepaliveOperation keepaliveOperation = new KeepaliveOperation();
            RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), keepaliveOperation);
            ctx.writeAndFlush(requestMessage);
        }

        super.userEventTriggered(ctx, evt);
    }
}
