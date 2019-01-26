package com.anla.netty.marshalling;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @user anLA7856
 * @time 19-1-25 下午11:43
 * @description
 */
public class SubReqClientHandler extends ChannelHandlerAdapter {
    public SubReqClientHandler(){}

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for(int i = 0; i < 10; i++){
            ctx.write(subReq(i));
        }
        ctx.flush();
    }

    private SubscribeReq subReq(int i) {
        SubscribeReq subscribeReq = new SubscribeReq();
        subscribeReq.setSubReqID(i);
        subscribeReq.setUserName("anLA7856");
        subscribeReq.setProductName("Netty Book For Protobuf");
        subscribeReq.setPhoneNumber("123456789");
        subscribeReq.setAddress("Guangzhou Hongzhuanchang");
        return subscribeReq;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Receive server response : [" + msg + "]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
