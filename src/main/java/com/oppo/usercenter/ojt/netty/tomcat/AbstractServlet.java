package com.oppo.usercenter.ojt.netty.tomcat;

public abstract class AbstractServlet {

    public static final String GET = "GET";

    public static final String POST = "POST";

    public void service(Request request, Response response){
        String method = request.getMethod();
        if (POST.equalsIgnoreCase(method)){
            doPost(request, response);
        } else {
            doGet(request, response);
        }
    }

    protected abstract void doGet(Request request, Response response);

    protected abstract void doPost(Request request, Response response);
}
