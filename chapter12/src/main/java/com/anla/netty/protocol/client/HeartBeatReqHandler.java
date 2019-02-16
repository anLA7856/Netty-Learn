package com.anla.netty.protocol.client;

import com.anla.netty.protocol.message.Header;
import com.anla.netty.protocol.message.MessageType;
import com.anla.netty.protocol.message.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @user anLA7856
 * @time 19-2-14 下午10:50
 * @description 心跳机制，握手成功之后，由客户端主动发送心跳消息
 */
public class HeartBeatReqHandler extends ChannelHandlerAdapter {
    private volatile ScheduledFuture<?> heartBeat;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        // 握手成功，那么主动发送心跳消息
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
            heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatReqHandler.HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
        }else if (message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_RESP.value()) {
            System.out.println("Client receive server heart beat message : ---> " + message);
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    private class HeartBeatTask implements Runnable{
        private final ChannelHandlerContext ctx;

        public HeartBeatTask(final  ChannelHandlerContext ctx){
            this.ctx = ctx;
        }


        public void run() {
            NettyMessage heartBeat = buildHeartBeat();
            System.out.println("Client send heart beat message to server : ---> " + heartBeat);
            ctx.writeAndFlush(heartBeat);
        }

        private NettyMessage buildHeartBeat() {
            NettyMessage message = new NettyMessage();
            Header header = new Header();
            header.setType(MessageType.HEARTBEAT_REQ.value());
            message.setHeader(header);
            return message;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (heartBeat != null) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
        ctx.fireExceptionCaught(cause);
    }
}
