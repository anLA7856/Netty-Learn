## HTTP协议开发应用

###HTTP请求消息
HTTP请求消息由三部分组成：请求行，消息头，请求正文
请求方法的作用和区别：
 - GET： 请求获取Request-URI所标识的资源
 - POST：在Request-URI所标识的资源后附加新的提交数据 
 - HEAD： 请求获取由Request-URI锁标识资源的相应消息报头
 - PUT： 请求服务器存储一个资源，并用Request-URI作为其标识
 - DELETE： 请求服务器删除Request-URI所标识的资源
 - TRACE： 请求服务器回送收到的请求消息，主要用于测试或诊断
 - CONNECT： 保留将来使用
 - OPTION： 请求查询服务器性能，或者查询与之有关的选项和需求
 
### HTTP相应消息
也由三部分，状态行，消息报头，相应正文
 - 1xx ： 指示消息，标识请求已经被接收，继续处理
 - 2xx ： 成功，表示消息已被成功接收、理解、接受
 - 3xx ： 重定向，要求请求必须进行更进一步操作
 - 4xx ： 客户端错误，请求有语法错误或者请求无法实现
 - 5xx ： 服务器错误，服务器未能处理请求


### 例子程序
例子程序主要就是请求端口，通过netty来处理返回消息，使用http协议相关，设置头以及body内容，使用ctx发送：

### 步骤
1. 启动`HttpFIleServer.java `
2. 访问[http://127.0.0.1:8080/chapter10/](http://127.0.0.1:8080/chapter10/)

## handler处理
```
                            socketChannel.pipeline().addLast("http-decoder", new HttpRequestDecoder());  // 添加http请求消息解码器
                            // 讲多个消息转化为单个FullHttpRequest或者FullHttpResponse，原因是HTTP解码器在每个HTTP消息中会生成多个消息对象
                            socketChannel.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65535));
                            socketChannel.pipeline().addLast("http-encoder", new HttpResponseEncoder());  // 对相应消息进行编码
                            socketChannel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());  // 异步发送大的码流，即拆分发送
                            socketChannel.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(url));  // 具体文件服务器业务逻辑
```