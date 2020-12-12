package com.totti.chat.server.info;

public class Transport {
    private int magic;

    private int opCode;

    private long streamId;

    public Transport() {}

    public Transport(int magic, int opCode, long streamId) {
        this.magic = magic;
        this.opCode = opCode;
        this.streamId = streamId;
    }

    public int getMagic() {
        return magic;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }

    public int getOpCode() {
        return opCode;
    }

    public void setOpCode(int opCode) {
        this.opCode = opCode;
    }

    public long getStreamId() {
        return streamId;
    }

    public void setStreamId(long streamId) {
        this.streamId = streamId;
    }
}
