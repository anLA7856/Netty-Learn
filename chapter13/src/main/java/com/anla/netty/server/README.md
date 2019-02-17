### Netty服务端创建的步骤
1. 创建ServerBootstrap实例
2. 设置并绑定Reactor线程池
3. 设置并绑定服务端Channel
4. TCP链路建立时创建ChannelPipeline
5. 添加并设置ChannelHandler
6. 监听端口并启动服务端
7. Selector轮询
8. 网络事件通知
9. 执行Netty系统和业务HandlerChannel



4. ChannelPipeline并不是NIO服务端必需的，本质是一个负责处理网络事件的职责链，负责管理和执行ChannelHandler，典型网络事件
 - 链路注册
 - 链路激活
 - 链路断开
 - 接受到请求消息
 - 请求消息接收并处理完毕
 - 发送应答消息
 - 链路发生异常
 - 发生用户自定义事件
 
5. Netty提供了大量的系统ChannelHandler供用户使用
 - 系统编解码框架，`ByteToMessageCodec`
 - 通用基于长度的半包解码器，`LengthFieldBasedFrameDecoder`
 - 码流日志打印Handler，`LoggingHandler`
 - SSL安全认证Handler，`SslHandler`
 - 链路空闲检测Handler，`IdleStateHandler`
 - 流量整形Handler，`ChannelTrafficShapingHandler`
 - Base64编解码，`Base64Decoder` 和 `Base64Encoder`
 
 
6.  Netty服务端 源码分析
首先通过构造函数创建ServerBootstrap实例，随后，通常会创建两个EventLoopGroup，也可以只创建一个并共享
```
        EventLoopGroup acceptGroup = new NioEventLoopGroup();
        EventLoopGroup IOGroup = new NioEventLoopGroup();
```
`NioEventLoopGroup`就是Reactor线程池，负责调度和执行客户端的接入、网络读写事件的处理，用户自定义任务和定时
任务的执行。
然后，根据不同的类型（client和server），通过传入不同的类（NioServerSocketChannel和NioSocketChannel），
从而创建不同的Channel工厂。
 - 当指定完工厂后，例如NioServerSocketChannel，需要设置一些TCP的参数，作为服务端，主要是设置TCP的backlog参数。
  `int listen(int fd, int backlog)    // 底层c对应接口`
  backlog指定了内核为此套接口排队的最大连接个数，对于给定的监听套接口，内核主要维护两个队列：
  未连接队列和已连接队列
  根据TCP三路握手过程中三个分节来分割这两个状态：
        服务器处于listen状态时，收到客户端syn分节（connect）时，在未完成队列中创建一个新条目，然后用三路握手的第二个分节，即服务器的syn相应
        客户端，此条目在第三个分节到达前（客户端对服务器的syn的ack）一致保留在未完成的连接队列中。如果三路握手完成，
        该条目从未完成连接队列搬到已完成连接队列的尾部
  当进程调用accept时，此时就从已完成队列取出一个条目。
  backlog被规定为两个队列总和的最大值，大多数实现默认值为5.Netty默认的backlog为100.
  TCP参数设置完成后，用户可以为启动辅助类和其父类分别指定Handler，两个Handler的用途不同：子类的Handler是NioServerSocketChannel对应的ChannelPipeline
  中的Handler;父类的Handler是客户端新介入的SocketChannel对应的ChannelPipeline的Handler。
  本质的区别就是，ServerBootstrap中的Handler是NioServerSocketChannel使用的，所有连接该监听端口都会执行它;
  父类的AbstractBootstrap中的Handler是个工厂类，他为每个新结入的客户端创建新的Handler
  
  
7. 客户端接入源码分析
  负责网络读写，连接和客户端介入的Reactor线程就是NioEventLoop，当多路符哦嗯器检测到新的准备就绪的Channel时，默认执行
  processSelectedKeysOptimized方法
  由于Channel的Attachment是NioServerSocketChannel，所以根据就绪位的操作位，执行不同的操作。
  
  ```
              if (a instanceof AbstractNioChannel) {
                  processSelectedKey(k, (AbstractNioChannel) a);
              } else {
                  @SuppressWarnings("unchecked")
                  NioTask<SelectableChannel> task = (NioTask<SelectableChannel>) a;
                  processSelectedKey(k, task);
              }
  ```
  所以NioUnsafe被设计成接口，由不同的Channel内部的Nio负责具体类的实现。
  对应NioServerSocketChannel，使用的是NioMessageUnsafe。
  ```
   for (;;) {
                      int localRead = doReadMessages(readBuf);
                      if (localRead == 0) {
                          break;
                      }
                      if (localRead < 0) {
                          closed = true;
                          break;
                      }
  
                      if (readBuf.size() >= maxMessagesPerRead | !autoRead) {
                          break;
                      }
                  }
  ```
  对于doReadMessages方法进行分析，就是接收新的客户端连接，并创建NioSocketChannel。
  接受新的客户端连接后，便会触发ChannelPipeline中的ChannelRead方法，执行headChannelHandlerContext的fireChannelRead方法，事件
  会在ChannelPipeline中传递，执行ServerBootstrapAcceptor的channelRead方法。
  该方法有三个步骤：
    - 将启动时传入的childHandler加入到客户端SocketChannel的ChannelPipeline中
    - 设置客户端SocketChannel的TCP参数
    - 注册SocketChannel到多路复用器
    
  NioSocketChannel的注册方法与ServerSocketChannel的一致，也是讲Channel注册到Reactor线程的多路复用器上。
  由于注册的位操作是0,所以，此时NioSocketChannel还不能读取客户端发送的消息，那什么时候修改监听位为OP——READ呢？
  执行完注册操作之后，便会触发ChannelReadComplete操作，
  而ChannelReadComplete在ChannelPipeline中处理：
    1. Netty的Header和Tail本身不关注ChannelReadComplete就直接透传，执行玩PipeLine的read()方法后，就直接执行HeadHandler的read方法，
    在该方法中，讲网络操作为改为读，
    