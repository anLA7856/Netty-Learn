package com.anla.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @user anLA7856
 * @time 19-1-26 上午9:59
 * @description
 */
public class HttpFileServer {
    private static final String DEFAULT_URL = "/chapter10";

    public void run(final int port, final String url) throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("http-decoder", new HttpRequestDecoder());  // 添加http请求消息解码器
                            // 讲多个消息转化为单个FullHttpRequest或者FullHttpResponse，原因是HTTP解码器在每个HTTP消息中会生成多个消息对象
                            socketChannel.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65535));
                            socketChannel.pipeline().addLast("http-encoder", new HttpResponseEncoder());  // 对相应消息进行编码
                            socketChannel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());  // 异步发送大的码流，即拆分发送
                            socketChannel.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(url));  // 具体文件服务器业务逻辑
                        }
                    });
            ChannelFuture future = bootstrap.bind("127.0.0.1", port).sync();
            System.out.println("HTTP 文件目录服务器启动，网址是 ： " + "http://127.0.0.1:8080");
            future.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;

        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        String url = DEFAULT_URL;
        if ((args != null ? args.length : 0) > 1){
            url = args[1];
        }
        new HttpFileServer().run(port, url);
    }
}
