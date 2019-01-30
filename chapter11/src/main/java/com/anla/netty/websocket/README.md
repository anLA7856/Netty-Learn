## WebSocket协议开发应用

###WebSocket协议特点
1. 单一的TCP连接，采用全双工模式通信
2. 对代理、防火墙、路由器透明
3. 无头部信息、Cookie和身份验证
4. 无安全开销
5. 通过“ping/pong”帧保持链路激活
6. 服务器可以主动传递消息给客户端，不需要客户端轮询

### WebSocket连接
浏览器首先要向服务器发起一个HTTP请求，这个请求和通常的请求不同，包含了一些附加信息，其中附加头信息“Upgrade: WebSocket”，
表明这是一个申请协议升级的HTTP请求。服务器解析这些附加头信息，然后生成应答消息给客户端，返回
Upgrade,Connection,Sec-WebSocket-Accept, Sec-WebSocket-Protocol等信息

### WebSocket连接关闭
底层TCP连接，正常情况下，应该由服务器关闭。在异常下，客户端发起TCP close，服务器接受后立即发送响应。

### 运行
1. 启动`WebSOcketServer.java`
2. 浏览器中打开`WebSOcketServer.html`

### 握手过程
握手请求简单校验通过之后，开始构造握手工厂，创建握手处理类`WebSocketServerHandshaker`，通过它构造握手相应给客户端，
同时将WebSocket相关编码和解码动态加到ChannelPipeline中，用于WebSocket消息的解码。
```
if (ctx == null) {
            // this means the user use a HttpServerCodec
            ctx = p.context(HttpServerCodec.class);
            if (ctx == null) {
                promise.setFailure(
                        new IllegalStateException("No HttpDecoder and no HttpServerCodec in the pipeline"));
                return promise;
            }
            p.addBefore(ctx.name(), "wsdecoder", newWebsocketDecoder());
            p.addBefore(ctx.name(), "wsencoder", newWebSocketEncoder());
            encoderName = ctx.name();
        } else {
            p.replace(ctx.name(), "wsdecoder", newWebsocketDecoder());

            encoderName = p.context(HttpResponseEncoder.class).name();
            p.addBefore(encoderName, "wsencoder", newWebSocketEncoder());
        }
```