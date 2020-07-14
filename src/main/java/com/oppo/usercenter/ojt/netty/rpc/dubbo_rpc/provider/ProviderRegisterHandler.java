package com.oppo.usercenter.ojt.netty.rpc.dubbo_rpc.provider;

import com.oppo.usercenter.ojt.netty.rpc.dubbo_rpc.protocol.RegisterProtocol;
import com.oppo.usercenter.ojt.netty.rpc.dubbo_rpc.protocol.RegisterTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProviderRegisterHandler extends SimpleChannelInboundHandler<RegisterProtocol> {

    private String packageName;

    private String serverHost;

    private int serverPort;

    private static List<RegisterProtocol> registerProtocols = new ArrayList<>();

    public ProviderRegisterHandler(String packageName, String serverHost, int serverPort){
        this.packageName = packageName;
        this.serverHost= serverHost;
        this.serverPort = serverPort;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterProtocol msg) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("register start");
        scanPackage(this.packageName);
        if (registerProtocols.size() <= 0){
            return;
        }
        System.out.println("start register...");

        registerProtocols.forEach(protocol -> ctx.writeAndFlush(protocol));

        // 关闭连接
        ctx.close();
    }

    public void scanPackage(String packageName) {
        if (StringUtils.isBlank(packageName)) {
            return;
        }

        String rootPath = this.getClass().getResource("/").getPath() + packageName.replaceAll("\\.", "/");

        File rootPackage = new File(rootPath);
        if (!rootPackage.exists()) {
            return;
        }

        for (File file : rootPackage.listFiles()) {
            if (file.isDirectory()) {
                scanPackage(packageName + "." + file.getName());
            } else {
                String fileName = file.getName();
                RegisterProtocol protocol = new RegisterProtocol();
                protocol.setHost(this.serverHost);
                protocol.setPort(this.serverPort);
                protocol.setRegisterType(RegisterTypeEnum.PROVIDER);
                protocol.setServiceName(packageName + "." + fileName.substring(0, fileName.lastIndexOf(".")));

                registerProtocols.add(protocol);
            }
        }
    }
}
