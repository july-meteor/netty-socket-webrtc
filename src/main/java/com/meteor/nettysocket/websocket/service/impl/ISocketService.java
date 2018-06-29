package com.meteor.nettysocket.websocket.service.impl;

import com.alibaba.fastjson.JSON;
import com.meteor.nettysocket.websocket.model.ResultModel;
import com.meteor.nettysocket.websocket.protocol.ProtocolEnum;
import com.meteor.nettysocket.websocket.service.SimlperCallBack;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("all")
public class ISocketService  implements SimlperCallBack {

    private static final Logger logger = LoggerFactory.getLogger(ISocketService.class);

    public static final Map<String, SimlperCallBack> userMap = new ConcurrentHashMap<String, SimlperCallBack>(); // <userId, callBack>

    private ChannelHandlerContext ctx;
    private String name;

    public ISocketService(ChannelHandlerContext ctx, String name) {
        this.ctx = ctx;
        this.name = name;
    }


    /**
     * 注册连接
     * @param requestId
     * @param callBack
     * @return
     */
    public static boolean register(String requestId, SimlperCallBack callBack) {
        if (null == requestId || userMap.containsKey(requestId)) {
            return false;
        }
        userMap.put(requestId, callBack);
        return true;
    }

    /**
     * 移除连接
     * @param requestId
     * @return
     */
    public static boolean logout(String requestId) {
        if (null == requestId  || !userMap.containsKey(requestId)) {
            return false;
        }
        userMap.remove(requestId);
        return true;
    }
    @Override
    public void sendMsg(ResultModel res) throws Exception {
        if (this.ctx == null || this.ctx.isRemoved()) {
            throw new Exception("尚未握手成功，无法向客户端发送WebSocket消息");
        }
        this.ctx.channel().write(new TextWebSocketFrame(JSON.toJSONString(res)));
        this.ctx.flush();

    }

    @Override
    public void sendAMR(ByteBuf byteBuf) throws Exception {

        if (this.ctx == null || this.ctx.isRemoved()) {
            throw new Exception("尚未握手成功，无法向客户端发送WebSocket消息");
        }
        try {
            this.ctx.channel().write(new BinaryWebSocketFrame(byteBuf));

        }catch (Exception e){
            e.printStackTrace();
        }

        this.ctx.flush();
    }



    /**
     * 通知所有机器有机器下线
     * @param requestId
     */
    public static void notifyDownline(String requestId) {
      userMap.forEach((reqId, callBack) -> { // 通知有人下线
          ResultModel serviceRequest = new ResultModel();
            serviceRequest.setProtocolId(ProtocolEnum.login_out.code);
            serviceRequest.setRequestId(requestId);
            try {
                callBack.sendMsg(serviceRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
