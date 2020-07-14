package com.oppo.usercenter.ojt.netty.tomcat.server;

import com.oppo.usercenter.ojt.netty.tomcat.AbstractServlet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;

import java.util.Map;

public class NettyHandler extends SimpleChannelInboundHandler<HttpRequest> {

    private final Map<String, AbstractServlet> servletMapping;

    public NettyHandler(Map<String, AbstractServlet> servletMapping) {
        this.servletMapping = servletMapping;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
        NettyRequest request = new NettyRequest(ctx, msg);
        NettyResponse response = new NettyResponse(ctx);

        String url = request.getUrl();
        if (this.servletMapping.containsKey(url)){
            AbstractServlet servlet = this.servletMapping.get(url);
            servlet.service(request, response);
        } else {
            response.write("404 - NOT Found!");
        }
    }
}
