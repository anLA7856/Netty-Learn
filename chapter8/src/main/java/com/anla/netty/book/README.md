## Protobuf 简易图书订购

## 规则
1. 运行`SubReqServer.java`
2. 运行`SubReqClient.java`

## 注意
```
                            socketChannel.pipeline().addLast(new ProtobufVarint32FrameDecoder());   // 用于半包处理
                            socketChannel.pipeline().addLast(new ProtobufDecoder(SubscribeReqProto.SubscribeReq.getDefaultInstance()));  // 告诉protobuf decoder 需要解码的目标类是什么
                            socketChannel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            socketChannel.pipeline().addLast(new ProtobufEncoder());
                            socketChannel.pipeline().addLast(new SubReqServerHandler());
```
由于`ProtobufDecoder`仅仅负责解码，但是不支持读半包，所以在`ProtobufDecoder`前面，一定要有能够处理读半包的解码器。
有三种方法：
1. 使用Netty提供的`ProtobufVarint32FrameDecoder`
2. 继承Netty提供的通用半包解码器`LengthFieldBasedFrameDecoder`
3. 继承`ByteToMessageDecoder`，自己处理半包消息