package com.oppo.usercenter.ojt.netty.tomcat.server;

import com.oppo.usercenter.ojt.netty.tomcat.Response;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;

public class NettyResponse implements Response {

    private final ChannelHandlerContext ctx;

    public NettyResponse(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void write(String body) {
        if (StringUtils.isBlank(body)){
            return;
        }
        ByteBuf response = Unpooled.wrappedBuffer(body.getBytes());
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, response);
//        httpResponse.headers().add("Content-Type", "text/html");
        httpResponse.headers().add("Content-Type", "application/json");

        ctx.writeAndFlush(httpResponse);

        System.out.println("[response]: " + body);
    }
}
