package com.totti.order.common.util;

import java.util.concurrent.atomic.AtomicLong;

public class Idutil {
    private static final AtomicLong IDX = new AtomicLong();

    private Idutil() {

    }

    public static long nextId() {
        return IDX.incrementAndGet();
    }
}
