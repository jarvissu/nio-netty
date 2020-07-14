package com.oppo.usercenter.ojt.netty.tomcat.server;

import com.oppo.usercenter.ojt.netty.tomcat.AbstractServlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class NettyTomcat {

    private int port;

    public NettyTomcat(int port){
        this.port=  port;
    }

    public void start() throws Exception {
        Map<String, AbstractServlet> servletMap = loadServlet();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        EventLoopGroup workGroup = new NioEventLoopGroup(4);

        serverBootstrap.group(bossGroup, workGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new HttpRequestDecoder());
                pipeline.addLast(new HttpResponseEncoder());
                pipeline.addLast(new NettyHandler(servletMap));
            }
        });
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture channelFuture = serverBootstrap.bind(this.port).sync();
        System.out.println("Tomcat startup....");

        ChannelFuture closeFuture = channelFuture.channel().closeFuture();
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                bossGroup.shutdownGracefully();
                workGroup.shutdownGracefully();
            }
        });
        closeFuture.sync();
    }

    public Map<String, AbstractServlet> loadServlet() throws Exception {
        String rootPath = this.getClass().getResource("/").getPath();
        System.out.println("rootPath = " + rootPath);

        File file = new File(rootPath + "/web.properties");
        if (!file.exists()){
            return new HashMap<>();
        }

        FileInputStream inputStream = new FileInputStream(file);
        Properties properties =  new Properties();
        properties.load(inputStream);

        Map<String, AbstractServlet> servletMapping = new HashMap<>();
        for (Object o: properties.keySet()){
            String key = (String) o;
            if (StringUtils.isNotBlank(key) && key.endsWith(".url")){
                String servletUrl = properties.getProperty(key);

                String servletNameKey = key.replace("url", "servlet");
                String servletName = properties.getProperty(servletNameKey);

                AbstractServlet servlet = (AbstractServlet) Class.forName(servletName).newInstance();
                servletMapping.put(servletUrl, servlet);
            }
        }
        return servletMapping;
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        new NettyTomcat(port).start();
    }
}
