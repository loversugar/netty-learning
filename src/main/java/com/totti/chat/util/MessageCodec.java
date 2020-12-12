package com.totti.chat.util;

import java.nio.charset.Charset;

import com.google.gson.Gson;
import com.totti.chat.server.info.ChatInfo;
import com.totti.chat.server.info.Message;
import com.totti.chat.server.info.Transport;

import io.netty.buffer.ByteBuf;

public class MessageCodec {
    private final static Gson gson = new Gson();

    private MessageCodec() {}

    public static Message decode(ByteBuf byteBuf) {
        Transport transport = new Transport(byteBuf.readInt(), byteBuf.readInt(), byteBuf.readLong());
        ChatInfo chatInfo = gson.fromJson(byteBuf.toString(Charset.forName("UTF-8")), ChatInfo.class);

        return new Message(transport, chatInfo);
    }

    public static void encode(Message message, ByteBuf byteBuf) {
        Transport transport = message.getTransport();
        ChatInfo chatInfo = message.getChatInfo();

        byteBuf.writeInt(transport.getMagic());
        byteBuf.writeInt(transport.getOpCode());
        byteBuf.writeLong(transport.getStreamId());

        byteBuf.writeBytes(gson.toJson(chatInfo).getBytes());
    }
}
