package com.totti.order.common.order;

import com.totti.order.common.Operation;
import com.totti.order.common.OperationResult;

public class OrderOperation extends Operation {
    private int tableId;
    private String dish;

    public OrderOperation(int tableId, String dish) {
        this.tableId = tableId;
        this.dish = dish;
    }

    @Override
    public OperationResult execute() {
        System.out.println("");
        OrderOperationResult result = new OrderOperationResult(tableId, dish, true);
        return result;
    }
}
