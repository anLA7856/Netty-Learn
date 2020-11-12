package anla.netty.chatroom.protocol.response;

import static anla.netty.chatroom.protocol.command.Command.MESSAGE_RESPONSE;

import lombok.Data;
import anla.netty.chatroom.protocol.Packet;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/9/11 13:44
 **/
@Data
public class MessageResponsePacket extends Packet {

    private String message;

    @Override
    public Byte getCommand() {

        return MESSAGE_RESPONSE;
    }
}
