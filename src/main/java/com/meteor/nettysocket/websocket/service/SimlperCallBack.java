package com.meteor.nettysocket.websocket.service;

import com.meteor.nettysocket.websocket.model.ResultModel;
import io.netty.buffer.ByteBuf;

public interface SimlperCallBack {
    /**
     * 返回信息
     * @param request
     * @throws Exception
     */
    void  sendMsg(ResultModel request)throws Exception;

    void  sendAMR(ByteBuf byteBuf)throws Exception;

}
