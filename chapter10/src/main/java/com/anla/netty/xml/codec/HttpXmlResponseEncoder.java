package com.anla.netty.xml.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.util.List;

import static io.netty.handler.codec.http.HttpHeaders.setContentLength;

/**
 * @user anLA7856
 * @time 19-1-27 下午4:42
 * @description
 */
public class HttpXmlResponseEncoder extends AbstractHttpXmlEncoder<HttpXmlResponse>{
    @Override
    protected void encode(ChannelHandlerContext ctx, HttpXmlResponse msg, List<Object> out) throws Exception {
        ByteBuf body = encode0(ctx, msg.getResult());
        FullHttpResponse response = msg.getHttpResponse();
        if (response == null) {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, body);
        }else {
            response = new DefaultFullHttpResponse(msg.getHttpResponse().getProtocolVersion(), msg.getHttpResponse().getStatus(), body);
        }
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/xml");
        setContentLength(response, body.readableBytes());
        out.add(response);
    }
}
