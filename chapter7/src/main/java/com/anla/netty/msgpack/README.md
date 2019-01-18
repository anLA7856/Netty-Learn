## MessagePack编解码

原先的启动方式

## 注意点
1. Object类，入UserInfo需要加入@Message注解，否则会报错
2. 支持年报和半包时候：
 - MessagePack编码器之前加入LengthFieldPrepender，它讲在ByteBuf之前增加两个字节的消息长度。
 - 在MessagePack解码器之前，增加LengthFieldBasedFrameDecoder，用于处理半包消息，这样后面的MsgpackDecoder接受到的永远是整包消息。
