package com.oppo.usercenter.ojt.netty.rpc.dubbo_rpc.proxy;

import java.lang.reflect.Proxy;

public class RpcProxy {

    public static <T> T createProxy(Class<?> clazz, String registryHost, int registryPort){
        /*clazz传进来就是interface*/
        MethodProxy proxy = new MethodProxy(clazz, registryHost, registryPort);
        Class<?>[] interfaces = clazz.isInterface() ? new Class[]{clazz} : clazz.getInterfaces();
        T result = (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, proxy);
        return result;
    }
}
