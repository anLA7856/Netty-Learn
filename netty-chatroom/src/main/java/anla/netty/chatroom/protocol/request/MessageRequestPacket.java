package anla.netty.chatroom.protocol.request;

import static anla.netty.chatroom.protocol.command.Command.MESSAGE_REQUEST;

import lombok.Data;
import lombok.NoArgsConstructor;
import anla.netty.chatroom.protocol.Packet;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/9/11 13:44
 **/
@Data
@NoArgsConstructor
public class MessageRequestPacket extends Packet {

    private String message;

    public MessageRequestPacket(String message) {
        this.message = message;
    }

    @Override
    public Byte getCommand() {
        return MESSAGE_REQUEST;
    }
}
