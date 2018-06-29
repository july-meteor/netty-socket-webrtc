package com.meteor.nettysocket.websocket.service.impl;

import com.meteor.nettysocket.websocket.model.RequestModel;
import com.meteor.nettysocket.websocket.model.ResultModel;
import com.meteor.nettysocket.websocket.protocol.ProtocolEnum;
import com.meteor.nettysocket.websocket.service.TextHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("all")
public class TextHandlerImpl  extends TextHandler {

    private  static  final Logger LOG = LoggerFactory.getLogger(TextHandlerImpl.class);

    @Override
    public ResultModel register(ChannelHandlerContext ctx,RequestModel req) {
        ResultModel result = new ResultModel();
        String requestId = req.getRequestId();//请求id；
        if (null == requestId) {
            result.setMessage("requestId不能为空");
        } else if (null == req.getName()) {
            result.setMessage("name不能为空");

        } else if (ISocketService.userMap.containsKey(requestId)) {
            result.setMessage("您已经注册了，不能重复注册");
        }

        if (!ISocketService.register(requestId, new ISocketService(ctx, req.getName()))) {
            result.setMessage("驻车失败!");
        } else {
            result.setFlag(true);
            result.setMessage("注册成功!");
            ISocketService.userMap.forEach((reqId, callBack) -> {
                result.getHadOnline().put(reqId, ((ISocketService)callBack).getName()); // 将已经上线的人员返回
                if (!reqId.equals(requestId)) {
                    //推送的内容
                    ResultModel pushData = new ResultModel();
                    pushData.setFlag(true);
                    pushData.setProtocolId(ProtocolEnum.cur_online.code);//当前在线用户
                    pushData.setRequestId(requestId);//请求地址
                    pushData.setName(req.getName());
                    try {
                        callBack.sendMsg(pushData); // 通知有人上线
                    } catch (Exception e) {
                     e.printStackTrace();
                    }
                }
            });
        }

        return result;
    }

    @Override
    public ResultModel loginOut(RequestModel request) {
        ResultModel result = new ResultModel();

        String requestId = request.getRequestId();

        if (null == requestId) {
            result.setMessage("requestId不能为空");

        } else {
            ISocketService.logout(requestId);
            result.setMessage("下线成功");
            // 通知有人下线
            ISocketService.notifyDownline(requestId);

        }
        return result;
    }

    @Override
    public ResultModel sendMessage(RequestModel req) {
        ResultModel result = new ResultModel();
        String requestId = req.getRequestId();
        if (null == requestId) {
            result.setMessage("requestId不能为空");
        } else if (null == req.getName()) {
            result.setMessage("name不能为空");
        } else if (null == req.getMessage() ) {
            result.setMessage("message不能为空");
        } else {
            result.setMessage("发送消息成功");
            /**
             * 后续需要改进，封装
             * 全部都推送
             */
            ISocketService.userMap.forEach((reqId,callBack)->{
                try {
                    ResultModel pushMsg = new ResultModel();
                    pushMsg.setProtocolId(ProtocolEnum.send_message.code);
                    pushMsg.setRequestId(requestId);
                    pushMsg.setMessage(req.getMessage());

                    callBack.sendMsg(pushMsg);
                }catch (Exception e){
                    LOG.error(e.getMessage());
                    LOG.error(e.getMessage(),e);
                }

            });
        }
        return result;
    }

}
