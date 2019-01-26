package com.anla.netty.marshalling;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

/**
 * @user anLA7856
 * @time 19-1-25 下午11:20
 * @description
 */
public class MarshallingCodeFactory {
    public static MarshallingDecoder buildMarshallingDecoder(){
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");  // 表示创建Java序列化工厂对象
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
        return new MarshallingDecoder(provider, 1024);   //注意这个表示，单个消息最大长度
    }

    public static MarshallingEncoder buildMarshallingEncoder(){
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");  // 表示创建Java序列化工厂对象
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
        return new MarshallingEncoder(provider);
    }
}
