package com.totti.order.common.keepAlive;

import com.totti.order.common.Operation;
import com.totti.order.common.OperationResult;

public class KeepaliveOperation extends Operation {
    private long time;

    public KeepaliveOperation() {
        this.time = System.nanoTime();
    }

    @Override
    public OperationResult execute() {
        KeepAliveOperationResult result = new KeepAliveOperationResult(time);
        return result;
    }

}
