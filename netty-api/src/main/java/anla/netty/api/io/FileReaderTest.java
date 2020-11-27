package anla.netty.api.io;

import java.io.*;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/11/12 23:18
 **/
public class FileReaderTest {

    public static void main(String[] args) throws IOException {
        reader();
        reader1();
    }

    private static void reader() throws IOException {
        Reader read = new FileReader("/home/anla7856/workspace/io.examples/pom.xml");
        int data = read.read();
        while(data != -1){
            System.out.print((char)data);     //读出的是字符，所以通过char可以强行转化得出字符
            data = read.read();
        }
    }


    private static void reader1() throws IOException{
        //用utf-8解码
        Reader read = new InputStreamReader(
                new FileInputStream(
                        new File("/home/anla7856/workspace/io.examples/pom.xml")),"UTF-8");
        int data = read.read();
        while(data != -1){
            System.out.print((char)data);
            data = read.read();
        }
    }

    private static void writer() throws IOException{
        Writer writer = new FileWriter("/home/anla7856/workspace/io.examples/output.txt");
        writer.write("Hello World Writer");    //不需要转码
        writer.close();
    }

    private static void printFileJava7() throws IOException {
        try(  FileInputStream     input         = new FileInputStream("file.txt");
              BufferedInputStream bufferedInput = new BufferedInputStream(input)
        ) {
            int data = bufferedInput.read();
            while(data != -1){
                System.out.print((char) data);
                data = bufferedInput.read();
            }
        }
    }

    private static void seek() throws IOException{
        RandomAccessFile file = new RandomAccessFile("/home/anla7856/workspace/io.examples/pom.xml", "rw");
        file.seek(200);
        long pointer = file.getFilePointer();
        file.close();
        System.out.println(pointer);
    }
}
