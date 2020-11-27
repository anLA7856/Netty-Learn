package anla.netty.api.io;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * Java IO中的管道为运行在同一个JVM中的两个线程提供了通信的能力。所以管道也可以作为数据源以及目标媒介。
 *
 * 你不能利用管道与不同的JVM中的线程通信(不同的进程)。在概念上，Java的管道不同于Unix/Linux系统中的管道。
 * 在Unix/Linux中，运行在不同地址空间的两个进程可以通过管道通信。
 * 在Java中，通信的双方应该是运行在同一进程中的不同线程。
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
