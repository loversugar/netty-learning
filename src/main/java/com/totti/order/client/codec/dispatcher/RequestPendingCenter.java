package com.totti.order.client.codec.dispatcher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.totti.order.common.OperationResult;

public class RequestPendingCenter {
    private Map<Long, OperationResultFuture> map = new ConcurrentHashMap<>();

    public void add(Long streamId, OperationResultFuture future) {
        map.put(streamId, future);
    }

    public void set(Long streamId, OperationResult operationResult) {
        OperationResultFuture future = map.get(streamId);
        if (future != null) {
            future.setSuccess(operationResult);
            map.remove(streamId);
        }
    }
}
