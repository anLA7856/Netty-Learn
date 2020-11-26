package anla.netty.api.nio.sample;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/11/15 21:09
 **/
public class Client {
    private Integer port;
    private String address;
    public Client(Integer port, String address){
        this.port = port;
        this.address = address;
    }
    /**
     * 用于连接服务器
     */
    public void start() throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new HelloClientHandler());
                    }
                });
        System.out.println("begin to connect...");
        ChannelFuture future = b.connect(this.address, this.port);
        future.channel().closeFuture().sync();
    }
    public static void main(String[] args) throws Exception {
        Client client = new Client(8989, "127.0.0.1");
        client.start();
    }
}

