package com.totti.chat.codec;

import org.apache.log4j.Logger;

import com.totti.chat.server.info.Message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger logger = Logger.getLogger(ChatHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

        logger.info("client receive...." + msg.getChatInfo().getUsername());
        logger.info("client receive...." + msg.getChatInfo().getInfo());
    }
}
