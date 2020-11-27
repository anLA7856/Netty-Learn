package anla.netty.api.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.SequenceInputStream;

/**
 *
 * SequenceInputStream把一个或者多个InputStream整合起来，形成一个逻辑连贯的输入流。
 * 当读取SequenceInputStream时，会先从第一个输入流中读取，完成之后再从第二个输入流读取，以此推类。
 *
 * @author luoan
 * @version 1.0
 * @date 2020/11/12 23:30
 **/
public class SequenceInputStreamTest {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream input1 = new FileInputStream("/home/anla7856/workspace/io.examples/file1.txt");
        InputStream input2 = new FileInputStream("/home/anla7856/workspace/io.examples/file2.txt");
        InputStream combined = new SequenceInputStream(input1, input2);


    }
}
