package com.mar.server;

import com.mar.servlet.HttpServlet;
import com.mar.servlet.Request;
import com.mar.servlet.Response;
import com.mar.utils.HttpProtocolUtil;

import java.io.IOException;

/**
 * @Author: 刘劲
 * @Date: 2020/5/7 9:21
 */
public class DispatcherServlet extends HttpServlet {


    @Override
    public void doGet(Request request, Response response) {
        String content = "<h1>DispatcherServlet get</h1>";
        try {
            response.output((HttpProtocolUtil.getHttpHeader200(content.getBytes().length) + content));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(Request request, Response response) {
        String content = "<h1>DispatcherServlet post</h1>";
        try {
            response.output((HttpProtocolUtil.getHttpHeader200(content.getBytes().length) + content));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() throws Exception {

    }

    public void destroy() throws Exception {

    }
}
