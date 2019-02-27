## 可靠性

### Netty可靠性需求
1. RPC框架的基础网络通信框架：主要用于分布式节点通信和数据交换，在各个业务领域均有典型应用，例如阿里的分布式服务框架Dubbo，
    消息队列RocketMQ，大数据处理Hadoop的基础通信和序列化框架Avro
2. 私有协议的基础通信框架：例如Thrift，Dubbo
3. 公有协议HTTP协议，SMPP协议


### Netty高可靠性设计
1. 网络通信类故障
    - 客户端连接超时（Netty自实现Channel连接超时，而JDK原生则没有超时）
2. 通信对端强制关闭
    在客户端和服务端正常通信过程中，如果发生网络闪断，对方进程突然宕机或者其他非正常关闭链路事件，TCP就会发生异常，由于TCP
    是全双工，通信双方都需要关闭和释放Socket句柄才不会发生句柄泄漏。（Netty有考虑，并在底层NioByteUnsafe统一异常处理）
3. 链路关闭
    短连接协议，例如HTTP，通信双方交互数据完成后，按照双方约定由服务器关闭，TCP获取TCP关闭连接请求后，关闭自身Socket连接，双方正式断开连接。
    但是，关闭也是一种事件，需要捕获事件并处理（客户端正常关闭链路，服务端能够感知到并释放资源）
4. 定制IO故障
    Netty的处理策略是发生IO异常，底层资源由它释放，同时将链路堆栈信息以事件形式通知给上层用户，由用户对异常进行定制(fireExceptionCaught)。
    这种方式即保证即保证了处理的安全性，也向上提供了灵活的定制能力。
    

### 链路的有效性检测
当网络发生单通、连接被防火墙Hang住、长时间GC或者通信线程发生非预期异常时，会导致链路不可用且不易即使发现。
从技术层面看，要解决链路的可靠性问题，必须周期性的对链路进行有效性检测。**最通用的就是心跳检测**：


### 提供空闲检测机制分三种
1. 读空闲
2. 写空闲
3. 读写空闲


### Reactor线程保护
Reactor线程是IO操作的核心，NIO的框架的发动机，一旦发生故障，将会导致挂载在其上面的多路复用器和多个链路无法工作。

1. 异常处理要谨慎（Netty的NioEventLoop中的run方法）：
```
    protected void run() {
        for (;;) {
            oldWakenUp = wakenUp.getAndSet(false);
            try {
                xxx

            } catch (Throwable t) {
                logger.warn("Unexpected exception in the selector loop.", t);

                // Prevent possible consecutive immediate failures that lead to
                // excessive CPU consumption.
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // Ignore.
                }
            }
        }
    }
```
捕获Throwable之后，即便发生了意外未知对异常，线程也不回跑飞，它休眠1s，防止死循环的异常绕接，然后继续恢复集训。

2. 避免NIO bug
    它会导致Selector空轮询，IO线程CPU 100%，严重影响系统安全性和可靠性
    Netty的解决策略：
        - 根据该bug特征，侦测是否存在
        - 将问题Selector注册的Channel转移到新建的Selector上
        - 老的问题Selector关闭，使用新建的Selector替换
    

### 内存保护
NIO通信的内存保护主要集中在如下几点：
 - 链路总数控制
 - 单个缓冲区上限控制
 - 缓冲区内存释放
 - NIO消息发送队列的长度上线控制
 
1. 缓冲区的内存泄漏保护：
    为了防止用户因为遗漏导致内存泄漏，Netty在Pipe line的尾Handler中自动对内存进行释放，例如TailHandler中：
             ```
                     @Override
                     public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                         try {
                             logger.debug(
                                     "Discarded inbound message {} that reached at the tail of the pipeline. " +
                                             "Please check your pipeline configuration.", msg);
                         } finally {
                             ReferenceCountUtil.release(msg);
                         }
                     }
             ```
2. 缓冲区溢出保护：



### 流量整形
流量整形（Traffic Shaping）是一种主动调整流量输出速率的措施。
一个典型应用基于下游网络节点TP指标来控制本地流量的输出。流量整形与流量监管的主要区别在于，流量整形对流量监管中需要丢弃的报文进行缓存，
通常是放入缓冲区或队列内部，也称流量整形（Traffic Shaping），称TS，当令牌桶有足够的令牌时，在均匀向外发送这些被缓存的文件。

1. 全局流量整形
    Netty流量整形的原理，对每次读取到的ByteBuf可写字节数进行计算，获取当前的报文流量，然后与流量整形阈值对别，如果已经达到或者超过了阈值，
    则计算等待事件delay，将当前的ByteBuf放到定时任务Task中缓存，由定时任务线程池在延迟delay之后，继续处理该ByteBuf。
    ```
        @Override
        public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
            long size = calculateSize(msg);
            long curtime = System.currentTimeMillis();
    
            if (trafficCounter != null) {
                trafficCounter.bytesRecvFlowControl(size);
                if (readLimit == 0) {
                    // no action
                    ctx.fireChannelRead(msg);
    
                    return;
                }
    
                // compute the number of ms to wait before reopening the channel
                long wait = getTimeToWait(readLimit,
                        trafficCounter.currentReadBytes(),
                        trafficCounter.lastTime(), curtime);
                if (wait >= MINIMAL_WAIT) { // At least 10ms seems a minimal
                    // time in order to
                    // try to limit the traffic
                    if (!isSuspended(ctx)) {
                        ctx.attr(READ_SUSPENDED).set(true);
    
                        // Create a Runnable to reactive the read if needed. If one was create before it will just be
                        // reused to limit object creation
                        Attribute<Runnable> attr  = ctx.attr(REOPEN_TASK);
                        Runnable reopenTask = attr.get();
                        if (reopenTask == null) {
                            reopenTask = new ReopenReadTimerTask(ctx);
                            attr.set(reopenTask);
                        }
                        ctx.executor().schedule(reopenTask, wait,
                                TimeUnit.MILLISECONDS);
                    } else {
                        // Create a Runnable to update the next handler in the chain. If one was create before it will
                        // just be reused to limit object creation
                        Runnable bufferUpdateTask = new Runnable() {
                            @Override
                            public void run() {
                                ctx.fireChannelRead(msg);
                            }
                        };
                        ctx.executor().schedule(bufferUpdateTask, wait, TimeUnit.MILLISECONDS);
                        return;
                    }
                }
            }
            ctx.fireChannelRead(msg);
        }
    ```  
    
2. 链路级流量整形
单恋路流量整形与全局流量整形最大的区别就是，它以单个链路为作用域，可以对不同的链路设置不同的整形策略。
它的实现原理与全局流量整形预类似。
Netty还支持自定义流量整形策略，通过继承AbstractTrafficShapingHandler的doAccounting方法可以定制整形策略。

### 优雅停机接口
Java的优雅停机接口通常是注册JDK的ShutdownHook来实现，当系统接受到退出指令后，首先标记系统处于退出状态，不再接受新的消息，然后将
积压的消息处理完，最后调用资源回收接口将资源销毁，最后各线程退出执行。


## 优化建议
1. 发送队列容量上限控制
2. 回推发送失败的消息（将发送失败消息，告知系统，从而处理）      