package com.totti.netty;

import io.netty.bootstrap.Bootstrap;

public class BootstrapServer {
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.connect();
    }
}
