package com.totti.chat.server.dispactcher;

import java.util.concurrent.ConcurrentLinkedQueue;

import io.netty.channel.Channel;

public class FriendList {
    public static ConcurrentLinkedQueue<Channel> concurrentLinkedQueue = new ConcurrentLinkedQueue();

    private FriendList() {}
}
