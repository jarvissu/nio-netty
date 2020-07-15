package com.oppo.usercenter.ojt.netty.rpc.dubbo_rpc.consumer;

import com.oppo.usercenter.ojt.netty.rpc.api.CurdService;
import com.oppo.usercenter.ojt.netty.rpc.api.HelloService;
import com.oppo.usercenter.ojt.netty.rpc.dubbo_rpc.proxy.RpcProxy;

public class RpcConsumer {

    public static void main(String[] args) {
        String registryHost = "127.0.0.1";
        int registryPort = 9001;

        HelloService helloService = RpcProxy.createProxy(HelloService.class, registryHost, registryPort);
        helloService.sayHello("苏童");

        CurdService curdService = RpcProxy.createProxy(CurdService.class, registryHost, registryPort);

        int a = 10;
        int b = 5;
        System.out.println(curdService.add(a, b));
        System.out.println(curdService.sub(a, b));
        System.out.println(curdService.multi(a, b));
        System.out.println(curdService.div(a, b));
    }
}
