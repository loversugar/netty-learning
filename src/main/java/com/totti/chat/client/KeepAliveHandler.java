package com.totti.chat.client;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

public class KeepAliveHandler extends ChannelDuplexHandler {
    private static final Logger logger = Logger.getLogger(KeepAliveHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent idleStateEvent = IdleStateEvent.class.cast(evt);
        logger.info("keepalive..." + idleStateEvent);

        if (idleStateEvent == IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT) {
            logger.info("please keep alive");
        }

        super.userEventTriggered(ctx, evt);
    }
}
