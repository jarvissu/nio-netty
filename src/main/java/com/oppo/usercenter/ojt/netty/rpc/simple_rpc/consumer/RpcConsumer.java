package com.oppo.usercenter.ojt.netty.rpc.simple_rpc.consumer;

import com.oppo.usercenter.ojt.netty.rpc.api.CurdService;
import com.oppo.usercenter.ojt.netty.rpc.api.HelloService;
import com.oppo.usercenter.ojt.netty.rpc.simple_rpc.proxy.RpcProxy;

public class RpcConsumer {

    public static void main(String[] args) {
        HelloService helloService = RpcProxy.createProxy(HelloService.class);
        helloService.sayHello("苏童");

        CurdService curdService = RpcProxy.createProxy(CurdService.class);

        int a = 10;
        int b = 5;
        System.out.println(curdService.add(a, b));
        System.out.println(curdService.sub(a, b));
        System.out.println(curdService.multi(a, b));
        System.out.println(curdService.div(a, b));
    }
}
