package anla.netty.api.io;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/11/12 23:35
 **/
public class StringReaderTest {

    public static void main(String[] args) throws IOException {
        reader();
        writer();
    }

    private static void writer() throws IOException {
        StringWriter stringWriter = new StringWriter();
        stringWriter.write("This is a text");
        String       data       = stringWriter.toString();
        StringBuffer dataBuffer = stringWriter.getBuffer();
        stringWriter.close();
    }

    private static void reader() throws IOException {
        String input = "Input String... ";
        StringReader stringReader = new StringReader(input);
        int data = stringReader.read();
        while(data != -1) {
            // doSomethingWithData(data);
            data = stringReader.read();
        }
        stringReader.close();
    }
}
