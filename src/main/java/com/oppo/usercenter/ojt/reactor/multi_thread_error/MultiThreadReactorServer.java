package com.oppo.usercenter.ojt.reactor.multi_thread_error;

public class MultiThreadReactorServer {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        new MultiThreadReactor(2,4).bind(port).start();
    }
}
