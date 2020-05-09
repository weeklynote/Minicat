package com.mar.servlet;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: 刘劲
 * @Date: 2020/5/6 19:02
 */
public class Request {
    /** HTTP请求方式 */
    private String httpMethod;
    private String url;
    private InputStream inputStream;

    public Request(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        int count = 0;
        while (count == 0){
            count = inputStream.available();
        }
        byte[] bytes = new byte[count];
        inputStream.read(bytes);
        String inputStr = new String(bytes);
        String protocol = inputStr.split("\\n")[0];
        final String[] split = protocol.split(" ");
        this.httpMethod = split[0];
        this.url = split[1];
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
