package com.meteor.nettysocket.base.componet;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * ybh websocket启动器
 */
@Component
public class WebSocketLanucher {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketLanucher.class);

    @Value("${websocket.server.port:8080}")
    private int SERVER_PORT;
    //服务引导
    @Autowired
    @Qualifier("serverBootstrap")
    private ServerBootstrap bootstrap;

    private Channel channel;

    /**
     * 让spring 初始化的时候开启服务
     */
    @PostConstruct
    public void start (){
        try {
            channel = bootstrap.bind(SERVER_PORT).sync().channel();
            LOG.info("socket服务开启 port:{}",SERVER_PORT);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(),e);
        }
    }


    @PreDestroy
    public void stop(){
        try {
            channel.closeFuture().sync();
            LOG.info("服务器关闭!");
        } catch (InterruptedException e) {
          LOG.error(e.getMessage(),e);
        }
    }

}
