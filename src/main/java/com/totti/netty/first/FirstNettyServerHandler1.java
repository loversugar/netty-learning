package com.totti.netty.first;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequestDecoder;
public class FirstNettyServerHandler1 extends HttpRequestDecoder{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    }
}
