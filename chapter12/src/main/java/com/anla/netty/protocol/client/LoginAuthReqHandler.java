package com.anla.netty.protocol.client;

import com.anla.netty.protocol.message.Header;
import com.anla.netty.protocol.message.MessageType;
import com.anla.netty.protocol.message.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @user anLA7856
 * @time 19-2-13 下午11:39
 * @description 用于握手认证的客户端ChannelHandler，用于在通道激活时候发起握手请求
 */
public class LoginAuthReqHandler extends ChannelHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 到这里，说明TCP三次握手成功，所以此时需要客户端构造业务握手消息给服务端，用于向
        // 服务端发起业务握手消息。即便于服务端采用IP白名单机制
        ctx.writeAndFlush(buildLoginReq());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        // 如果是握手消息，则需要判断是否认证成功
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
            byte loginResult = Byte.parseByte(message.getBody().toString());
            if (loginResult != (byte)0) {
                // 握手失败，关闭连接
                ctx.close();
            }else {
                System.out.println("Login is ok : " + message);
                ctx.fireChannelRead(msg);
            }
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildLoginReq() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ.value());
        message.setHeader(header);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);   // fire 掉
    }
}
