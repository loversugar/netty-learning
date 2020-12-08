package com.totti.order.server.codec.handler;

import org.apache.log4j.Logger;

import com.totti.order.common.Operation;
import com.totti.order.common.RequestMessage;
import com.totti.order.common.auth.AuthOperation;
import com.totti.order.common.auth.AuthOperationResult;
import com.totti.order.server.codec.MetricHandler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class AuthHandler extends SimpleChannelInboundHandler<RequestMessage> {
    private static final Logger logger = Logger.getLogger(MetricHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) throws Exception {
        try {
            Operation operation = msg.getMessageBody();

            if (operation instanceof AuthOperation) {
                AuthOperation authOperation = AuthOperation.class.cast(operation);
                AuthOperationResult authOperationResult = authOperation.execute();
                if (authOperationResult.isPassAuth()) {
                    logger.info("pass auth");
                } else {
                    logger.error("fail to auth");
                    ctx.close();
                }
            } else {
                logger.error("fail to auth");
                ctx.close();
            }
        } catch (Exception e) {
            logger.error("fail to auth");
            ctx.close();
        } finally {
            ctx.pipeline().remove(this);
        }

    }
}
