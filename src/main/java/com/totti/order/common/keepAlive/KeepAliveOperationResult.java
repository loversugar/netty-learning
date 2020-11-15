package com.totti.order.common.keepAlive;

import com.totti.order.common.OperationResult;

public class KeepAliveOperationResult extends OperationResult {
    private final long time;

    public KeepAliveOperationResult(long time) {
        this.time = time;
    }
}
