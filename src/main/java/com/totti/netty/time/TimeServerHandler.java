package com.totti.netty.time;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//       // Discard the received data silently
////        ((ByteBuf) msg).release();
//       // ByteBuf in = (ByteBuf) msg;
//       // try {
//       //     while (in.isReadable()) {
//       //         System.out.println((char) in.readByte());
//       //         System.out.flush();
//       //     }
//       // } finally {
//       //     ReferenceCountUtil.release(msg);
//       // }
//        ctx.write(msg);
//        ctx.flush();
//    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final ByteBuf time = ctx.alloc().buffer(4);
        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
        final ChannelFuture f = ctx.writeAndFlush(time);
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
               assert f == future;
               ctx.close();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       // Close the connection when an exception is raised
        cause.printStackTrace();
        ctx.close();
    }
}
