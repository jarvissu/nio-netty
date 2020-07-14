package com.oppo.usercenter.ojt.netty.rpc.dubbo_rpc.register;

import com.alibaba.fastjson.JSON;
import com.oppo.usercenter.ojt.netty.rpc.dubbo_rpc.protocol.InvokerProtocol;
import com.oppo.usercenter.ojt.netty.rpc.dubbo_rpc.protocol.RegisterProtocol;
import com.oppo.usercenter.ojt.netty.rpc.dubbo_rpc.protocol.RegisterTypeEnum;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterHandler extends SimpleChannelInboundHandler<RegisterProtocol> {

    private static Map<String, RegisterProtocol> providerMapping = new ConcurrentHashMap<>();

    private static Map<String, RegisterProtocol> consumerMapping = new ConcurrentHashMap<>();

    public RegisterHandler() throws Exception {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterProtocol protocol) throws Exception {
        if (Objects.isNull(protocol)) {
            return;
        }

        System.out.println("protocol=[" + JSON.toJSONString(protocol) + "]");

        RegisterTypeEnum registerType = protocol.getRegisterType();
        if (RegisterTypeEnum.PROVIDER.equals(registerType)) {
            providerMapping.put(protocol.getServiceName(), protocol);
        } else {
            consumerMapping.put(protocol.getServiceName(), protocol);
            RegisterProtocol providerProtocol = providerMapping.get(protocol.getServiceName());
            System.out.println("providerMapping: " + JSON.toJSONString(providerMapping));
            System.out.println("[response]: " + JSON.toJSONString(providerProtocol) + "");

            if (Objects.nonNull(providerProtocol)) {
                ctx.writeAndFlush(providerProtocol);
            }
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
