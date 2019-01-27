package com.anla.netty.xml.codec;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * @user anLA7856
 * @time 19-1-27 下午4:19
 * @description 请求消息解码类
 */
public class HttpXmlRequestDecoder extends AbstractHttpXmlDecoder<FullHttpRequest>{

    public HttpXmlRequestDecoder(Class<?> clazz) {
        this(clazz, false);
    }

    public HttpXmlRequestDecoder(Class<?> clazz, boolean isPrint){
        super(clazz, isPrint);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest request, List<Object> out) throws Exception {
        if (!request.getDecoderResult().isSuccess()){
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        HttpXmlRequest httpXmlRequest = new HttpXmlRequest(request, decode0(ctx, request.content()));
        out.add(httpXmlRequest);
    }

    private static void  sendError(ChannelHandlerContext ctx, HttpResponseStatus status){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status.toString()+"\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
