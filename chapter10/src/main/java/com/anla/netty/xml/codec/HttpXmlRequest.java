package com.anla.netty.xml.codec;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @user anLA7856
 * @time 19-1-27 下午3:53
 * @description HTTP+XML 请求消息
 */
public class HttpXmlRequest {
    private FullHttpRequest request;
    private Object body;

    @Override
    public String toString() {
        return "HttpXmlRequest{" +
                "request=" + request +
                ", body=" + body +
                '}';
    }

    public HttpXmlRequest(FullHttpRequest request, Object body) {
        this.request = request;
        this.body = body;
    }

    public HttpXmlRequest() {
    }

    public FullHttpRequest getRequest() {
        return request;
    }

    public void setRequest(FullHttpRequest request) {
        this.request = request;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
