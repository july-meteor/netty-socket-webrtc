package com.meteor.nettysocket.websocket.service;

import com.alibaba.fastjson.JSON;
import com.meteor.nettysocket.websocket.model.RequestModel;
import com.meteor.nettysocket.websocket.model.ResultModel;
import com.meteor.nettysocket.websocket.protocol.ProtocolEnum;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;


/**
 * 文本消息处理
 */

public abstract class TextHandler {



 public   ResultModel decoding(ChannelHandlerContext ctx,String msg){
        RequestModel request = JSON.parseObject(msg,RequestModel.class);
        //结果模型
        //上线请求
        if (ProtocolEnum.login.code.intValue() == request.getProtocolId()){
         return register(ctx,request);

        }else if(ProtocolEnum.send_message.code.intValue()  == request.getProtocolId()){
            return  sendMessage(request);

        }else if(ProtocolEnum.login_out.code.intValue()  == request.getProtocolId()){
            return   loginOut(request);

        }
        return  null;

    }


    /**
     * 注册事件
     * @param req
     * @return
     */
    public  abstract ResultModel register(ChannelHandlerContext ctx, RequestModel req);

    public  abstract ResultModel loginOut(RequestModel req);

    public  abstract ResultModel sendMessage(RequestModel req);

//    public abstract ResultModel sendAMR(RequestModel req);

}
