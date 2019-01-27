## HTTP + xml协议开发应用
XML解析框架用的JiBx，需要定义xml，后才能和java bean 类解析

## JiBx使用方法
这里其实花的时间挺多的，都是使用json格式操作，所以JiBx也有好几年没有维护了，不过问题不大。
JiBx官网有详细的文档： [http://jibx.sourceforge.net/](http://jibx.sourceforge.net/)
1. 下载JiBx工具包，作用主要是由Java Bean文件，生成bind.xml以及pojo.xsd文件
下载地址[https://sourceforge.net/projects/jibx/files/](https://sourceforge.net/projects/jibx/files/)
2. 编写pojo类，入Order类，然后执行命令生成绑定的xml文件即xsd文件（注意，jibx-tools.jar作用与class文件，所以需要在target目录下）
`java -cp /home/xxx/download/jibx/jibx/lib/jibx-tools.jar:bin org.jibx.binding.generator.BindGen -s src com.anla.netty.xml.pojo.Order`
-cp:指定类运行所依赖其他类的路径，通常是类库，jar包之类，需要全路径到jar包，linux是冒号":";window上分号“;”
-s 表示源文件所在路径，也是我们命令行窗口中所在的目录
最后的是我们class二进制文件的包名。
3. 最后，需要由生成的bind.xml生成绑定编译的新的class文件JiBX_binding*.class，而此时就要用到第二步生成的bind.xml，
本来同样在target目录下：
`java -cp /home/anla7856/下载/jibx/jibx/lib/jibx-bind.jar:bin org.jibx.binding.Compile -v binding.xml`
此处binding.xml需要在target目录下，如果懒得使用自己生成binding.xml,也可以用我的binding.xml，在chapter10根目录下。
但是需要指定binding.xml文件目录