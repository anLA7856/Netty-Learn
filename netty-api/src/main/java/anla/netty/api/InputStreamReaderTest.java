package anla.netty.api;

import java.io.*;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/11/12 23:29
 **/
public class InputStreamReaderTest {

    private static void reader1() throws IOException {
        Reader read = new InputStreamReader(new FileInputStream(new File("/home/anla7856/workspace/io.examples/pom.xml")),"UTF-8");
        int data = read.read();
        while(data != -1){
            System.out.print((char)data);
            data = read.read();
        }
    }
}
