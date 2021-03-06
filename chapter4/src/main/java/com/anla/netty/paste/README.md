##介绍：
该例子是一个传统的Netty运行出现粘包的例子

## 运行
1. 运行`com.anla.netty.paste.TimeServer.java`
2. 运行`com.anla.netty.paste.TimeClient.java`

## 粘包问题的解决策略
由于底层TCP无法理解上层的业务数据，所以在底层是无法保证数据包不被拆分和重组的，
这个问题只能通过上层的应用协议栈设计来解决。主要有以下方法
1. 消息定长，例如每个报文固定为400字节，如果不够，则补空格
2. 在包尾增加回车换行符进行分割，例如FTP协议
3. 将消息分为消息头和消息体，消息头包含标识消息总长度，通常设计思路
为消息头的第一个字段使用int32来表示消息的总长度
4. 更加复杂的应用层协议


## 分析
1. 服务端设计，没接受一条消息，就计数一次，按照设计，最终服务端
接受的消息总数应该跟客户端发送的消息总数相同
而且请求消息删除回车换行后应该为"QUERY TIME ORDER"
2. 服务端说明只接受了3条消息，加起来总数恰好是100条，
3. 客户端应该收到100条当前系统时间的消息,但是实际上只受到了一条（说明服务端应答也发生了粘包）即，服务端只收到了3条消息，所以只会发3条消息，
由于不满足条件，所以发了两条“BAD ORDER”，

## 结果
由于程序没有考虑TCP粘包，所以当发生TCP粘包时，我们的程序就不能正常工作。