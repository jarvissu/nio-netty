package com.oppo.usercenter.ojt.netty.helloword;

import com.oppo.usercenter.ojt.SystemConstant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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

import java.util.concurrent.TimeUnit;

public class NettyClient {

    private String host;

    private int port;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup loopGroup = new NioEventLoopGroup(2);
        bootstrap.group(loopGroup);
        bootstrap.channel(NioSocketChannel.class);
//        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
//                pipeline.addLast(new LengthFieldBasedFrameDecoder(SystemConstant.BUFFER_SIZE, 0, 4, 0,4));
//                pipeline.addLast(new LengthFieldPrepender(4));
                pipeline.addLast(new DelimiterBasedFrameDecoder(SystemConstant.BUFFER_SIZE, Delimiters.lineDelimiter()));
                pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                pipeline.addLast(new NettyClientEchoHandler());
            }
        });

        ChannelFuture channelFuture = bootstrap.connect(this.host, this.port).sync();
        String content = "Hello OPPO!\r\n";
        writeAndFlush(channelFuture.channel(), content);

        ChannelFuture closeFuture = channelFuture.channel().close();
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("client shutdown!!!");
                loopGroup.shutdownGracefully();
            }
        });
    }

    private void writeAndFlush(Channel channel, String content) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            channel.writeAndFlush(content);
            TimeUnit.SECONDS.sleep(1);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String host = "127.0.0.1";
        int port = 8080;

        new NettyClient(host, port).start();
    }
}
