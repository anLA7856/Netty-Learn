package com.anla.netty.protocol.codec;

import com.anla.netty.protocol.message.Header;
import com.anla.netty.protocol.message.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @user anLA7856
 * @time 19-2-13 下午11:14
 * @description
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {
    private MyMarshallingDecoder myMarshallingDecoder;


    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) throws IOException {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        myMarshallingDecoder = new MyMarshallingDecoder();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null){
            return null;
        }
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setSrcCode(in.readInt());
        header.setLength(in.readInt());
        header.setSessionId(in.readLong());
        header.setType(in.readByte());
        header.setPriority(in.readByte());
        int size = in.readInt();
        if (size > 0) {    // 解码附件
            Map<String, Object> attchment = new HashMap<String, Object>(size);
            int keySize = 0;
            byte[] keyArray = null;
            String key = null;
            for (int i = 0;i < size; i++) {
                keySize = in.readInt();
                keyArray = new byte[keySize];
                in.readBytes(keyArray);
                key = new String(keyArray, "UTF-8");
                attchment.put(key, myMarshallingDecoder.decode(in));
            }
            keyArray = null;
            key = null;
            header.setAttachment(attchment);
        }
        if (in.readableBytes() > 4){
            message.setBody(myMarshallingDecoder.decode(in));
        }
        message.setHeader(header);
        return message;
    }
}
