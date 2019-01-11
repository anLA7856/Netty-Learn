/##介绍：
该例子是一个传统的AIO运行例子，使用NIO基本类来构造程序.
1. 运行`com.anla.netty.aio.TimeServer.java`
2. 运行`com.anla.netty.aio.TimeClient.java`

## NIO有点
1. 通过java.util.concurrent.Futrue类来标识异步操作的结果
2. 在执行异步操作的时候作为操作完成的回调

NIO2.0的异步套接自通道是真正的异步非阻塞I/O，对应于UNIX网络编程中的事件驱动I/O（AIO）。
它不需要多路复用器对注册的通道进行轮询操作即可实现异步读写，从而简化了NIO的编程模型。