## Future
Future异步获取操作结果

### ChannelFuture
由于Netty的future都是与异步IO相关的，因此命名为ChannelFuture，代表它与Channel操作相关

Netty强烈建议直接通过添加监听器的方式获取IO操作结果，



## Promise
Promise是可写的Future，Future自身没有写操作相关接口，Netty通过Promise对Future进行扩展，用于设置IO操作结果。

### setSuccess
需要判断当前Promise的操作结果是否已经被重置，如果已经被重置，则不允许重复设置，返回设置失败。
由于可能存在IO线程和用户线程同时操作Promise，所以设置操作结果需要加锁保护，防止并发操作。