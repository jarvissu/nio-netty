package com.oppo.usercenter.ojt.netty.tomcat.server;

import com.oppo.usercenter.ojt.netty.tomcat.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class NettyRequest implements Request {

    private final ChannelHandlerContext ctx;

    private final HttpRequest httpRequest;

    public NettyRequest(ChannelHandlerContext ctx, HttpRequest httpRequest) {
        this.ctx = ctx;
        this.httpRequest = httpRequest;
    }

    @Override
    public String getUrl() {
        return httpRequest.uri();
    }

    @Override
    public String getMethod() {
        return httpRequest.method().name();
    }
}
