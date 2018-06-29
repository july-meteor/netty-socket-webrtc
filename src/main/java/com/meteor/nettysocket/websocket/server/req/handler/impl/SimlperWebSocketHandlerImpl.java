package com.meteor.nettysocket.websocket.server.req.handler.impl;

import com.alibaba.fastjson.JSON;
import com.meteor.nettysocket.websocket.model.ResultModel;
import com.meteor.nettysocket.websocket.protocol.ProtocolEnum;
import com.meteor.nettysocket.websocket.server.req.handler.WebSocketHandler;
import com.meteor.nettysocket.websocket.service.TextHandler;
import com.meteor.nettysocket.websocket.service.impl.ISocketService;
import com.meteor.nettysocket.websocket.service.impl.TextHandlerImpl;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@SuppressWarnings("all")
@Component
@Qualifier("SimlperWebSocketHandlerImpl")
@ChannelHandler.Sharable
public class SimlperWebSocketHandlerImpl extends WebSocketHandler {

    private static  final Logger LOG =LoggerFactory.getLogger(SimlperWebSocketHandlerImpl.class);


    @Override
    protected void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {

        /**
         * 以下是处理websocket的基本方法
         */
        //判断是否是关闭链接的命令
        if (frame instanceof CloseWebSocketFrame){
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }else if (frame instanceof  PingWebSocketFrame){ // 判断是否是Ping消息
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        /**
         * 判断消息类型
         */
        String result =null;
        //文本类型消息
        if (frame instanceof TextWebSocketFrame){
            String msg = ((TextWebSocketFrame) frame).text();
            TextHandlerImpl textHandler = new TextHandlerImpl();
            try {
                result=  JSON.toJSONString(textHandler.decoding(ctx,msg));
            }catch (Exception e){
                e.printStackTrace();
            }
        }else  if (frame instanceof  BinaryWebSocketFrame){//二进制消息 这个是我们主要的类型
            System.err.println("二进制数据接收");
            ByteBuf buf = frame.content();

            //向所有人发送语音消息
            ISocketService.userMap.forEach((reqId, callBack) -> {
            try {

                buf.retain();
                callBack.sendAMR(buf);
            }catch (Exception e){
                e.printStackTrace();
            }

            });


            //返回文本消息
            ResultModel temp =new ResultModel();
            temp.setFlag(true);
            temp.setMessage("发送成功！");
            result = JSON.toJSONString(temp);

        }
        sendWebSocket(result);

    }

    @Override
    protected void sendWebSocket(String msg) throws Exception {
        System.err.println(msg);
        if (this.handshaker == null || this.ctx == null || this.ctx.isRemoved()) {
            throw new Exception("尚未握手成功，无法向客户端发送WebSocket消息");
        }
        this.ctx.channel().write(new TextWebSocketFrame(msg));
        this.ctx.flush();
    }
}
