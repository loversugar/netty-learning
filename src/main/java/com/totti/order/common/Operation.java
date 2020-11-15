package com.totti.order.common;

public abstract class Operation extends MessageBody {
    public abstract OperationResult execute();
}
