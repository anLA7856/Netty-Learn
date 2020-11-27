package anla.netty.api.nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static anla.netty.api.io.FileReaderTest.FILE_NAME;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/11/14 11:41
 **/
public class ByteBufferTest {

    public static void main(String[] args) throws IOException {
        buffer();
    }

    private static void buffer() throws IOException {
        RandomAccessFile aFile = new RandomAccessFile(FILE_NAME, "rw");
        FileChannel inChannel = aFile.getChannel();
        ByteBuffer buf = ByteBuffer.allocate(48);
        int bytesRead = inChannel.read(buf);
        while (bytesRead != -1) {
            buf.flip();  //准备读
            while(buf.hasRemaining()){
                System.out.print((char) buf.get()); // 一次从buf中读取一个字节，并且转化为char输出
            }
            buf.clear(); //清空buf，让其处于待写状态。
            bytesRead = inChannel.read(buf);
        }
        aFile.close();
    }
}
