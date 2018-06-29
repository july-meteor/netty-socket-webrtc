package com.meteor.nettysocket.websocket.model;

import io.netty.buffer.ByteBuf;

/**
 * 基础vo
 */

public class BaseModel {


    /**
     * 请求的id
     */
    private String requestId;
    /**
     * 协议号
     */
    private int protocolId;
    /**
     * 二进制数据
     */
    private Object data;
    /**
     * 文本消息
     */
    private String message;


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(int protocolId) {
        this.protocolId = protocolId;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
