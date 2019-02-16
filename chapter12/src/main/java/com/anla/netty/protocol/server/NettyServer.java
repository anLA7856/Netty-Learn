package com.anla.netty.protocol.server;

import com.anla.netty.protocol.codec.NettyMessageDecoder;
import com.anla.netty.protocol.codec.NettyMessageEncoder;
import com.anla.netty.protocol.message.NettyConstant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @user anLA7856
 * @time 19-2-14 下午11:29
 * @description
 */
public class NettyServer {
    public void bind() throws Exception{
        // 配置服务端线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyMessageDecoder(1024*1024, 4,4));
                        ch.pipeline().addLast(new NettyMessageEncoder());
                        ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
                        ch.pipeline().addLast(new LoginAuthRespHandler());
                        ch.pipeline().addLast("HeartBeatHandler", new HeartBeatRespHandler());
                    }
                });
        // 绑定端口，同步等待成功
        bootstrap.bind(NettyConstant.REMOTEIP, NettyConstant.PORT).sync();
        System.out.println("Netty Server start ok : " + NettyConstant.REMOTEIP + " : " + NettyConstant.PORT);
    }

    public static void main(String[] args) throws Exception{
        new NettyServer().bind();
    }
}
