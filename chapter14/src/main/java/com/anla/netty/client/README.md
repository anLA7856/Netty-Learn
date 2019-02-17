### Netty客户端创建的步骤
1. 创建Bootstrap实例，通过API设置创建客户端相关参数，异步发起客户端连接
2. 创建处理客户端连接，IO读写的Reactor线程组NioEventLoopGroup，可以通过构造函数执行IO线程个数，默认为CPU内核2倍
3. 通过Bootstrap的ChannelFactory和用户指定Channel类型创建客户端连接的NioSocketChannel
4. 创建默认的Channel Handler Pipeline，用于调度和执行网络事件
5. 异步发起TCP连接，判断连接是否成功，如果成功，则直接讲NioSocketChannel注册到多路复用器上，监听读操作位，用于数据报读取和消息发送。
  如果没有立即连接成功，则注册连接监听位到多路复用器，等待连接结果。
6. 注册网络监听状态到多路复用器
7. 由多路复用器在IO现场中轮询各Channel，处理连接结果
8. 如果连接成功，设置Future，发送事件，触发ChannelPipeline
9. 由ChannelPipeline调度执行系统和用户的ChannelHandler，执行业务逻辑




### 创建客户端源码
1. 设置EventLoopGroup接口，客户端对于服务端，只需要一个处理I/O的读写线程组。
2. 使用channel，传入NioSocketChannel对象。
3. TCP参数设置，这些参数包括，接受和发送缓冲区大小，连接超时事件等。
    - SO——TIMEOUT：控制读取操作将阻塞多少毫秒，如果返回值为0,计数器被禁止，该线程组讲无限期阻塞
    - SO——SNDBUF：套接字发送缓冲区大小
    - SO——RCVBUF：套接字使用的接受缓冲区大小
    - SO——REUSEADDR：用于决定如果网络上仍有数据向旧的ServerSocket传数据，是否允许新的ServerSocket绑定到与旧的ServerSocket同样的端口上。
    - CONNECT——TIMEOUT——MILLIS：客户端连接超时事件
    - TCP——NODELAY：激活或禁止TCP——NODELAY套接字选项，如果是时延敏感型，建议关闭
4. 使用handler接口，Bootstrap为了简化Handler的编排，提供了ChannelInitializer，它继承了ChannelHandlerAdapter，当TCP链路注册成功
    之后，调用iniChannel接口，用于设置用户的ChannelHandler。
    ```
        public final void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            ChannelPipeline pipeline = ctx.pipeline();
            boolean success = false;
            try {
                initChannel((C) ctx.channel());
                pipeline.remove(this);
                ctx.fireChannelRegistered();
                success = true;
            } catch (Throwable t) {
                logger.warn("Failed to initialize a channel. Closing: " + ctx.channel(), t);
            } finally {
                if (pipeline.context(this) != null) {
                    pipeline.remove(this);
                }
                if (!success) {
                    ctx.close();
                }
            }
        }
    ```    
5. 发起客户端连接`ChannelFuture f = b.connect(host, port).sync()`

### 客户端连接操作
1. 首先要创建和初始化NioSocketChannel
2. 在创建Channel时候，需要从NioEventLoopGroup中获取NioEventLoop，然后作为参数创建Channel
3. 讲Channel注册到Selector上
4. 异步发起TCP连接，在SocketChannel执行connect操作后，有以下三种结果。
    1. 连接成功，返回True
    2. 暂时没有连接上，服务器没有返回SYN应答，连接结果不稳定，返回false
    3. 连接失败，直接抛出异常
    如果第二种，则会讲NioSocketChannel中的selectionKey设置为OP——CONNECT，监听连接结果。
5. 异步连接返回后，如果连接成功，则会触发ChannelActive事件，在ChannelActive事件中，最终会将NioSocketChannel中的selectKey设置为
    SelectionKey.OP_READ，用于监听网络读操作。
    
### 异步连接结果通知
NioEventLoop的Selector轮询客户端连接Channel，当服务器返回握手应答后，对结果进行判断，即为上小端的4.        

   