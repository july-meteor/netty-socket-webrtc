package com.meteor.nettysocket.websocket.service;

import com.meteor.nettysocket.websocket.model.ResultModel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * 二进制处理类
 *
 * 这里写得比较简单。。。按正常流程协议是要去拆分的。作为demo就算了
 */
public  class BinaryHandler {

    public ResultModel decoding(ChannelHandlerContext ctx, ByteBuf byteBuf){

        //返回文本消息
        ResultModel result =new ResultModel();
        result.setFlag(true);
        result.setMessage("发送成功！");



        return  result;
    }

}
