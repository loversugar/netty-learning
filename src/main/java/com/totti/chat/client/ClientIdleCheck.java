package com.totti.chat.client;

import java.util.concurrent.TimeUnit;

import io.netty.handler.timeout.IdleStateHandler;

public class ClientIdleCheck extends IdleStateHandler {
    public ClientIdleCheck() {
        super(0, 5, 0, TimeUnit.SECONDS);
    }
}
