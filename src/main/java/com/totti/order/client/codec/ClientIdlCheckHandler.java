package com.totti.order.client.codec;

import java.util.concurrent.TimeUnit;

import io.netty.handler.timeout.IdleStateHandler;

public class ClientIdlCheckHandler extends IdleStateHandler {
    public ClientIdlCheckHandler() {
        super(0, 5, 0, TimeUnit.SECONDS);
    }
}
