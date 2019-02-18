### ByteBuffer缺点
1. ByteBuffer长度固定，一旦分配完成，它的容量不能动态扩展和收缩，当需要编码的的POJO对象大于ByteBuffer的容量时，就会发生索引月结
2. ByteBuffer只有一个标识位置指针，读写需要手工调用flip和rewind，使用者必须小心处理
3. ByteBuffer的API功能优先，高级和使用特性需要自己编程实现
4. 原生的ByteBuffer只有position一个标识位置

### ByteBuf
1. 使用readerIndex和writerIndex，readerIndex不回超过writeIndex，并且写操作不回修改readerIndex，读操作不回修改writerIndex。
2. ByteBuf的动态扩展，类似于集合类的动态扩展。

### 简单功能介绍
1. read（顺序读）
2. write（顺序写）
3. readerIndex和writerIndex分为三个区域
4. discardReadBytes，0～readerIndex之间是已读缓冲区，可以调用discardReadBytes来重用这部分空间，节约内存。这在私有协议栈消息
    解码时候非常有用，因为TCP底层可能粘包，几百个消息被TCP粘包后作为整包发送，这样通过discardReadBytes重用已经解码过的区域。
    **需要指出的是，调用discardReadBytes会发生字节数组的内存复制，所以频繁使用，会导致性能下降**
5. Readable bytes 和 Writable bytes
    ```
     *      +-------------------+------------------+------------------+
     *      | discardable bytes |  readable bytes  |  writable bytes  |
     *      +-------------------+------------------+------------------+
     *      |                   |                  |                  |
     *      0      <=      readerIndex   <=   writerIndex    <=    capacity
    ```
    当新分配、包装或者复制一个新的ByteBuf对象时，它的readerIndex为0，writerIndex为ByteBuf容量，即不可读。
6. clear
```
 *  BEFORE clear()
 *
 *      +-------------------+------------------+------------------+
 *      | discardable bytes |  readable bytes  |  writable bytes  |
 *      +-------------------+------------------+------------------+
 *      |                   |                  |                  |
 *      0      <=      readerIndex   <=   writerIndex    <=    capacity
 *
 *
 *  AFTER clear()
 *
 *      +---------------------------------------------------------+
 *      |             writable bytes (got more space)             |
 *      +---------------------------------------------------------+
 *      |                                                         |
 *      0 = readerIndex = writerIndex            <=            capacity
```    
7. mark和reset
    回滚操作，主要就是重新设置索引信息。
    mark用于讲*Index存起来，然后reset就是将当前*Index重新设置到mark存储的Index中
8. 查找操作
    查找操作，即可以查找某个字符，或者某个位置前后的字符，例如indexOf，bytesBefore，forEachByte等。
9. Derived buffer
    类似于数据库的视图。ByteBuf提供了多个解藕，让可以赋值或者切片（slice），从而返回当前ByteBuf的字段内容，
    例如duplicate，copy，slice（返回可读子缓冲区）等     
10. 转化成标准的ByteBuffer
    当然，Netty还是封装了一层，所以当底层需要转化为JDK标准ByteBuffer时候，可以利用nioBuffer方法
11. 随机读写（set和get）
    随机写或者获取某一个位置字段
    
### 源码分析  
从内存分配来看，ByteBuf分为两类
1. 堆内存（HeapByteBuf）字节缓冲区：
    优点：内存的分配和回收速度快，可以被JVM自动回收
    缺点：如果进行Socket的IO读写，需要额外做一次内存复制，讲堆内存对应内容复制到内核Channel中
2. 直接内存（DirectByteBuf）：非堆内存，分配和回收慢，和堆内存相反
从内存回收看，
1. 基于对象池的ByteBuf和普通ByteBuf。
    内存池的ByteBuf，提升了内存使用效率，降低高负载的频繁GC，在高负载，大并发更加平稳

### AbstractByteBuf源码分析
在里面有个leakDetector，定义为static，意味着所有ByteBuf实例共享同一个ResourceLeakDetector，用于
检测对象是否泄漏等。    
```
static final ResourceLeakDetector<ByteBuf> leakDetector = new ResourceLeakDetector<ByteBuf>(ByteBuf.class);
```
在其中并没有特定的byte数组或者DirectByteBuffer，因为具体由哪种实现，交由子类实现。

#### 读，写
1. 对可用空间进行校验
2. 复制或者另一方面读入
3. 操作索引
4. 重用缓冲区
5. 丢弃skipBytes

### AbstractReferenceCountedByteBuf分析
主要对引用进行计数，类似于JVM内存回收的对象引用计数器，用于跟踪对象的分配和销毁，做自动内存回收
### UnpooledHeapByteBuf源码
UnpooledHeapByteBuf基于堆内存进行内存分配和字节缓冲区，它没有基于对象池技术，意味着每次IO读写都会创建一个新的UNpooledHeapByteBuf，
频繁进行大块内存分配和回收对性能会造成一定影响。但是比堆外内存申请释放，成本会低。
1. 成员变量
    ByteBufAllocator
    byte[]
    ByteBuffer
2. 动态扩展缓冲区
3. 字节数组复制
4. 转换成JDK ByteBuffer
### UnpooledDirectByteBuf
和UnpooledHeapByteBuf实现原理相同，不同指出就是内部缓冲区由java.nio.DirectByteBuffer实现


### PooledByteBuf内存原理分析
以内存池来实现ByteBuf
1. PoolArena，指内存中连续的一大块内存，一开始申请，而不用再次申请使用。
    Netty中由多个Chunk组成的大块内存区域，而每个Chunk由多个Page组成
2. PoolChunk，Chunk用来组织和管理多个Page的内存分配和释放，Chunk中的Page被构造成一颗二叉树。
3. PoolSubpage，对于小于一个Page的内存，Netty在Page中完成分配，每个page会被切分成大小相等的多个存储块，
    而存储块的大小由第一次申请的内存块大小决定。
4. 内存回收策略，通过状态位来标识内存是否可用。

### PooledDirectByteBuf
与UnPooledDirectByteBuf唯一不同就是，缓冲区分配和销毁策略不同
1. 创建爱你字节缓冲区，不能直接new，而是从内存池中获取，然后设置引用计数器的值。
2. 复制新的字节缓冲区


### ByteBuf相关辅助类功能介绍
1. ByteBufHolder
    ByteBufHolder是ByteBuf的容器，提供了一些其他使用方法，可以继承ByteBufHolder按需实现
2. ByteBufAllocator
    字节缓冲区分配器。按照不同实现，分为基于内存池的PooledByteBufAllocator和基于普通字节缓冲区UnpooledByteBufAllocator。
3. CompositeByteBuf
    将多个ByteBuf实例组装在一起，形成一个统一的视图，有点类似于数据库中的视图
4. ByteBufUtil
    提供了一系列静态方法用于操作ByteBuf对象，相当有用的就是字符串的编码和解码
    - `public static ByteBuf encodeString(ByteBufAllocator alloc, CharBuffer src, Charset charset)` 编码
    - `static String decodeString(ByteBuffer src, Charset charset)` 解码














