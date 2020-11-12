package anla.netty.chatroom.protocol.request;

import static anla.netty.chatroom.protocol.command.Command.LOGIN_REQUEST;

import lombok.Data;
import anla.netty.chatroom.protocol.Packet;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/9/11 13:44
 **/
@Data
public class LoginRequestPacket extends Packet {
    private String userId;

    private String username;

    private String password;

    @Override
    public Byte getCommand() {

        return LOGIN_REQUEST;
    }
}
