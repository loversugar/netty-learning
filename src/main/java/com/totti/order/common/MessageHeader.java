package com.totti.order.common;

public class MessageHeader {
    /**
     * 版本号
     */
    private int version = 1;

    /**
     * 业务类型
     */
    private int opCode;

    private Long requestId;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getOpCode() {
        return opCode;
    }

    public void setOpCode(int opCode) {
        this.opCode = opCode;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
}
