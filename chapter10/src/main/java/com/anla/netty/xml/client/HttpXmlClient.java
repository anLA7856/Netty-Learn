package com.anla.netty.xml.client;

import com.anla.netty.xml.codec.HttpXmlRequestEncoder;
import com.anla.netty.xml.codec.HttpXmlResponseDecoder;
import com.anla.netty.xml.pojo.Order;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;

/**
 * @user anLA7856
 * @time 19-1-27 下午4:54
 * @description
 */
public class HttpXmlClient {
    public void connect(int port, String host) throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("http-decoder", new HttpResponseDecoder());   // 讲二进制iu解码为http应答消息
                            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65535));  //讲1个Http请求消息的多个部分合并成一条完整的HTTP消息
                            ch.pipeline().addLast("xml_decoder", new HttpXmlResponseDecoder(Order.class, true));   // xml消息自动解码
                            ch.pipeline().addLast("http-encoder", new HttpRequestEncoder());
                            ch.pipeline().addLast("xml-encoder", new HttpXmlRequestEncoder());
                            ch.pipeline().addLast("xmlClientHandler", new HttpXmlClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(host, port).sync();  //异步连接，只连接自己
            future.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
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
        new HttpXmlClient().connect( port, "127.0.0.1");
    }
}
