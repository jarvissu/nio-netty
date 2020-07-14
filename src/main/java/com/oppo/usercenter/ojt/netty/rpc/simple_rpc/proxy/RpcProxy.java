package com.oppo.usercenter.ojt.netty.rpc.simple_rpc.proxy;

import java.lang.reflect.Proxy;

public class RpcProxy {

    public static <T> T createProxy(Class<?> clazz){
        /*clazz传进来就是interface*/
        MethodProxy proxy = new MethodProxy(clazz);
        Class<?>[] interfaces = clazz.isInterface() ? new Class[]{clazz} : clazz.getInterfaces();
        T result = (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, proxy);
        return result;
    }
}
