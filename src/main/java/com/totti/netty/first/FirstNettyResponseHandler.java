package com.totti.netty.first;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.rtsp.RtspHeaderNames.CONTENT_LENGTH;

public class FirstNettyResponseHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        HttpVersion version = HttpVersion.HTTP_1_1;
        ByteBuf content = Unpooled.copiedBuffer("success".getBytes());
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(version, HttpResponseStatus.OK, content);
        httpResponse.headers().set(CONTENT_TYPE, "text/plain");
        httpResponse.headers().setInt(CONTENT_LENGTH, httpResponse.content().readableBytes());

        ctx.writeAndFlush(httpResponse);
    }
}
