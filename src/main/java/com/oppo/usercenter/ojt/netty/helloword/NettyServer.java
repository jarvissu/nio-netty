package com.oppo.usercenter.ojt.netty.helloword;

import com.oppo.usercenter.ojt.SystemConstant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class NettyServer {

    private int port;

    public NettyServer(int port){
        this.port = port;
    }

    public void start() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        EventLoopGroup workGroup = new NioEventLoopGroup(4);

        serverBootstrap.group(bossGroup, workGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
//                pipeline.addLast(new LengthFieldBasedFrameDecoder(SystemConstant.BUFFER_SIZE, 0, 4, 0,4));
//                pipeline.addLast(new LengthFieldPrepender(4));
                pipeline.addLast(new DelimiterBasedFrameDecoder(SystemConstant.BUFFER_SIZE, Delimiters.lineDelimiter()));
                pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                pipeline.addLast(new NettyServerEchoHandler());
            }
        });

        serverBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);

        ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
        System.out.println("Server startup...");

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
        int port = 8080;
        new NettyServer(port).start();
    }
}
