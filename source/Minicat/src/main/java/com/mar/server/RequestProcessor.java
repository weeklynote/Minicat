package com.mar.server;

import com.mar.servlet.HttpServlet;
import com.mar.servlet.Request;
import com.mar.servlet.Response;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

/**
 * @Author: 刘劲
 * @Date: 2020/5/7 9:44
 */
public class RequestProcessor extends Thread {

    private Socket socket;
    private Map<String, HttpServlet> urlMappers;

    public RequestProcessor(Socket socket, Map<String, HttpServlet> urlMappers){
        this.socket = socket;
        this.urlMappers = urlMappers;
    }

    @Override
    public void run() {
        try {
            Request request = new Request(socket.getInputStream());
            Response response = new Response(socket.getOutputStream());
            if (!urlMappers.containsKey(request.getUrl())){
                // 静态资源
                response.html(request.getUrl());
            }else {
                // 动态资源
                HttpServlet httpServlet = urlMappers.get(request.getUrl());
                httpServlet.service(request, response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
