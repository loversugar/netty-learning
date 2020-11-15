package com.totti.order.common;

public class ResponseMessage extends Message<OperationResult> {
    @Override
    protected Class getMessageBodyDecodeClass(int opCode) {
        return OperationType.fromOpCode(opCode).getOperationResultClazz();
    }
}
