package com.meteor.nettysocket.base.componet;

import com.meteor.nettysocket.websocket.server.req.handler.impl.SimlperWebSocketHandlerImpl;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author 747718944@qq.com
 * @data 18-6-28 上午11:00
 */
@Component
@Qualifier("WebSocketProtocolInitilzer")
@SuppressWarnings("all")
public class WebSocketProtocolInitilzer extends ChannelInitializer<Channel> {

    @Autowired
    private SimlperWebSocketHandlerImpl simlperWebSocketHandler;


    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("http-codec", new HttpServerCodec()); // Http消息编码解码
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536)); // Http消息组装
        pipeline.addLast("http-chunked", new ChunkedWriteHandler()); // WebSocket通信支持
        pipeline.addLast("handler",simlperWebSocketHandler); // WebSocket服务端Handler

    }
}
