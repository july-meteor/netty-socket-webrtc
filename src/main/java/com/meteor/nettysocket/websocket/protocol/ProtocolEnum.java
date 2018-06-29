package com.meteor.nettysocket.websocket.protocol;

/**
 *  一些协议枚举
 */
public enum  ProtocolEnum {

    login(1000,"用户登录协议"),
    cur_online(1001, "客户端上线请求"),
    send_message(1002, "客户端发送'发送消息'请求"),
    send_AMR(1003, "服务端发送'接收消息'请求"),

    login_out(1004, "客户端下线请求");

    public String note;
    public Integer code;

    private ProtocolEnum(Integer code, String note) {
        this.note = note;
        this.code = code;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
