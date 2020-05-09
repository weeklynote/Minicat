package com.mar.servlet;

import com.mar.utls.HttpProtocolUtil;
import com.mar.utls.StaticResourceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Author: 刘劲
 * @Date: 2020/5/6 19:02
 */
public class Response {

    private OutputStream outputStream;

    public Response(OutputStream outputStream){
        this.outputStream = outputStream;
    }

    public void output(String content) throws IOException {
        outputStream.write(content.getBytes());
    }

    public void html(String path) throws IOException {
        String absolutePath = StaticResourceUtil.getAbsolutePath(path);
        File file = new File(absolutePath);
        if (file.exists() && file.isFile()){
            StaticResourceUtil.outputStaticResource(new FileInputStream(file), outputStream);
        }else {
            output(HttpProtocolUtil.getHttpHeader404());
        }
    }

}
