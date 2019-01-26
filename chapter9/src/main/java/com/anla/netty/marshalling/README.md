## JBoss Marshalling 简易图书订购

## 规则
1. 运行`SubReqServer.java`
2. 运行`SubReqClient.java`

## 注意
```$xslt
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");  // 表示创建Java序列化工厂对象
```
参数`serial`表示创建的是Java序列化工厂对象，它由`jboss-marshalling-serial-*`提供
所以在maven引入时候，需要引入api和serial两种jar包
具体看github，也知二者并不在同一个工程下：
[https://github.com/jboss-remoting/jboss-marshalling](https://github.com/jboss-remoting/jboss-marshalling)