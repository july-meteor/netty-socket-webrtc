package com.meteor.nettysocket.websocket.server.req.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  websocket 简单的处理,
 *  目的：
 *      1、websocket第一次接入会以HTTP的方式，我们要完成第一次握手并构建 长连接
 *      2、websocket 只有抽象的方法
 */
public abstract  class WebSocketHandler extends SimpleChannelInboundHandler<Object> {


    private static final Logger LOG = LoggerFactory.getLogger(WebSocketHandler.class);

    protected WebSocketServerHandshaker handshaker;
    protected ChannelHandlerContext ctx;
    protected String sessionId;


    /**
     *  接入方式辨别并处理
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof FullHttpRequest){//传统http接入方式
            handleHttpRequest(ctx, (FullHttpRequest) msg);

        }else if(msg instanceof WebSocketFrame){//webSocketS接入方式
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        LOG.info("连接关闭!!!");
        super.close(ctx, promise);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("连接关闭!!!");
        ctx.close();

    }

    /**
     *  处理HTTP 接入方式
     *   处理Http请求，完成WebSocket握手
     * 	 * 注意：WebSocket连接第一次请求使用的是Http
     * @param ctx
     * @param request
     * @throws Exception
     */
    private    void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception{
        // 如果HTTP解码失败，返回HHTP异常
        if (!request.getDecoderResult().isSuccess() || (!"websocket".equals(request.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        // 正常WebSocket的Http连接请求，构造握手响应返回
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://" + request.headers().get(HttpHeaders.Names.HOST), null, false);
        handshaker = wsFactory.newHandshaker(request);
        if (handshaker == null) { // 无法处理的websocket版本
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        } else { // 向客户端发送websocket握手,完成握手
            handshaker.handshake(ctx.channel(), request);
            // 记录管道处理上下文，便于服务器推送数据到客户端
            this.ctx = ctx;
        }
    }


    /**
     *  http返回消息
     * @param ctx
     * @param request
     * @param response
     */
    private  void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response){
        // 返回应答给客户端
        if (response.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(response.getStatus().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
            HttpHeaders.setContentLength(response, response.content().readableBytes());
        }

        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(response);
        if (!HttpHeaders.isKeepAlive(request) || response.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }

    }

    /**
     * 处理 websocket连接
     * @param ctx
     * @param frame
     * @throws Exception
     */
    protected  abstract void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception;



    /**
     *  socket返回消息
     * @param msg
     * @throws Exception
     */
    protected  abstract  void sendWebSocket(String msg) throws Exception;



}
