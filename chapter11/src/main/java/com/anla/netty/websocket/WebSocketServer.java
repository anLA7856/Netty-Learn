package com.anla.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @user anLA7856
 * @time 19-1-28 下午11:16
 * @description
 */
public class WebSocketServer {
    public void run(int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("http-codec", new HttpServerCodec());  // 将应答消息编码或者解码为HTTP消息
                            ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65535));  // 将http消息的多个部分，组合成一个完整的http消息
                            ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());   // 想客户端发送HTML5文件
                            ch.pipeline().addLast("handler", new WebSocketServerHandler());   // 增加业务处理handler
                        }
                    });
            Channel channel = bootstrap.bind(port).sync().channel();
            System.out.println("Web socket server started at port " + port);
            System.out.println("Open your brower and nacigate to http://localhost:" + port);
            channel.closeFuture().sync();
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
        new WebSocketServer().run(port);
    }
}
