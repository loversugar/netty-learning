package com.totti.chat.server.codec;

import io.netty.handler.codec.LengthFieldPrepender;

public class ChatFrameEncode extends LengthFieldPrepender {

    public ChatFrameEncode() {
        super(2);
    }
}
