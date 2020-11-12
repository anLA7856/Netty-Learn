package anla.netty.chatroom.util;

import anla.netty.chatroom.attribute.Attributes;
import io.netty.channel.Channel;
import io.netty.util.Attribute;


/**
 * @author luoan
 * @version 1.0
 * @date 2020/9/11 13:44
 **/
public class LoginUtil {
    public static void markAsLogin(Channel channel) {
        channel.attr(Attributes.LOGIN).set(true);
    }

    public static boolean hasLogin(Channel channel) {
        Attribute<Boolean> loginAttr = channel.attr(Attributes.LOGIN);

        return loginAttr.get() != null;
    }
}
