package anla.netty.chatroom.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import anla.netty.chatroom.protocol.Packet;
import anla.netty.chatroom.protocol.PacketCodeC;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/9/11 13:44
 **/
public class PacketEncoder extends MessageToByteEncoder<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
        PacketCodeC.INSTANCE.encode(out, packet);
    }
}
