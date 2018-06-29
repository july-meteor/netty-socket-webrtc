package com.meteor.nettysocket.base.config;

import com.meteor.nettysocket.base.componet.WebSocketProtocolInitilzer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 747718944@qq.com
 * @data 18-6-27 下午4:50
 *  整合spring boot 和 netty
 *   nettye的配置属性
 *   s
 */
@Configuration
@SuppressWarnings("all")
public class NettyConfig {

    @Autowired
    @Qualifier("WebSocketProtocolInitilzer")
    private WebSocketProtocolInitilzer webSocketProtocolInitilzer;

    @Bean( name =  "serverBootstrap")
    public ServerBootstrap bootstrap(){
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup(), workerGroup());
        serverBootstrap.channel(NioServerSocketChannel.class).childHandler(webSocketProtocolInitilzer);
        return  serverBootstrap;

    }

    @Bean(name =" bossGroup")
    public NioEventLoopGroup bossGroup(){
        return  new NioEventLoopGroup();
    }

    @Bean(name =" workerGroup")
    public NioEventLoopGroup workerGroup(){
        return  new NioEventLoopGroup();
    }


}
