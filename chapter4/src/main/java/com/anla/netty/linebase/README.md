##介绍：
基于`LineBaseFrameDecoder`解决上一个例子的粘包

## 运行
1. 运行`com.anla.netty.linebase.TimeServer.java`
2. 运行`com.anla.netty.linebase.TimeClient.java`

通过增加`LineBaseFrameDecoder`和`StringDecoder`，成功解决了TCP粘包导致的读半包问题，程序运行结果完全符合预期


## `LineBaseFrameDecoder`原理
依次遍历ByteBuf中的可读字节，判断是"\n"或者"\r\n"，如果有，就以此为结束位置，从可读索引到结束位置区间的字节就组成了一行。
它是以换行符为结束标志的解码器，支持携带结束符或者不屑带结束符两种解码方式，同时支持配置单行最大成都（本程序配置1024）。
这样依赖，如果连续读取到最大长度后，还没有发现换行符，那么就会抛出异常，同时忽略到之前读到的异常码流