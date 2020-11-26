package anla.netty.api.io;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

/**
 *
 * PrintStream允许你把格式化数据写入到底层OutputStream中。比如，写入格式化成文本的int，
 * long以及其他原始数据类型到输出流中，而非它们的字节数据。
 * @author luoan
 * @version 1.0
 * @date 2020/11/12 23:31
 **/
public class PrintStreamTest {

    private static void printStream(){
        String s = "printfStream";
        OutputStream os = System.out;
        PrintStream output = new PrintStream(os);
        output.print(true);
        output.printf(Locale.UK, "Text + data: %s$", s);
        output.print((float) 123.456);
        output.close();
    }
}
