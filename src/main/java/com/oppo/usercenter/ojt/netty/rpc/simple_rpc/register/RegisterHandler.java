package com.oppo.usercenter.ojt.netty.rpc.simple_rpc.register;

import com.oppo.usercenter.ojt.netty.rpc.simple_rpc.protocol.InvokerProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.scene.transform.Rotate;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterHandler extends SimpleChannelInboundHandler<InvokerProtocol> {

    private Map<String, Object> serviceMapping = new ConcurrentHashMap<>();

    private List<String> classNames = new ArrayList<>();

    public RegisterHandler() throws Exception {
        scanPackage("com.oppo.usercenter.ojt.netty.rpc.provider");
        doRegister();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InvokerProtocol msg) throws Exception {
        Object result = null;
        if (serviceMapping.containsKey(msg.getServiceName())) {
            Object clazz = serviceMapping.get(msg.getServiceName());
            Method method = clazz.getClass().getMethod(msg.getMethodName(), msg.getParamTypes());
            result = method.invoke(clazz, msg.getParamValues());
        }
        if (Objects.isNull(result)) {
            result = new Object();
        }
        ctx.writeAndFlush(result);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
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
                classNames.add(packageName + "." + fileName.substring(0, fileName.lastIndexOf(".")));
            }
        }
    }

    public void doRegister() throws Exception {
        if (classNames.size() <= 0) {
            return;
        }

        for (String className : classNames) {
            Class<?> clazz = Class.forName(className);
            Class<?>[] interfaces = clazz.getInterfaces();
            if (Objects.nonNull(interfaces) && interfaces.length >= 1) {
                String interfaceName = interfaces[0].getName();
                serviceMapping.put(interfaceName, clazz.newInstance());
            }
        }
    }
}
