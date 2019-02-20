### Channel功能
io.netty.channel.Channel是Netty的网络操作抽象类，包含了基本操作，以及Netty框架的一些功能，入获取Channel的EventLoop，获取分配器
ByteBufAllocator等。

#### 工作原理
1. 在Channel的接口层，采用Facade模式封装，讲网络IO，以及相关联其他类封装起来，对外统一提供
2. Channel提供接口大而全，为SocketChannel和ServerSocketChannel提供统一视图

#### 主要功能介绍
1. 网络IO操作，包括read，write，writeAndFlush，close，disconnect，connect，bind，config，isOpen等等。
2. 其他常用api，
    如eventLoop()，
    如metadata()，获取TCP参数配置
    如parent()，对于服务端而言，它的父Channel为空，对于客户端而言，它的父Channel就是创建它的ServerSocketChannel
    如id()，它返回ChannelId对象，ChannelId是唯一标识
    
####AbstractChannel源码分析
1. 成员变量
2. 核心源码，即事件驱动到ChannelPipeline传播源码。（直接调用）
    ```
     @Override
        public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
            return pipeline.connect(remoteAddress, localAddress);
        }
    
        @Override
        public ChannelFuture disconnect() {
            return pipeline.disconnect();
        }
    
        @Override
        public ChannelFuture close() {
            return pipeline.close();
        }
    
        @Override
        public Channel flush() {
            pipeline.flush();
            return this;
        }
    
        @Override
        public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
            return pipeline.bind(localAddress, promise);
        }
    ```
    
####AbstractNioChannel 源码
1. 成员变量，主要是Selectkey，以及SelectableChannel相关
2. 核心源码分析，
     - Channel注册。
     
####AbstractNioByteChannel
1. 成员变量，Runnable类型的flushTask，机型继续写半包
2. 核心源码，doWrite（注重对写半包处理）


####AbstractNioMessageChannel
1. 核心源码，doWrite（循环体消息进行消息发送，判断是否成功）

####AbstractNioMessageServerChannel
1. 定义了一个EventLoopGroup，用于给新介入的NioSocketChannel分配EventLoop。

####NioServerSocketChannel
配置以下ServerSocketChannel的TCP参数，例如backlog等。


####NioSocketChannel源码
1. 连接操作
2. 写半包
3. duxie1caozuo1
    
    
    
### Unsafe功能    
Unsafe接口实际上是Channel接口的辅助接口，它不应该被用户代码直接调用。
实际的IO读写都是由Unsafe接口负责完成的。


#### AbstractUnsafe源码分析
1. register方法
2. bind方法
3. disconnect方法
4. close方法
5. write方法
6. flush方法


#### AbstractNioUnsafe源码
1. AbstractNioUnsafe是AbstractUnsafe类的NIO实现，主要实现了connect，finishConnect。

#### NioByteUnsafe源码
1. read方法