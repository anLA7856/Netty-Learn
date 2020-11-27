package anla.netty.api.io;

import java.io.*;

import static anla.netty.api.io.FileReaderTest.FILE_NAME;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/11/12 23:16
 **/
public class FileInputStreamTest {

    public static void main(String[] args) throws IOException {
        read();
        write();
    }


    private static void read() throws FileNotFoundException, IOException {
        File file = new File(FILE_NAME);
        byte[] data = new byte[1024];
        int length = new FileInputStream(file).read(data);
        System.out.println(length);
        InputStream is = new ByteArrayInputStream(data);
        String content = new String(data, "UTF-8");    //输出的则为pom文件内容
        System.out.println(content);
        int temp = is.read();
        while(temp != -1){
            System.out.print(temp);             //这里输出的是字节符号
            temp = is.read();
        }
        is.close();
    }

    private static void write() throws UnsupportedEncodingException, IOException {
        File file = new File(FILE_NAME);
        FileOutputStream fos = new FileOutputStream(file);
        OutputStream output = new BufferedOutputStream(fos);
        output.write("This text is converted to bytes".getBytes("utf-8"));
        output.close();
        fos.close();
    }
}
