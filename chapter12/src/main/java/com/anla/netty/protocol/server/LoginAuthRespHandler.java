package com.anla.netty.protocol.server;

import com.anla.netty.protocol.message.Header;
import com.anla.netty.protocol.message.MessageType;
import com.anla.netty.protocol.message.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @user anLA7856
 * @time 19-2-14 下午10:35
 * @description 握手handler
 */
public class LoginAuthRespHandler extends ChannelHandlerAdapter {
    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<String, Boolean>();

    private String[] whiteList = {"127.0.0.1", "192.168.1.105"};

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        // 如果是握手请求消息，处理，其他消息，透传
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_REQ.value()) {
            String nodeIndex = ctx.channel().remoteAddress().toString();
            NettyMessage loginResp = null;
            // 重复登录，拒绝
            if (nodeCheck.containsKey(nodeIndex)) {
                loginResp = buildResponse((byte)-1);
            }else {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean isOK = false;
                for (String wIp: whiteList) {
                    if (wIp.equals(ip)) {
                        isOK = true;
                        break;
                    }
                }
                loginResp = isOK ? buildResponse((byte)0) : buildResponse((byte) -1);
                if (isOK) {
                    nodeCheck.put(nodeIndex, true);
                }
            }
            System.out.println("The login response is : " + loginResp + "body [" + loginResp.getBody() + "]");
            ctx.writeAndFlush(loginResp);
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildResponse(byte result) {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RESP.value());
        message.setHeader(header);
        message.setBody(result);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        nodeCheck.remove(ctx.channel().remoteAddress().toString());   // 删除缓存
        ctx.close();
        ctx.fireExceptionCaught(cause);    // 下一步处理
    }
}
