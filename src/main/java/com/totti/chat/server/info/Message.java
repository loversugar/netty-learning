package com.totti.chat.server.info;

public class Message {
    private Transport transport;

    private ChatInfo chatInfo;

    public Message() {}

    public Message(Transport transport, ChatInfo chatInfo) {
        this.transport = transport;
        this.chatInfo = chatInfo;
    }

    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public ChatInfo getChatInfo() {
        return chatInfo;
    }

    public void setChatInfo(ChatInfo chatInfo) {
        this.chatInfo = chatInfo;
    }
}
