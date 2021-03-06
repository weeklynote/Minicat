# Tomcat
1.首先熟悉Tomcat软件包的conf/server.xml，了解各个标签的作用，在源代码中有对应的类。
2.实现自定义Tomcat时使用了Socket编程。
3.Tomcat启动流程见本目录的对应图片
4.Tomcat的请求处理流程见本目录的对应图片
5.JVM 的类加载机制
1. Jvm内置了几种类加载器，包括：引导类加载器、扩展类加载器、系统类加载器；每个加载器都会加载不同位置的class文件。
2. 双亲委派机制在加载类时会把请求转发给父类加载器，一直传递到顶层加载器；父类加载器加载不到就交给子类。
3. 双亲委派机制能防止重复加载同多个.class。通过委托去向父加载器请求，如果加载过了就不在加载，保证数据安全。保证核心.class不能被篡改。通过委托方式，不会去篡改核心.class，即使篡改也不会去加载，即使加载也不会是同一个.class对象了。

6.Tomcat 的类加载机制没有严格遵守双亲委派机制，这点在自定义Tomcat时加载webapps目录下的应用时也用到了类加载器。
7.Tomcat 对 HTTPS 的支持
8.Tomcat 性能优化策略
1. -server 启动Server，以服务端模式运⾏服务端模式建议开启
2. -Xms 最小堆内存;-Xmx 最大堆内存;建议设置为可用内存的80%
3. 调整tomcat的连接器
4. 禁用 AJP 连接器
5. 调整 IO 模式，并发性能要求较高时，可以考虑APR模式
6. 动静分离(Nginx + Tomcat)

# Nginx
作用：
1.Http服务器
2.反向代理服务器proxy_pass
3.负载均衡服务器upstream + weight(不同的均衡策略)
4.动静分离
机制：
Nginx启动后，以daemon多进程方式在后台运行，包括一个Master进程和多个Worker进程，Master进程管理Worker进程。
执行./nginx -s reload命令master进程对配置文件进行语法检查，尝试配置尝试成功则使⽤新的配置，新建worker进程；新建成功，给旧的worker进程发送关闭消息；旧的worker进程收到信号会继续服务，直到把当前进程接收到的请求处理完毕后关闭。
当一个请求来临时，会面领多个worker进程抢占的可能，Nginx是用户斥锁来保证只能有一个worker进程能处理请求。
