package com.mar.servlet;


/**
 * @Author: 刘劲
 * @Date: 2020/5/7 9:20
 */
public interface Servlet {

    void init() throws Exception;
    void destroy() throws Exception;
    void service(Request request, Response response) throws Exception;
}
