package anla.netty.chatroom.serialize;

import anla.netty.chatroom.serialize.impl.JSONSerializer;


/**
 * @author luoan
 * @version 1.0
 * @date 2020/9/11 13:44
 **/
public interface Serializer {

    Serializer DEFAULT = new JSONSerializer();

    /**
     * 序列化算法
     * @return
     */
    byte getSerializerAlogrithm();

    /**
     * java 对象转换成二进制
     */
    byte[] serialize(Object object);

    /**
     * 二进制转换成 java 对象
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);

}
