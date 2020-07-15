package com.oppo.usercenter.ojt.netty.rpc.dubbo_rpc.provider;

import com.oppo.usercenter.ojt.netty.rpc.dubbo_rpc.register.RegisterHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class RpcProvider {

    private int port;

    public RpcProvider(int port){
        this.port = port;
    }

    public void start() throws InterruptedException {
        // 注册当前service到注册中心
        ProviderRegistry providerRegistry = new ProviderRegistry("127.0.0.1", this.port);
        providerRegistry.register("127.0.0.1", 9001, "com.oppo.usercenter.ojt.netty.rpc.api");
        System.out.println("Register success!");

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        EventLoopGroup workGroup = new NioEventLoopGroup(4);

        serverBootstrap.group(bossGroup, workGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0,4));
                pipeline.addLast(new LengthFieldPrepender(4));
                pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                pipeline.addLast(new ObjectEncoder());
                pipeline.addLast(new ProviderHandler());
            }
        });

        ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
        System.out.println("provider startup...");

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

    public static void main(String[] args) throws InterruptedException {
        int port = 9002;
        new RpcProvider(port).start();
    }
}
