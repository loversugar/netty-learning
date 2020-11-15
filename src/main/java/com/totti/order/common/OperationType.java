package com.totti.order.common;

import com.totti.order.common.auth.AuthOperation;
import com.totti.order.common.auth.AuthOperationResult;
import com.totti.order.common.keepAlive.KeepAliveOperationResult;
import com.totti.order.common.keepAlive.KeepaliveOperation;
import com.totti.order.common.order.OrderOperation;
import com.totti.order.common.order.OrderOperationResult;

public enum OperationType {
    AUTH(1, AuthOperation.class, AuthOperationResult.class),
    KEEPALIVE(2, KeepaliveOperation.class, KeepAliveOperationResult.class),
    ORDER(3, OrderOperation.class, OrderOperationResult.class);

    private int opCode;

    private Class<? extends Operation> operationClazz;

    private Class<? extends OperationResult> operationResultClazz;

    OperationType(int opCode, Class<? extends Operation> operationClazz,
        Class<? extends OperationResult> operationResultClazz) {
        this.opCode = opCode;
        this.operationClazz = operationClazz;
        this.operationResultClazz = operationResultClazz;
    }

    public static Class<? extends OperationResult> ofCode(int opCode) {
        for (OperationType operationType : OperationType.values()) {
            if (operationType.getOpCode() == opCode) {
                return operationType.operationResultClazz;
            }
        }
        return null;
    }

    public static OperationType fromOpCode(int opCode) {
        for (OperationType operationType : OperationType.values()) {
            if (operationType.getOpCode() == opCode) {
                return operationType;
            }
        }

        return null;
    }

    public static OperationType fromOperation(Operation operation) {
        for (OperationType operationType : OperationType.values()) {
            if (operationType.operationClazz == operation.getClass()) {
                return operationType;
            }
        }

        return null;
    }

    public int getOpCode() {
        return opCode;
    }

    public Class<? extends Operation> getOperationClazz() {
        return operationClazz;
    }

    public Class<? extends OperationResult> getOperationResultClazz() {
        return operationResultClazz;
    }
}
