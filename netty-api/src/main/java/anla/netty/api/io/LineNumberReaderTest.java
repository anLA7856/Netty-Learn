package anla.netty.api.io;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import static anla.netty.api.io.FileReaderTest.FILE_NAME;

/**
 *
 * LineNumberReader是记录了已读取数据行号的BufferedReader。
 * 默认情况下，行号从0开始，当LineNumberReader读取到行终止符时，行号会递增(换行\n，回车\r，或者换行回车\n\r都是行终止符)。
 *
 * @author luoan
 * @version 1.0
 * @date 2020/11/12 23:32
 **/
public class LineNumberReaderTest {

    public static void main(String[] args) throws IOException {
        LineNumberReader lineNumberReader =
                new LineNumberReader(new FileReader(FILE_NAME));

        int data = lineNumberReader.read();
        while(data != -1){
            char dataChar = (char) data;
            data = lineNumberReader.read();
            int lineNumber = lineNumberReader.getLineNumber();
        }
        lineNumberReader.close();
    }
}
