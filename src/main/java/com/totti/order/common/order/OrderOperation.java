package com.totti.order.common.order;

import org.apache.log4j.Logger;

import com.totti.order.common.Operation;
import com.totti.order.common.OperationResult;

public class OrderOperation extends Operation {
    private static Logger logger = Logger.getLogger(OrderOperation.class);

    private int tableId;
    private String dish;

    public OrderOperation(int tableId, String dish) {
        this.tableId = tableId;
        this.dish = dish;
    }

    @Override
    public OperationResult execute() {
        logger.info("executing.....");
        OrderOperationResult result = new OrderOperationResult(tableId, dish, true);
        return result;
    }
}
