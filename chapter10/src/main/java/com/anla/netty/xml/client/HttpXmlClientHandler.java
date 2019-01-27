package com.anla.netty.xml.client;

import com.anla.netty.xml.codec.HttpXmlRequest;
import com.anla.netty.xml.codec.HttpXmlResponse;
import com.anla.netty.xml.pojo.OrderFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @user anLA7856
 * @time 19-1-27 下午5:15
 * @description
 */
public class HttpXmlClientHandler extends SimpleChannelInboundHandler<HttpXmlResponse> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        HttpXmlRequest request = new HttpXmlRequest(null, OrderFactory.create(123));
        ctx.writeAndFlush(request);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, HttpXmlResponse msg) throws Exception {
        System.out.println("The client receive response of http header is :" + msg.getHttpResponse().headers().names());
        System.out.println("The client receive response of http body is : " + msg.getResult());
    }
}
