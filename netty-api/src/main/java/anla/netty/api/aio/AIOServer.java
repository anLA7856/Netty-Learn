package anla.netty.api.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/11/15 20:36
 **/
public class AIOServer {
    private AsynchronousServerSocketChannel server ;
    private Object attachment;

    public AIOServer(){
        try {
            server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress("127.0.0.1", 8088));
            System.out.println("server run at:127.0.0.1:8088");
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.accept(attachment, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            final ByteBuffer buffer = ByteBuffer.allocate(1024);
            Future<Integer> writeResult = null;    //定义一个异步任务,用于检验回写
            @Override
            public void completed(AsynchronousSocketChannel channel, Object attachment) {
                try {
                    //代表成功拿到了一个
                    buffer.clear();
                    channel.read(buffer).get(100, TimeUnit.SECONDS);    //把result里面东西，读到buffer里面。有超时的读取。
                    System.out.println("server read the data from client: " + new String(buffer.array()));
                    buffer.flip();
                    writeResult = channel.write(buffer);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally{
                    server.accept(null, this);     //需要加上这一句，否则只会相应一次？
                    try {
                        System.out.println("server write data's length" + writeResult.get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("server accept failed");
            }
        });
    }
    public static void main(String[] args) {
        new AIOServer();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}