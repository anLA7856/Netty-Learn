package com.anla.netty.protocol.codec;

import com.anla.netty.protocol.message.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;
import java.util.Map;

/**
 * @user anLA7856
 * @time 19-2-13 下午10:37
 * @description 编码器
 */
public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {

    private MyMarshallingEncoder myMarshallingEncoder;

    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, List<Object> out) throws Exception {
        if (msg == null || msg.getHeader() == null) {
            throw new Exception("the encode message is null");
        }
        ByteBuf sendBuf = Unpooled.buffer();
        sendBuf.writeInt((msg.getHeader().getSrcCode()));
        sendBuf.writeInt((msg.getHeader().getLength()));
        sendBuf.writeLong((msg.getHeader().getSessionId()));
        sendBuf.writeByte((msg.getHeader().getType()));
        sendBuf.writeByte(msg.getHeader().getPriority());
        sendBuf.writeInt(msg.getHeader().getAttachment().size());   // 只发送map大小

        String key = null;
        byte[] keyArray = null;
        Object value = null;
        for (Map.Entry<String, Object> param : msg.getHeader().getAttachment().entrySet()) {
            key = param.getKey();
            keyArray = key.getBytes("UTF-8");
            sendBuf.writeInt(keyArray.length);
            sendBuf.writeBytes(keyArray);
            value = param.getValue();
            myMarshallingEncoder.encode(value, sendBuf);
        }
        key = null;
        keyArray = null;
        value = null;
        if (msg.getBody() != null) {
            myMarshallingEncoder.encode(msg.getBody(), sendBuf);
        }else {
            sendBuf.writeInt(0);
            sendBuf.setInt(4, sendBuf.readableBytes());
        }
    }
}
