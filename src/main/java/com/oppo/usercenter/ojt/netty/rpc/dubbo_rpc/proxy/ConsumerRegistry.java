package com.oppo.usercenter.ojt.netty.rpc.dubbo_rpc.proxy;

import com.oppo.usercenter.ojt.netty.rpc.dubbo_rpc.protocol.RegisterProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class ConsumerRegistry {
    private final String registerHost;

    private final int registerPort;

    public ConsumerRegistry(String registerHost, int registerPort) {
        this.registerHost = registerHost;
        this.registerPort = registerPort;
    }

    public RegisterProtocol request(Class<?> proxyClass) throws InterruptedException {

        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup loopGroup = new NioEventLoopGroup(1);
        bootstrap.group(loopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

        ConsumerRegistryHandler registryHandler = new ConsumerRegistryHandler(proxyClass);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0,4));
                pipeline.addLast(new LengthFieldPrepender(4));
                pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                pipeline.addLast(new ObjectEncoder());
                pipeline.addLast(registryHandler);
            }
        });

        ChannelFuture channelFuture = bootstrap.connect(this.registerHost, this.registerPort).sync();
        ChannelFuture closeFuture = channelFuture.channel().closeFuture();
        closeFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                loopGroup.shutdownGracefully();
            }
        });
        closeFuture.sync();

        return registryHandler.getProviderProtocol();
    }
}
