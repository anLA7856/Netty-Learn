package anla.netty.chatroom.attribute;

import io.netty.util.AttributeKey;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/9/11 13:44
 **/
public interface Attributes {
    AttributeKey<Boolean> LOGIN = AttributeKey.newInstance("login");
}
