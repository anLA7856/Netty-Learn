package com.anla.netty.xml.server;

import com.anla.netty.xml.codec.HttpXmlRequest;
import com.anla.netty.xml.codec.HttpXmlResponse;
import com.anla.netty.xml.pojo.Address;
import com.anla.netty.xml.pojo.Order;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.ArrayList;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;

/**
 * @user anLA7856
 * @time 19-1-27 下午5:24
 * @description
 */
public class HttpXmlServerHandler extends SimpleChannelInboundHandler<HttpXmlRequest> {
    @Override
    protected void messageReceived(final ChannelHandlerContext ctx, HttpXmlRequest msg) throws Exception {
        HttpRequest request = msg.getRequest();
        Order order = (Order) msg.getBody();
        System.out.println("Http server receive request : " + order);
        doBusiness(order);
        ChannelFuture future = ctx.writeAndFlush(new HttpXmlResponse(null, order));
        if (!isKeepAlive(request)) {
            future.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    ctx.close();
                }
            });
        }
    }

    private void doBusiness(Order order) {
        order.getCustomer().setFirstName("孙");
        order.getCustomer().setLastName("悟空");
        List<String> midNames = new ArrayList<String>();
        midNames.add("唐僧");
        order.getCustomer().setMiddleNames(midNames);
        Address address = order.getBillTo();
        address.setCity("广州");
        address.setCountry("深圳");
        address.setState("东莞");
        address.setPostCode("123456");
        order.setBillTo(address);
        order.setShipTo(address);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer("失败: " + status.toString()+"\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
        // 构造内部消息异常，发送给客户端，发送完成后，关闭HTTP链路
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


}
