package com.oppo.usercenter.ojt.netty.rpc.simple_rpc.proxy;

import com.oppo.usercenter.ojt.netty.rpc.simple_rpc.protocol.InvokerProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MethodProxy implements InvocationHandler {

    private Class<?> clazz;

    public MethodProxy(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        /*如果传进来就是一个已实现的具体类*/
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(proxy, args);
        }
        return rpcInvoke(proxy, method, args);
    }

    private Object rpcInvoke(Object proxy, Method method, Object[] args) throws InterruptedException {

        /*封装协议传输*/
        InvokerProtocol msg = new InvokerProtocol();
        msg.setServiceName(clazz.getName());
        msg.setMethodName(method.getName());
        msg.setParamTypes(method.getParameterTypes());
        msg.setParamValues(args);

        RpcProxyHandler consumerHandler = new RpcProxyHandler();
        EventLoopGroup loopGroup = new NioEventLoopGroup(4);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(loopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                        pipeline.addLast(new LengthFieldPrepender(4));
                        pipeline.addLast(new ObjectEncoder());
                        pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                        pipeline.addLast(consumerHandler);
                    }
                });

        ChannelFuture channelFuture = bootstrap.connect("localhost", 8080).sync();

        /*发送远程调用的数据*/
        channelFuture.channel().writeAndFlush(msg).sync();
        ChannelFuture closeFuture = channelFuture.channel().closeFuture();
        closeFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                loopGroup.shutdownGracefully();
                System.out.println("Consume finished!");
            }
        });
        closeFuture.sync();
        return consumerHandler.getResponse();
    }
}
