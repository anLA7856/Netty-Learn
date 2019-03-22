## Netty架构剖析

1. Reactor 通信调度层
2. 职责链ChannelPipeline
3. 业务逻辑编排层 Service ChannelHandler

### 影响高性能因素
软件：
1. 架构不合理导致
2. 编码实现不合理，例如锁的不恰当使用
硬件：
1. 服务器配置低
2. 贷款，磁盘IOPS等限制导致IO操作性能差
3. 测试环境共用导致被测试产品收到影响


### Netty架构设计如何实现高性能
1. 非阻塞IO库，基于Reactor模式，解决了传统同步阻塞IO模式下服务端无法平滑处理线性增长的客户端问题
2. TCP接收和发送使用直接内存代替堆内存，避免了内存复制
3. 支持内存池方式循环利用ByteBuf，避免了频繁创建和销毁ByteBuf
4. 可配值IO线程数，TCP参数，为不同用户场景提供定制化调优参数，满足不同性能场景
5. **采用环形数组缓冲区实现无锁化并发编程，代替传统线程安全容器或者锁**
6. 合理使用线程安全容器，原子类等
7. 关键资源处理使用单线程串行化方式，避免多线程并发带来额外cpu消耗
8. 通过引用计数器及时申请释放不再被引用对象，细粒度内存管理降低了GC频率，减少GC带来的时延


### 可靠性
1. 链路有效性检测，netty提供分为（读空闲超时机制和写空闲超时机制）
2. 内存保护机制
    - 通过引用计数器对Netty的ByteBuf等内置对象进行细粒度内存申请和释放，对非法对象进行检测和保护
    - 通过内存池来重用ByteBuf，节省内存
    - 可设置内存容量上线，包括ByteBuf、线程池线程数等
3. 优雅停机
    优雅停机功能是指，当系统退出时，JVM通过注册的Shutdown Hook拦截到退出信号量，然后执行退出操作，释放相关模块
    的占用资源，讲缓冲区消息处理完成或者清空，讲待刷新的数据持久化到磁盘中，等资源回收和缓冲区消息处理完成之后，在退出，
    有最大时间T，这个事件还没有退出，就使用kill -9强杀
4. 可定制性
5. 可扩展性，可以方便实现多种用户层协议        