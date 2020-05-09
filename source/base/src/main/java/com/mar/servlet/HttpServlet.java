package com.mar.servlet;

import java.io.IOException;

/**
 * @Author: 刘劲
 * @Date: 2020/5/8 21:22
 */
public abstract class HttpServlet implements Servlet {

    public abstract void doGet(Request request, Response response) throws IOException;
    public abstract void doPost(Request request, Response response) throws IOException;

    @Override
    public void service(Request request, Response response) throws IOException {
        if ("GET".equalsIgnoreCase(request.getHttpMethod())){
            doGet(request, response);
        }else {
            doPost(request, response);
        }
    }
}
