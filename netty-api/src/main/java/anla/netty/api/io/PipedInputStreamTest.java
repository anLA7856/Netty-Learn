package anla.netty.api.io;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/11/12 23:10
 **/
public class PipedInputStreamTest {


    public static void main(String[] args) throws IOException {
        final PipedOutputStream pos = new PipedOutputStream();
        final PipedInputStream pis = new PipedInputStream(pos);

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    pos.write("hello world?".getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (pos != null) {
                        try {
                            pos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            public void run() {
                try {
                    int data = pis.read();
                    while (data != -1) {
                        System.out.print((char) data);
                        data = pis.read();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (pis != null) {
                        try {
                            pis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(t1);
        executor.execute(t2);
    }
}
