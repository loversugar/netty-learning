package com.totti.chat.server.codec;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

public class IdleCheckHandler extends ChannelDuplexHandler {
    private static final Logger logger = Logger.getLogger(IdleCheckHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        logger.info("IdleCheckHandler.....");
        if (evt == IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT) {
            logger.info("didn't get any info from client during 10 seconds, so close this connection");
            ctx.close();
            return;
        }

        super.userEventTriggered(ctx, evt);
    }
}
