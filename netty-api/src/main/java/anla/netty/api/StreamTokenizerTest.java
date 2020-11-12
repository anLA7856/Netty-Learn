package anla.netty.api;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/11/12 23:34
 **/
public class StreamTokenizerTest {
    private static void streamTokenizer() throws IOException {
        StreamTokenizer streamTokenizer = new StreamTokenizer(
                new StringReader("Mary had 1 little lamb..."));
        while(streamTokenizer.nextToken() != StreamTokenizer.TT_EOF){
            if(streamTokenizer.ttype == StreamTokenizer.TT_WORD) {
                System.out.println(streamTokenizer.sval); //sval 如果读取到的符号是字符串类型，该变量的值就是读取到的字符串的值
            } else if(streamTokenizer.ttype == StreamTokenizer.TT_NUMBER) {
                System.out.println(streamTokenizer.nval); //nval 如果读取到的符号是数字类型，该变量的值就是读取到的数字的值
            } else if(streamTokenizer.ttype == StreamTokenizer.TT_EOL) {
                System.out.println();
            }
        }
    }
}
