## Netty私有协议栈

### 运行方式
启动NettyServer
启动NettyClient

###前言
由于现代软件系统的复杂性，一个大型软件系统往往被人为拆分为多个模块，另外随着移动互联网的兴起，网站规模越来越大，业务功能越来越多，
为了能够支撑业务发展，往往需要集群分布式部署，这样，各个模块之间就要进行跨节点通信。
主要有以下四种方式：
1. 通过RMI进行远程服务调用
2. 通过Java的Socket+Java序列化进行跨节点调用
3. 利用一些开源的RPC框架进行远程服务调用，例如Facebook的Thrift、Apache的WebService
4. 利用标准的公有协议进行跨节点服务调用，例如HTTP+XML，RESTFUL+JSON方式

### 本例子具有功能
1. 基于Netty的NIO通信框架，提供高性能的异步通信能力
2. 提供消息的编解码框架，可以实现pojo的序列化与反序列化
3. 提供基于IP地址的白名单介入认证机制
4. 链路的有效性校验机制
5. 链路的断连重连机制


### 心跳
客户端和服务端都可以维持心跳，即都可以发送心跳消息，来验证是否对面通信服务仍存活。

### 可靠性设计
1. 心跳机制
2. 重连机制
3. 重复登录保护
4. 消息缓存重发

### 安全性设计
1. ip白名单
2. ssl/tsl

### 可扩展性设计
使用Netty中的attachment来增加可选附件。
Netty协议栈架构需要具备一定的扩展能力，例如统一的消息拦截，接口日志，安全，加密解密等，方便的添加或者删除，
不需要修改之前的逻辑代码，类似于Servlet的FilterChain和AOP，但考虑到性能因素，不推荐通过AOP来实现功能扩展。

### 编写调试中问题
1. Client端一直显示无法连接到服务器，并且`Marshalling.getProvidedMarshallerFactory("serial");` 一直返回null
    原因：没有引入`Marshalling-serial`包
2. 连接上后，`Client`端发送的消息，`Server`一直无法接收。
    原因：`Encode`类写的有问题，导致实际无法发送信息，由于是根据书本上代码来，经过仔细检查，发现由于
    权威指南上`NettyMessageEncoder`继承`MessageToMessageEncoder`，而实际上应该继承`MessageToByteEncoder`    