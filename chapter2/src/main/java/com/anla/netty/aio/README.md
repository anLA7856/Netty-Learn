##介绍：
该例子是一个传统的NIO运行例子，使用NIO基本类来构造程序.
1. 运行`com.anla.netty.nio.TimeServer.java`
2. 运行`com.anla.netty.nio.TimeClient.java`

## NIO有点
1. 客户端发起的连接是异步的，可以利用多路复用其注册OP——CONNECT等待后续结果
2. SocketChannel的读写操作都是异步的，如果没有可读写数据它不回同步等待，直接返回
3. 线程模型的优化：Selector使用Linux上主流的epoll实现，没有连接句柄限制，可以同时受理成千上万请求