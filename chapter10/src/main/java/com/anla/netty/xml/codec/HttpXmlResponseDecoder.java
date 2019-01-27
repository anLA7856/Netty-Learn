package com.anla.netty.xml.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;

import java.util.List;

/**
 * @user anLA7856
 * @time 19-1-27 下午4:47
 * @description
 */
public class HttpXmlResponseDecoder extends AbstractHttpXmlDecoder<DefaultFullHttpResponse> {

    protected HttpXmlResponseDecoder(Class<?> clazz) {
        this(clazz, false);
    }

    public HttpXmlResponseDecoder(Class<?> clazz, boolean isPrint) {
        super(clazz, isPrint);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DefaultFullHttpResponse msg, List<Object> out) throws Exception {
        HttpXmlResponse response = new HttpXmlResponse(msg, decode0(ctx, msg.content()));
        out.add(response);
    }
}
