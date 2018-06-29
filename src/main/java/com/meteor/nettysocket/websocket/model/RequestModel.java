package com.meteor.nettysocket.websocket.model;

import io.netty.buffer.ByteBuf;

/**
 *  请求的模型
 */
public class RequestModel extends  BaseModel {


    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
