package com.anla.netty.protocol.codec;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteOutput;

import java.io.IOException;

/**
 * @user anLA7856
 * @time 19-2-13 下午11:08
 * @description 对bytebuf的封装操作
 */
public class ChannelBufferByteOutput implements ByteOutput {

    private final ByteBuf buffer;

    public ChannelBufferByteOutput(ByteBuf buffer) {
        this.buffer = buffer;
    }
    public void write(int i) throws IOException {
        buffer.writeByte(i);
    }

    public void write(byte[] bytes) throws IOException {
        buffer.writeBytes(bytes);
    }

    public void write(byte[] bytes, int srcIndex, int length) throws IOException {
        buffer.writeBytes(bytes, srcIndex, length);
    }

    public void close() throws IOException {

    }

    public void flush() throws IOException {

    }

    public ByteBuf getBuffer() {
        return buffer;
    }
}
