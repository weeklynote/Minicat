package com.mar.server;

import com.mar.servlet.HttpServlet;
import com.mar.utils.StaticResourceUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @Author: 刘劲
 * @Date: 2020/5/6 18:56
 */
public class BootStrap {

    private int port = 8080;
    private Map<String, HttpServlet> urlMapper = new HashMap<>(16);

    public void start() throws IOException {
        loadWebApp();
        int corePoolSize = 10;
        int maximumPoolSize =50;
        long keepAliveTime = 100L;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();


        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                handler
        );
        ServerSocket serverSocket = new ServerSocket(port);
        /** 1.0 */
//        while (true){
//            final Socket accept = serverSocket.accept();
//            final OutputStream outputStream = accept.getOutputStream();
//            String hello = "Hello, MiniCat!";
//            String response = HttpProtocolUtil.getHttpHeader200(hello.getBytes().length) + hello;
//            outputStream.write(response.getBytes());
//            accept.close();
//        }
        /** 2.0 */
//        while (true){
//            final Socket accept = serverSocket.accept();
//            Request request = new Request(accept.getInputStream());
//            Response response = new Response(accept.getOutputStream());
//            response.html(request.getUrl());
//            accept.close();
//        }
        /** 3.0 */
//        while (true){
//            final Socket accept = serverSocket.accept();
//            Request request = new Request(accept.getInputStream());
//            Response response = new Response(accept.getOutputStream());
//            if (!urlMapper.containsKey(request.getUrl())){
//                // 静态资源
//                response.html(request.getUrl());
//            }else {
//                // 动态资源
//                HttpServlet httpServlet = urlMapper.get(request.getUrl());
//                httpServlet.service(request, response);
//            }
//            accept.close();
//        }
        /** 4.0(不用线程池) */
//        while (true){
//            final Socket accept = serverSocket.accept();
//            final RequestProcessor requestProcessor = new RequestProcessor(accept, urlMapper);
//            requestProcessor.start();
//        }
        /** 5.0使用线程池 */
//        while (true){
//            final Socket accept = serverSocket.accept();
//            final RequestProcessor requestProcessor = new RequestProcessor(accept, urlMapper);
//            threadPoolExecutor.execute(requestProcessor);
//        }
        /** 6.0加载webapps目录 */
        while (true){
            final Socket accept = serverSocket.accept();
            final RequestProcessor requestProcessor = new RequestProcessor(accept, urlMapper);
            threadPoolExecutor.execute(requestProcessor);
        }

    }

    public static void main(String[] args) throws IOException {
        BootStrap bootStrap = new BootStrap();
        bootStrap.start();
    }

    private void loadWebApp() {
        File file = new File(StaticResourceUtil.getAbsolutePath("webapps"));
        if (file.exists() && file.isDirectory()){
            final File[] files = file.listFiles();
            if (files != null && files.length > 0){
                for (File app : files) {
                    loadServlet(app.getAbsolutePath(), app.getName());
                }
            }
        }
    }

    private ClassLoader getClassLoader(String path){
        return new MyClassLoader(path);
    }

    private void loadServlet(String parent, String name) {
        // 自定义ClassLoader用来隔离WebApp下的class文件加载
        ClassLoader classLoader = getClassLoader(parent);
        String path = "webapps" + File.separator + name + File.separator + "WEB-INF" + File.separator + "web.xml";
        // 加载webapps目录下的web.xml文件，与自定义tomcat时的解析流程差不多
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(path);
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            /**
             * <servlet>
             *     <servlet-name>mar</servlet-name>
             *     <servlet-class>com.mar.server.DispatcherServlet</servlet-class>
             * </servlet>
             */
            List<Element> selectNodes = rootElement.selectNodes("//servlet");
            for (int i = 0; i < selectNodes.size(); i++) {
                Element element =  selectNodes.get(i);
                Element servletnameElement = (Element) element.selectSingleNode("servlet-name");
                String servletName = servletnameElement.getStringValue();
                Element servletclassElement = (Element) element.selectSingleNode("servlet-class");
                String servletClass = servletclassElement.getStringValue();
                /**
                 * 根据servlet-name的值找到url-pattern
                 * <web-app>
                 *    .....
                 *     <servlet-mapping>
                 *         <servlet-name>mar</servlet-name>
                 *         <url-pattern>/mar</url-pattern>
                 *     </servlet-mapping>
                 * </web-app>
                 */
                Element servletMapping = (Element) rootElement.selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletName + "']");
                /** /mar */
                String urlPattern = servletMapping.selectSingleNode("url-pattern").getStringValue();
                // 在解析<servlet-class>之后需要先试用加载器加载class文件
                final Class<?> loadClass = classLoader.loadClass(servletClass);
                urlMapper.put(urlPattern, (HttpServlet) loadClass.newInstance());
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static class MyClassLoader extends ClassLoader{
        private String path;

        public MyClassLoader(String path) {
            this.path = path;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                System.err.println("MyClassLoader findClass name>>>>" + name);
                // 获取.class 文件的二进制字节
                byte[] bytes = getBytes(name);
                // 将二进制字节转化为Class对象
                Class<?> clazz = defineClass(name,bytes,0,bytes.length);
                return clazz;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return super.findClass(name);
        }

        private byte[] getBytes(String name) throws IOException {
            String classFile = path + File.separator + "WEB-INF" + File.separator + "classes" + File.separator + name.replace(".", File.separator) + ".class";
            System.err.println("MyClassLoader getBytes name>>>" + classFile);
            File file = new File(classFile);
            FileInputStream fileInputStream = new FileInputStream(file);
            FileChannel fileChannel = fileInputStream.getChannel();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            WritableByteChannel writableByteChannel = Channels.newChannel(byteArrayOutputStream);
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int i;
            while (true) {
                i = fileChannel.read(byteBuffer);
                if (i == 0 || i == -1) {
                    break;
                }
                byteBuffer.flip();
                writableByteChannel.write(byteBuffer);
                byteBuffer.clear();
            }
            writableByteChannel.close();
            fileChannel.close();
            fileInputStream.close();
            return byteArrayOutputStream.toByteArray();
        }
    }
}
