package com.totti.chat.server.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class ChatFrameDecode extends LengthFieldBasedFrameDecoder {
    public ChatFrameDecode(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment,
        int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
