package anla.netty.api.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/11/15 20:37
 **/
public class AIOClient {
    private AsynchronousSocketChannel channel;
    private InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 8088);
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);
    public AIOClient() {
        try {
            channel = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private class ConnectCompletionHandler implements CompletionHandler<Void, Object> {
        AsynchronousSocketChannel channel;
        public ConnectCompletionHandler(AsynchronousSocketChannel channel) {
            this.channel = channel;
        }
        @Override
        public void completed(Void result, Object attachment) {
            System.out.println("connect server successfully");
            ByteBuffer bf = ByteBuffer.wrap("this is from client".getBytes());
            channel.write(bf, bf, new WriteCompletionHandler(channel));
            channel.read(buffer, null, new ReadCompletionHandler(channel));
        }
        @Override
        public void failed(Throwable exc, Object attachment) {
            System.out.println("connect server failed");
        }
    }

    private class WriteCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
        AsynchronousSocketChannel channel;
        public WriteCompletionHandler(AsynchronousSocketChannel channel) {
            this.channel = channel;
        }
        @Override
        public void completed(Integer result, ByteBuffer attachment) {
            if (attachment.hasRemaining()) { // 有剩余，就一致写。
                channel.write(attachment, attachment, this);
            }
            System.out.println("write successfully");
        }
        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            System.out.println("write failed");
        }
    }

    private class ReadCompletionHandler implements CompletionHandler<Integer, Object> {
        AsynchronousSocketChannel channel;
        public ReadCompletionHandler(AsynchronousSocketChannel channel) {
            this.channel = channel;
        }
        @Override
        public void completed(Integer result, Object attachment) {
            buffer.flip();
            System.out.println("read successfully,and data is :" + buffer.array());
        }
        @Override
        public void failed(Throwable exc, Object attachment) {
            System.out.println("read failed");
        }
    }
    private void bind() {
        channel.connect(serverAddress, null, new ConnectCompletionHandler(this.channel));
    }
    public static void main(String[] args) {
        new AIOClient().bind();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}