package com.meteor.nettysocket.websocket.model;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * 结果模型
 */
public class ResultModel extends  BaseModel {


    /**
     * 返回的结果
     */
    private boolean flag = false;

    /**
     * 在线人员列表,请求地址和姓名
     */
    private Map<String, String> hadOnline = new HashMap<String, String>(); // <requestId, name>


    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Map<String, String> getHadOnline() {
        return hadOnline;
    }

    public void setHadOnline(Map<String, String> hadOnline) {
        this.hadOnline = hadOnline;
    }


    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
