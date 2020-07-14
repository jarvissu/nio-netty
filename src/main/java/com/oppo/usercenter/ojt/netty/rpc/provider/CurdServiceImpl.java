package com.oppo.usercenter.ojt.netty.rpc.provider;

import com.oppo.usercenter.ojt.netty.rpc.api.CurdService;

public class CurdServiceImpl implements CurdService {

    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int sub(int a, int b) {
        return a - b;
    }

    @Override
    public int multi(int a, int b) {
        return a * b;
    }

    @Override
    public int div(int a, int b) {
        return a / b;
    }
}
