package com.anla.netty.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @user anLA7856
 * @time 19-1-11 下午11:09
 * @description 用于接受连接的通知,连接完成，操作
 */
public class AcceptCompletionHanlder implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {

    /**
     *
     * @param result
     * @param attachment 异步Channel携带的附件
     */
    @Override
    public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
        // 客户端已经介入成功，一个AsynchronousServerSocketChannel可以接受成千上万个客户端，所以
        //需要继续调用它的accept方法，接受其他客户端连接，最终形成一个循环
        attachment.asynchronousServerSocketChannel.accept(attachment, this);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        result.read(byteBuffer, byteBuffer, new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
        attachment.latch.countDown();   // 做完了，让其继续走
    }
}
