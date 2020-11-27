package anla.netty.api.io;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * 当你要读取的数据中包含了int，long，float，double这样的基本类型变量时，DataInputStream可以很方便地处理这些数据。
 * @author luoan
 * @version 1.0
 * @date 2020/11/12 23:25
 **/
public class DataInputStreamTest {

    private static void readData() throws IOException {
        DataInputStream input = new DataInputStream(
                new FileInputStream("/home/anla7856/workspace/io.examples/pom.xml"));
        int data = input.readInt();
        System.out.println(data);
    }
}
