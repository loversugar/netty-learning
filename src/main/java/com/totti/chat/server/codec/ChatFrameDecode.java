package com.totti.chat.server.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class ChatFrameDecode extends LengthFieldBasedFrameDecoder {
    public ChatFrameDecode() {
        super(Integer.MAX_VALUE, 0, 2, 0, 2);
    }

}
