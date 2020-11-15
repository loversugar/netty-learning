package com.totti.order.common;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.totti.order.common.util.JsonUtils;

import io.netty.buffer.ByteBuf;

public abstract class Message<T extends MessageBody> {
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    private MessageHeader header;
    private T body;

    /**
     * 编码
     * 
     * @param buf
     */
    public void encode(ByteBuf buf) {
        buf.writeInt(header.getVersion());
        buf.writeInt(header.getOpCode());
        buf.writeLong(header.getRequestId());
        // body
        buf.writeBytes(JsonUtils.toJson(body).getBytes(UTF_8));
    }

    /**
     * 解码
     * 
     * @param msg
     */
    public void decode(ByteBuf msg) {
        int version = msg.readInt();
        int opCode = msg.readInt();
        long requestId = msg.readLong();

        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setVersion(version);
        messageHeader.setOpCode(opCode);
        messageHeader.setRequestId(requestId);

        Class<T> clz = getMessageBodyDecodeClass(opCode);
        T obj = JsonUtils.parseObject(msg.toString(UTF_8), clz);

        this.header = messageHeader;
        this.body = obj;
    }

    public MessageHeader getHeader() {
        return header;
    }

    public void setHeader(MessageHeader header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    protected abstract Class<T> getMessageBodyDecodeClass(int opCode);
}
