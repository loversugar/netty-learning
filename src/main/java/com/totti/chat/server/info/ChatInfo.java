package com.totti.chat.server.info;

public class ChatInfo {
    private String username;

    private String info;

    public ChatInfo() {}

    public ChatInfo(String username, String info) {
        this.username = username;
        this.info = info;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
