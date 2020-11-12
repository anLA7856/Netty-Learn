package anla.netty.api;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 *
 * 这两个BufferedStream可以提供一个缓冲区，能提高IO的读取速度，你可以一次读取一大块的数据，
 * 而不需要每次从网络或者磁盘中一次读取一个字节。特别是在访问大量磁盘数据时，缓冲通常会让IO快上许多。
 *
 * @author luoan
 * @version 1.0
 * @date 2020/11/12 23:23
 **/
public class BufferedInputStreamTest {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream input = new BufferedInputStream(new FileInputStream("c:\\data\\input-file.txt"));
    }
}
