## Protobuf 编解码例子

## 规则
1. 下载对应版本的protobuf compiler，这里是linux 64下版本
2. 执行命令，生成.java文件`./protoc --java_out=. ./SubscribeResp.proto `
3. 拷贝生成的bean文件到相应目录，并编写Test例子
