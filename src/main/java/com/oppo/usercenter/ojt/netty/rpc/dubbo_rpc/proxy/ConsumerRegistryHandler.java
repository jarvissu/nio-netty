package com.oppo.usercenter.ojt.netty.rpc.dubbo_rpc.proxy;

import com.oppo.usercenter.ojt.SystemConstant;
import com.oppo.usercenter.ojt.netty.rpc.dubbo_rpc.protocol.RegisterProtocol;
import com.oppo.usercenter.ojt.netty.rpc.dubbo_rpc.protocol.RegisterTypeEnum;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;

import java.util.Random;

@Data
public class ConsumerRegistryHandler extends SimpleChannelInboundHandler<RegisterProtocol> {

    private RegisterProtocol providerProtocol;

    private Class<?> proxyClass;

    public ConsumerRegistryHandler(Class<?> proxyClass){
        this.proxyClass = proxyClass;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterProtocol msg) throws Exception {
        this.providerProtocol = msg;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String className = this.proxyClass.getName();
        RegisterProtocol protocol = new RegisterProtocol();
        protocol.setServiceName(className);
        protocol.setRegisterType(RegisterTypeEnum.CONSUMER);
        protocol.setHost("127.0.0.1");
        // 此处仅模拟客户端连接端口
        protocol.setPort(new Random(System.currentTimeMillis()).nextInt(55535) + 10000);

        ctx.writeAndFlush(protocol);

        // 关闭连接; 此处不能关闭连接，否则providerProtocol还没有接收到值时，连接已经关闭
//        ctx.close();
    }
}
