## 高性能之道

### 传统RPC调用性能差原因
1. 网络传输
2. 序列化性能差
3. 线程模型，一个连接一个线程。


### Netty高性能之道
1. 异步非阻塞
2. 高效的Reactor
3. 无锁化串行设计（Pipeline中handler设计）
4. 高效并发编程
5. 高性能序列化框架
6. 零拷贝，主要体现在以下
    - Netty的接收和发送ByteBuffer采用Direct Buffers，使用堆外直接对Socket进行读写，不需要对字节缓冲区二次拷贝
    - CompositeByteBuf，它将多个ByteBuf封装成一个ByteBuf，对外统一封装ByteBuf接口，实际上CompositeByteBuf就是一个ByteBuf集合，
        对外提供ByteBuf接口，添加ByteBuf，不需要做内存拷贝
    - 文件传输，Netty文件传输类DefaultFileRegion通过transferTo方法发送到目标Channel中。
7. 内存池
8. 灵活的TCP参数配置能力，对性能影响较大的配置项
    - SO——RCVBUF和SO——SNDBUF：通常建议128kb或者256kb
    - SO——TCPNODELAY:对于时延敏感应用需要关闭该优化算法
    - 软中断，如果linux支持rps，开启rps后，可以实现软中断，提升网络吞吐率。
        RPS根据数据包的源地址，目的地址以及目的和源端口，计算出hash值，然后根据这个hash值选择软中断运行cpu，从上层看，也就是将
        每个连接和cpu绑定，并通过这个hash值，来均衡中断在多个cpu上，提升网络并行处理性能。