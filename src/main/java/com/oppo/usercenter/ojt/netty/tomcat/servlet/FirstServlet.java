package com.oppo.usercenter.ojt.netty.tomcat.servlet;

import com.oppo.usercenter.ojt.netty.tomcat.AbstractServlet;
import com.oppo.usercenter.ojt.netty.tomcat.Request;
import com.oppo.usercenter.ojt.netty.tomcat.Response;

public class FirstServlet extends AbstractServlet {

    @Override
    protected void doGet(Request request, Response response) {
        doPost(request, response);
    }

    @Override
    protected void doPost(Request request, Response response) {
        response.write("I am first servlet!!!");
    }
}
