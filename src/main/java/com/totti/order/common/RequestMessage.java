package com.totti.order.common;

public class RequestMessage extends Message<Operation> {
    public RequestMessage() {

    }

    public RequestMessage(long requestId, Operation operation) {
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setRequestId(requestId);
        messageHeader.setOpCode(OperationType.fromOperation(operation).getOpCode());
    }

    @Override
    protected Class getMessageBodyDecodeClass(int opCode) {
        return OperationType.fromOpCode(opCode).getOperationClazz();
    }
}
