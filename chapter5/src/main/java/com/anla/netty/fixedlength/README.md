##介绍：
基于`FixedLengthFrameDecoder`解决上一个例子的粘包

## 运行
1. 运行`com.anla.netty.fixedlength.EchoServer.java`
2. 打开终端，输入`telnet localhost 8080`

## `FixedLengthFrameDecoder` 原理
是一个固定长度解码器，它能够按照指定长度对消息进行自动解码，开发者并不需要考虑TCP粘包和拆包
利用`FixedLengthFrameDecoder`，无论一次接受多少数据包，它都会啊找设置的长度进行解码，如果是半包，它会缓存该消息并等到下一个包，
直到读取到一个完整的包。