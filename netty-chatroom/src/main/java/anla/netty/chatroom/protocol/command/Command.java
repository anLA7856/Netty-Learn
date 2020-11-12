package anla.netty.chatroom.protocol.command;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/9/11 13:44
 **/
public interface Command {

    Byte LOGIN_REQUEST = 1;

    Byte LOGIN_RESPONSE = 2;

    Byte MESSAGE_REQUEST = 3;

    Byte MESSAGE_RESPONSE = 4;
}
