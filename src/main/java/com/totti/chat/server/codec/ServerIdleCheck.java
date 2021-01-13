package com.totti.chat.server.codec;

import java.util.concurrent.TimeUnit;

import io.netty.handler.timeout.IdleStateHandler;

public class ServerIdleCheck extends IdleStateHandler {
    public ServerIdleCheck() {
        super(10, 0, 0, TimeUnit.SECONDS);
    }
}
