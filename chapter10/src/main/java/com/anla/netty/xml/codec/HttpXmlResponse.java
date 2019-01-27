package com.anla.netty.xml.codec;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @user anLA7856
 * @time 19-1-27 下午4:20
 * @description
 */
public class HttpXmlResponse {
    private FullHttpResponse httpResponse;
    private Object result;

    @Override
    public String toString() {
        return "HttpXmlResponse{" +
                "httpResponse=" + httpResponse +
                ", result=" + result +
                '}';
    }

    public HttpXmlResponse() {
    }

    public HttpXmlResponse(FullHttpResponse httpResponse, Object result) {
        this.httpResponse = httpResponse;
        this.result = result;
    }

    public FullHttpResponse getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(FullHttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
