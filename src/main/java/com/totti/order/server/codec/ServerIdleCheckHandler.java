package com.totti.order.server.codec;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

public class ServerIdleCheckHandler extends IdleStateHandler {
    private static Logger logger = Logger.getLogger(ServerIdleCheckHandler.class);

    public ServerIdleCheckHandler() {
        super(10, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        if (evt == IdleStateEvent.READER_IDLE_STATE_EVENT) {
            logger.info("idle check happen, so close connection");
            ctx.close();
            return;
        }

        super.channelIdle(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("ServerIdleCheckHandler#channelActive.....");
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("ServerIdleCheckHandler#channelRead.....");
        super.channelRead(ctx, msg);
    }
}
