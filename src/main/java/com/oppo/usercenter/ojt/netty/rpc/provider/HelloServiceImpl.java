package com.oppo.usercenter.ojt.netty.rpc.provider;

import com.oppo.usercenter.ojt.netty.rpc.api.HelloService;

public class HelloServiceImpl implements HelloService {
    @Override
    public void sayHello(String name) {
        System.out.println("Hello World! " + name);
    }
}
