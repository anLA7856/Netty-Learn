package com.anla.netty.protocol.message;

/**
 * @user anLA7856
 * @time 19-1-30 下午11:09
 * @description
 */
public final class NettyMessage {
    private Header header;
    private Object body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
