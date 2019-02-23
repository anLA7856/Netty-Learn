### ChannelPipeline
Netty的ChannelPipeline和ChannelHandler机制，类似于Servlet和Filter过滤器，这类拦截器实际上是职责链的一种变形。

#### ChannelPipeline功能说明
ChannelPipeline是ChannelHandler的，它负责ChannelHandler的管理和事件拦截与调度。


##### ChannelPipeline的事件处理过程
1. 底层SocketChannel read()方法读取ByteBuf，触发ChannelRead事件，由IO线程NioEventLoop调用ChannelPipeline的
fireChannelRead(Object msg)方法，将消息（ByteBuf）传输到ChannelPipeline中。
2. 消息依次被HeadHandler，ChannelHandler1, ChannelHandler2......TailHandler拦截处理，在这个过程中，任何ChannelHandler都可以
中断当前流程，结束传递
3. 调用ChannelHandlerContext的write方法发送消息，消息从TailHandler开始，途径ChannelHandlerN......ChannelHandler1,HeadHandler，
最终被添加到消息发送缓冲区中等待刷新和发送。
终端消息传递：当编码失败时，需要终端流程，构造异常的Future返回
```
 *  +---------------------------------------------------+---------------+
 *  |                           ChannelPipeline         |               |
 *  |                                                  \|/              |
 *  |    +----------------------------------------------+----------+    |
 *  |    |                   ChannelHandler  N                     |    |
 *  |    +----------+-----------------------------------+----------+    |
 *  |              /|\                                  |               |
 *  |               |                                  \|/              |
 *  |    +----------+-----------------------------------+----------+    |
 *  |    |                   ChannelHandler N-1                    |    |
 *  |    +----------+-----------------------------------+----------+    |
 *  |              /|\                                  .               |
 *  |               .                                   .               |
 *  | ChannelHandlerContext.fireIN_EVT() ChannelHandlerContext.OUT_EVT()|
 *  |          [method call]                      [method call]         |
 *  |               .                                   .               |
 *  |               .                                  \|/              |
 *  |    +----------+-----------------------------------+----------+    |
 *  |    |                   ChannelHandler  2                     |    |
 *  |    +----------+-----------------------------------+----------+    |
 *  |              /|\                                  |               |
 *  |               |                                  \|/              |
 *  |    +----------+-----------------------------------+----------+    |
 *  |    |                   ChannelHandler  1                     |    |
 *  |    +----------+-----------------------------------+----------+    |
 *  |              /|\                                  |               |
 *  +---------------+-----------------------------------+---------------+
 *                  |                                  \|/
 *  +---------------+-----------------------------------+---------------+
 *  |               |                                   |               |
 *  |       [ Socket.read() ]                    [ Socket.write() ]     |
 *  |                                                                   |
 *  |  Netty Internal I/O Threads (Transport Implementation)            |
 *  +-------------------------------------------------------------------+
```




Netty的事件分为inbound和outbound，inbound事件通常由IO线程触发，例如TCP链路建立，链路关闭，读事件，异常通知事件
而outbound事件通常由用户主动发起的网络IO操作，例如用户发起的连接操作，绑定操作，消息发送等。

#### 构建pipeline
用户不需要自己创建pipeline，因为使用ServerBootstrap或者Bootstrap启动服务端或者客户端，Netty会为每个Channel创建一个独立的pipeline，
对于使用者，只需要将自定义拦截器加入到pipeline中即可。

ChannelPipeline支持运行态动态的添加或者删除ChannelHandler，某些场景很实用，例如在业务高峰期需要做拥塞保护，就可以添加相应的Handler

ChannelPipeline是线程安全的(Synchronized)，这意味着N个业务线程可以并发操作ChannelPipeline而不存在多线程并发问题，但是ChannelHandler却不是线程安全
的，这意味着，用户仍需要自己保证ChannelHandler的线程安全。


#### ChannelPipeline源码分析
1. addBefore，存在两种并发场景
    - IO线程和用户业务线程并发访问
    - 用户多个线程之间并发访问（多个地方要添加Handler）
    1. 在链上新增一个handler
    2. 成功后，以新增ChannelHandler等参数构造一个新的DefaultChannelContext。
    3. 将DefaultChannelHandlerContext添加到当前的pipeline中。（对ChannelHandlerContext做重复性校验,代码如下）
    如果ChannelHandlerContext不是可以在多个ChannelPipeline中共享的，且已经被添加到ChannelPipeline中，则抛异常。
    ```
        private static void checkMultiplicity(ChannelHandlerContext ctx) {
            ChannelHandler handler = ctx.handler();
            if (handler instanceof ChannelHandlerAdapter) {
                ChannelHandlerAdapter h = (ChannelHandlerAdapter) handler;
                if (!h.isSharable() && h.added) {
                    throw new ChannelPipelineException(
                            h.getClass().getName() +
                            " is not a @Sharable handler, so can't be added or removed multiple times.");
                }
                h.added = true;
            }
        }
    ```    
    4. 添加成功后，发送ChannelHandlerContext通知消息。
    
2. ChannelPipeline的inbound事件
pipeline中以fireXXX命名的方法都是从IO线程流向用户业务Handler的inbound事件，他们的实现因功能而异，淡出里步骤类似。
    - 调用HeadHandler对应的fireXXX方法
    - 执行事件相关逻辑操作。
    
    例如ChannelPipeline的fireChannelActive方法：
    ```
        @Override
        public ChannelPipeline fireChannelActive() {
            head.fireChannelActive();
    
            if (channel.config().isAutoRead()) {
                channel.read();
            }
    
            return this;
        }
    ```
    
2. ChannelPipeline的outbound事件
由用户线程或者代码发起的IO操作，称为outbound事件。

Pipeline本身并不直接进行IO操作，由前面只是，真正进行IO操作的是Channel和Unsafe，例如connect操作，最终由TailHandler
```
    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return tail.connect(remoteAddress, localAddress);
    }
    
    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return connect(remoteAddress, localAddress, newPromise());
    }
```    
最终由HeadHandler调用Unsafe的Unsafe的connect方法，发起真正的连接，pipeline仅仅负责调度。

#### ChannelHandler功能说明
ChannelHandler负责对IO事件或操作进行拦截和处理，可以选择拦截自己感兴趣事件。
支持注解：
    - Sharable：多个ChannelPipeline共用同一个ChannelHandler
    - Skip：被Skip注解方法不会被调用。
    
可以看到ChannelHandlerAdapter的大部分方法都被@Skip注解了，所以在执行过程中会被忽略。



#### ByteToMessageDecoder
将ByteBuf解码成POJO对象，用户继承ByteToMessageDecoder，只需要实现decode抽象方法，即可完成ByteBuf到POJO对象解码
**ByteToMessage没有考虑到TCP粘包和组包处理**，读半包需要自己处理，所以一般不会直接继承ByteToMessageDecoder


#### MessageToMessageDecoder
MessageToMessageDecoder可以看作Netty的二次解码其，职责讲agiel对象二次解码为其他对象。
因为从TCP流中的ByteBuf到Java对象是一次解码，而对Java对象根据某些规则做第二次解码，解码成符合要求的POJO对象
例如开始的HTTP+XML协议，第一次解码是讲字节数组解码成HttpRequest对象，第二次讲其里面内容解码为POJO对象


#### LengthFieldBasedFrameDecoder
在TCP粘包导致解码时候，需要考虑如何处理半包问题。
如果消息通过长度进行区分，LengthFieldBasedFrameDecoder都可以自动处理粘包和半包问题，只需要传入正确的参数，即可轻松搞定读半包问题
 - lengthFieldOffset = 0
 - lengthFieldLength = 2
 - lengthAdjustment = 0
 - initialBytesToStrip = 0
 ```
  * BEFORE DECODE (14 bytes)         AFTER DECODE (14 bytes)
  * +--------+----------------+      +--------+----------------+
  * | Length | Actual Content |----->| Length | Actual Content |
  * | 0x000C | "HELLO, WORLD" |      | 0x000C | "HELLO, WORLD" |
  * +--------+----------------+      +--------+----------------+
 ```

 - lengthFieldOffset = 0
 - lengthFieldLength = 2
 - lengthAdjustment = 0
 - initialBytesToStrip = 2
 ```
 * BEFORE DECODE (14 bytes)         AFTER DECODE (12 bytes)
 * +--------+----------------+      +----------------+
 * | Length | Actual Content |----->| Actual Content |
 * | 0x000C | "HELLO, WORLD" |      | "HELLO, WORLD" |
 * +--------+----------------+      +----------------+
 ```
 
 
 
 - lengthFieldOffset = 0
 - lengthFieldLength = 2
 - lengthAdjustment = -2
 - initialBytesToStrip = 0
 ```
 * BEFORE DECODE (14 bytes)         AFTER DECODE (14 bytes)
 * +--------+----------------+      +--------+----------------+
 * | Length | Actual Content |----->| Length | Actual Content |
 * | 0x000E | "HELLO, WORLD" |      | 0x000E | "HELLO, WORLD" |
 * +--------+----------------+      +--------+----------------+
 ```


 - lengthFieldOffset = 2
 - lengthFieldLength = 3
 - lengthAdjustment = 0
 - initialBytesToStrip = 0
 ```
 * BEFORE DECODE (17 bytes)                      AFTER DECODE (17 bytes)
 * +----------+----------+----------------+      +----------+----------+----------------+
 * | Header 1 |  Length  | Actual Content |----->| Header 1 |  Length  | Actual Content |
 * |  0xCAFE  | 0x00000C | "HELLO, WORLD" |      |  0xCAFE  | 0x00000C | "HELLO, WORLD" |
 * +----------+----------+----------------+      +----------+----------+----------------+
 ```

 - lengthFieldOffset = 1
 - lengthFieldLength = 2
 - lengthAdjustment = 1
 - initialBytesToStrip = 3
 ```
 * BEFORE DECODE (17 bytes)                      AFTER DECODE (17 bytes)
 * +----------+----------+----------------+      +----------+----------+----------------+
 * |  Length  | Header 1 | Actual Content |----->|  Length  | Header 1 | Actual Content |
 * | 0x00000C |  0xCAFE  | "HELLO, WORLD" |      | 0x00000C |  0xCAFE  | "HELLO, WORLD" |
 * +----------+----------+----------------+      +----------+----------+----------------+
 ```
在pipeline中增加LineBasedFrameDecoder解码器，指定正确的参数组合，他就可以讲Netty的ByteBuf解码成单个整包消息，后面的业务解码器拿到
的就是完整的数据报，正常解码即可

#### MessageToByteEncoder
将POJO编码成ByteBuf

#### MessageToMessageEncoder
二次编码器，结合HTTP+XML例子


#### LengthFieldPrepender
如果协议第一个字段为长度字段，Netty提供了LengthFieldPrepender编码器，它可以计算待发送消息的二进制字节长度，将该长度添加到ByteBuf
缓冲区头中。
通过LengthFieldPrepender，讲长度消息写入到ByteBuf的前2个字节中，编码后就是“长度字段+原消息”
通过设置LengthFieldPrepender为true，消息长度将包含长度本身占用字节。
```
 * For example, <tt>{@link LengthFieldPrepender}(2)</tt> will encode the
 * following 12-bytes string:
 * <pre>
 * +----------------+
 * | "HELLO, WORLD" |
 * +----------------+
 * </pre>
 * into the following:
 * <pre>
 * +--------+----------------+
 * + 0x000C | "HELLO, WORLD" |
 * +--------+----------------+
 * </pre>
 * If you turned on the {@code lengthIncludesLengthFieldLength} flag in the
 * constructor, the encoded data would look like the following
 * (12 (original data) + 2 (prepended data) = 14 (0xE)):
 * <pre>
 * +--------+----------------+
 * + 0x000E | "HELLO, WORLD" |
 * +--------+----------------+
```


### ChannelHandler源码分析

#### ChannelHandler的类继承关系图
1. ChannelPipeline的系统ChannelHandler，用于IO操作和对事件进行预处理，对用户不可见，这类ChannelHandler主要包括HeadHandler和TailHandler
2. 编解码ChannelChannel，包括ByteToMessageCodec，MessageToMessageDecoder。本身又包括很多子类
3. 其他系统功能性ChannelHandler，包括流量整形Handler，读写超时Handler，日志Handler


#### ByteToMessageDecoder源码分析
ByteToMessageDecoder用于将ByteBuf解码成POJO对象。
1. 判断是否为ByteBuf对象
2. 是否有半包消息
3. 对ByteBuf循环解码，让子类进行解码。

解码后需要对当前的pipeline状态和解码结果进行判断，如果当前的ChannelHandlerContext已经被移除，则不能进行解码，直接退出循环。
如果输出的out列表长度没变化，说明解码没有成功，需要针对不同场景进行判断
1. 如果用户解码器没有消费ByteBuf，则说明是个半包消息，则需要IO线程继续读取后续数据报
2. 如果解码器消费了ByteBuf，说明可以解码可以继续进行
3. 如果用户没有消费ByteBuf，但是解码器却多出一个或者多个对象，则会被认为非法，需要抛出DecoderException（是否消费，根据ByteBuf的index判断）
4. 最后通过isSingleDecode判断，如果是单条消息解码器，第一次解码完成后就退出循环



#### MessageToMessageDecoder
MessageToMessageDecoder负责将一个POJO对象解码成另一个POJO对象。
先通过RecyclableArrayList创建一个新的可循换利用的RecyclableArrayList，然后对解码的消息进行判断，通过类型参数校验器查看可接受类型，
校验通过就直接调用子类的decode方法。
解码完成后，就调用ReferenceCountUtil的release方法释放被解码的msg对象。

如果需要解码的对象不是当前解码器可接受和处理的类型，则将它加入到RecyclableArrayList中不进行解码。
```
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RecyclableArrayList out = RecyclableArrayList.newInstance();
        try {
            if (acceptInboundMessage(msg)) {
                @SuppressWarnings("unchecked")
                I cast = (I) msg;
                try {
                    decode(ctx, cast, out);
                } finally {
                    ReferenceCountUtil.release(cast);
                }
            } else {
                out.add(msg);
            }
        } catch (DecoderException e) {
            throw e;
        } catch (Exception e) {
            throw new DecoderException(e);
        } finally {
            int size = out.size();
            for (int i = 0; i < size; i ++) {
                ctx.fireChannelRead(out.get(i));
            }
            out.recycle();
        }
    }
```

最后，对RecyclableArrayList进行遍历，循环调用ChannelHandlerContext的fireChannelRead方法，通知后续的ChannelHandler继续进行处理。
循环通知完成之后，通过recycle方法释放RecyclableArrayList。


#### LengthFieldBasedFrameDecoder
最重要，最通用，基于消息长度的半包解码器。
如果解码成功，就加入到out中
```
    protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object decoded = this.decode(ctx, in);
        if (decoded != null) {
            out.add(decoded);
        }

    }
```
再由decoder(ctx, in)方法。
1. 判断是否需要丢弃当前可读的字节缓冲区。
2. 然后按照配置的偏移量以及可读字节数进行读取
3. 按照lengthFieldEndOffset和lengthAdjustment字段进行修正

如果当前的可读字节数小于frameLength，则说明是个半包消息，需要返回空，由IO线程继续读取后续的数据报，等待下次解码。


#### MessageToByteEncoder
负责讲POJO对象编码成ByteBuf，以便网络进行传输


#### MessageToMessageEncoder
负责将一个POJO对象编码成另一个POJO对象，例如将XML Document对象编码成XML格式字符串


#### LengthFieldPrepender
负责在待发送的ByteBuf消息头中增加一个长度字段来标识消息长度，它简化了用户编码的开发，使得不需要额外的设置这个长度。










问题：ChannelHandler里面的半包发送接受，和Channel里面半包发送接收有啥区别
    