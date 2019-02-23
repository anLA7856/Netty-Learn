## EventLoop和EventLoopGroup
Netty框架的主要线程就是IO线程，线程模型设计的好坏，决定了系统的吞吐量，并发性和安全性等架构质量。

### Netty的线程模型
讨论Netty线程模型的时候，一般首先会想到最经典的Reactor线程模型，尽管不同的NIO框架对Reactor模型的实现存在差异，但本质还是遵循了Reactor的基础线程模型

#### Reactor单线程模型
Reactor单线程模型，是指所有的IO操作都在同一个NIO线程上面完成，主要职责如下：
1. 作为NIO服务端，接受客户端连接
2. 作为NIO客户端，向服务端发起TCP连接
3. 读取通信对端的请求或者应答消息
4. 想通信端发送消息请求或者应答消息

Reactor模式使用的是异步非阻塞IO，所有IO操作都不会导致阻塞，理论上一个线程可以独立处理所有IO相关操作。
例如，通过Acceptor类接受客户端的TCP连接请求消息，当链路建立成功后，通过Dispatch将对应的ByteBuffer派发到指定的Handler上，进行消息解码。
用户线程消息编码后，通过NIO线程将消息发送。
单线程模式出现问题：
1. 一个NIO线程同时处理成百上千链路，性能无法支撑，即使进入了cpu 100%，也无法满足海量消息编码、解码、读取和发送
2. 负载过重后，速度将会变慢，导致大量客户端连接超时，超时后往往进行重发，家中NIO线程的负载，导致大量消息积压和处理超时，称为瓶颈
3. 可靠性，一个NIO线程意外跑非，导致整个通信模块不可用。


#### Reactor多线程模型
多线程模型与单线程模型最大区别就是有一组NIO线程处理IO操作。
1. 有一个专门NIO线程--Acceptor线程用于监听服务端，接受客户端TCP连接请求
2. 网络IO操作--读写由一个NIO线程池负责，线程池可以采用标准的JDK线程池实现，它包含一个任务队列和N个可用线程，这些NIO线程负责消息读取，
解码，编码和发送
2. 一个NIO线程可以同时处理N条链路，但是一个链路只对应一个NIO线程，防止并发操作问题


#### 主从Reactor多线程模型
主从Reactor线程模型特点：
服务端用于接收客户端连接不再是一个单独的NIO线程，而是一个独立的NIO线程池。Acceptor接收客户端TCP连接请求并处理完成后（包括接入认证）
讲新创建的SocketChannel注册到IO线程池（sub reactor线程池）的某个IO线程上，由它负责SocketChannel的读写和编解码工作。（Netty 官方推荐）


#### Netty的线程模型
通过设置不同的启动参数，Netty可以同时支持Reactor单线程模型，多线程模型和主从Reactor多线程模型
服务端启动的时候，创建了两个NioEventLoopGroup，他们实际是两个独立的Reactor线程池，一个用于接收客户端TCP连接，
另一个用于处理IO相关读写操作，或者执行系统Task，定时任务Task等。
Netty用于接收客户端请求职责如下：
1. 接收客户端TCP连接，初始化Channel参数
2. 将链路状态变更事件通知给ChannelPipeline

Netty处理IO操作的Reactor线程
1. 异步读取通信对端的数据报，发送读事件到ChannelPipeline
2. 异步发送消息到通信对端，调用Channel的消息发送接口
3. 执行系统调用Task
4. 执行任务Task，例如链路空闲状态检测定时任务

通过调整线程池的线程个数，是否共享线程池等方式，Netty的Reactor线程模型可以在单线程、多线程和主从多线程切换。


**为了提升性能，Netty在很多地方进行了无锁化设计，例如在IO线程内部进行串行操作，避免多线程内部导致性能下降问题。**
**表面上看，串行化设计似乎CPU利用率不高，并发程度不够，但是通过调整NIO线程池参数，可以同时启动多个串行化并行运行，**
**这种局部无锁化的串行线程设计相比一个队列一个多工作线程的模型性能更优**

Netty的NioEventLoop读取到消息之后，直接调用ChannelPipeline的fireChanelRead(Object msg)。只要用户不主动切换线程，一直
都是由NioEventLoop调用用户的Handler，期间不进行线程切换。这种串行化处理方式避免多线程操作导致锁的竞争，从性能上看是最优的。


最佳实践
1. 创建两个NioEventLoopGroup，用于逻辑隔离NIO Acceptor 和 NIO IO 线程
2. 尽量不要在ChannelHandler中启动用户线程（解码后用于讲POJO消息发送到后端业务线程除外）
3. 解码要放在NIO调用的解码Handler中进行，不要切换到用户线程中完成消息的解码
4. 如果业务逻辑操作非常简单，没有复杂的业务逻辑计算，没有可能导致线程被阻塞的磁盘操作、数据库操作、网络操作，可以直接在NIO线程上完成业务
逻辑编排，不需要要切换到用户线程。
5. 如果业务逻辑处理复杂，不要在NIO线程完成，建议解码后的POJO消息封装成Task，派发到业务线程池中由业务线程执行，以保证NIO线程尽快释放，
处理其他IO操作




### NioEventLoop源码

#### NioEventLoop设计原理
Netty的NioEventLoop并不是一个纯粹的IO线程，它除了负责IO的读写之外，还有
1. 系统Task，通过调用NioEventLoop的execute(Runnable task)方法实现，Netty有很多系统Task，创建他们的主要原因：当IO线程和用户
同时操作网络资源时，为了防止并发导致的锁竞争，讲用户线程封装成Task放入消息队列中，由IO线程负责执行，这样就实现了局部无锁化。
2. 定时任务，通过调用NioEventLoop的schedule(Runnable command, long delay, TimeUnit unit)方法实现
3. 它实现EventLoop接口，EventExecutorGroup接口和ScheduledExecutorService接口。

#### NioEventLoop
作为NIO框架的Reactor线程，NioEventLoop需要处理网络读写事件，因此必须聚合一个多路复用器对象。
1. 所有的逻辑操作都在for循环体内进行，只有当NioEventLoop接收到退出指令时候，才退出循环。
2. 查看消息队列队列中是否有Channel需要处理，有 就需要处理
3. 查看是否有定时任务需要执行

Select完成后，需要对结果进行判断，如果存在任意一种情况，则退出循环
1. 有Channel处于就绪状态，selectedKeys不为0,说明有读写事件需要处理
2. oldWakenUp为true
3. 系统或者用户调用了wakeup操作，唤醒当前的多路复用器
4. 消息队列有新任务处理
如果本次轮询结果为空，也没有操作或是新的消息需要处理，则说明是空轮询，有可能触发了JDK的epoll bug，它会导致空轮询，使IO线程一直
处于100%状态。
Netty修复该bug
1. 对Selector的select操作进行周期统计
2. 每完成一次空的select操作进行一次技术
3. 在某个周期如果连续发生N此空轮询，说明触发了JDK NIO 的epoll死循环bug。
如果Selector处于死循环后，需要通过重建Selector的方式让系统恢复正常：
首先通过inEventLoop方法判断是否其他线程发起的rebuildSelector，如果有其他线程发起，为了避免多线程并发操作Selector和其他资源。
需要将rebuildSelector封装成Task，放到NioEventLoop的消息队列中，有NioEventLoop负责调用，这样就避免了多线程操作的并发问题。
通过销毁旧的多路复用器，使用新建的Selector就可以解决空轮询Selector导致的IO线程CPU占用100%

如果轮询到就绪状态的SocketChannel，则需要处理网络IO事件，
处理完IO事件之后，NioEventLoop需要执行非IO操作的系统Task和定时任务，代码如下：
```
    protected void run() {
        for (;;) {
            oldWakenUp = wakenUp.getAndSet(false);
            try {
                if (hasTasks()) {
                    selectNow();
                } else {
                    select();

                    // 'wakenUp.compareAndSet(false, true)' is always evaluated
                    // before calling 'selector.wakeup()' to reduce the wake-up
                    // overhead. (Selector.wakeup() is an expensive operation.)
                    //
                    // However, there is a race condition in this approach.
                    // The race condition is triggered when 'wakenUp' is set to
                    // true too early.
                    //
                    // 'wakenUp' is set to true too early if:
                    // 1) Selector is waken up between 'wakenUp.set(false)' and
                    //    'selector.select(...)'. (BAD)
                    // 2) Selector is waken up between 'selector.select(...)' and
                    //    'if (wakenUp.get()) { ... }'. (OK)
                    //
                    // In the first case, 'wakenUp' is set to true and the
                    // following 'selector.select(...)' will wake up immediately.
                    // Until 'wakenUp' is set to false again in the next round,
                    // 'wakenUp.compareAndSet(false, true)' will fail, and therefore
                    // any attempt to wake up the Selector will fail, too, causing
                    // the following 'selector.select(...)' call to block
                    // unnecessarily.
                    //
                    // To fix this problem, we wake up the selector again if wakenUp
                    // is true immediately after selector.select(...).
                    // It is inefficient in that it wakes up the selector for both
                    // the first case (BAD - wake-up required) and the second case
                    // (OK - no wake-up required).

                    if (wakenUp.get()) {
                        selector.wakeup();
                    }
                }

                cancelledKeys = 0;

                final long ioStartTime = System.nanoTime();
                needsToSelectAgain = false;
                if (selectedKeys != null) {
                    processSelectedKeysOptimized(selectedKeys.flip());
                } else {
                    processSelectedKeysPlain(selector.selectedKeys());
                }
                final long ioTime = System.nanoTime() - ioStartTime;

                final int ioRatio = this.ioRatio;
                runAllTasks(ioTime * (100 - ioRatio) / ioRatio);

                if (isShuttingDown()) {
                    closeAll();
                    if (confirmShutdown()) {
                        break;
                    }
                }
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
由于NioEventLoop需要同时处理IO事件和非IO任务，为了保证两者都能够CPU事件被执行，Netty提供IO比例供用户定制。如果IO操作多余定时
任务和Task，则可以将IO比例增加，反之调小，默认值为50%。

执行Task Queue中原有的任务和从延时队列中赋值的已经超时或者正处于超时状态的定时任务。
由于获取系统纳秒是个耗时操作，每次循环都获取当前系统纳秒事件进行超时判断会降低性能，没60次循环一次，如果当前系统事件已经到了分配
给非IO操作的超时事件，如果退出循环。

最后，判断系统是否进入优雅停机状态，如果处于关闭状态，则需要调用closeAll方法，释放资源，并让NioEventLoop线程退出循环，结束运行。
closeAll方法：遍历所有Channel，调用它的Unsafe.close()方法关闭所有链路，释放线程池ChannelPipeline和ChannelHandler等资源。


                    