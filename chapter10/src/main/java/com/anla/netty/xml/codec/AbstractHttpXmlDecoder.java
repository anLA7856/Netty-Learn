package com.anla.netty.xml.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import java.io.StringReader;
import java.nio.charset.Charset;

/**
 * @user anLA7856
 * @time 19-1-27 下午4:20
 * @description
 */
public abstract class AbstractHttpXmlDecoder<T> extends MessageToMessageDecoder<T> {
    private IBindingFactory factory;
    private StringReader reader;
    private Class<?> clazz;
    private boolean isPrint;
    private final static String CHARSET_NAME = "UTF-8";
    private final static Charset UTF_8 = Charset.forName(CHARSET_NAME);

    protected AbstractHttpXmlDecoder(Class<?> clazz){
        this.clazz = clazz;
    }

    protected AbstractHttpXmlDecoder(Class<?> clazz, boolean isPrint){
        this.clazz = clazz;
        this.isPrint = isPrint;
    }
    protected Object decode0(ChannelHandlerContext channelHandlerContext, ByteBuf body) throws JiBXException {
        factory = BindingDirectory.getFactory(clazz);
        String content = body.toString(UTF_8);
        if (isPrint) {
            System.out.println("The body is : " + content);
        }
        reader = new StringReader(content);
        IUnmarshallingContext unmarshallingContext = factory.createUnmarshallingContext();
        Object result = unmarshallingContext.unmarshalDocument(reader);
        reader.close();
        reader = null;
        return result;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (reader != null) {
            reader.close();
            reader = null;
        }
    }
}
