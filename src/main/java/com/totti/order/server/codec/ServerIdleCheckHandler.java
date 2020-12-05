package com.totti.order.server.codec;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.totti.order.common.order.OrderOperation;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

public class ServerIdleCheckHandler extends IdleStateHandler {
    private static Logger logger = Logger.getLogger(OrderOperation.class);

    public ServerIdleCheckHandler() {
        super(10, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT) {
            logger.info("idle check happen, so close connection");
            ctx.close();
            return;
        }

        super.channelIdle(ctx, evt);
    }
}
