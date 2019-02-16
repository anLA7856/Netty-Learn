package com.anla.netty.protocol.codec;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteInput;

import java.io.IOException;

/**
 * @user anLA7856
 * @time 19-2-13 下午11:22
 * @description
 */
public class ChannelBufferByteInput implements ByteInput {
    private final ByteBuf buffer;

    public ChannelBufferByteInput(ByteBuf buffer) {
        this.buffer = buffer;
    }

    public void close() throws IOException {
    }

    public int available() throws IOException {
        return buffer.readableBytes();
    }

    public int read() throws IOException {
        if (buffer.isReadable()) {
            return buffer.readByte() & 0xff;
        }
        return -1;
    }

    public int read(byte[] array) throws IOException {
        return read(array, 0, array.length);
    }

    public int read(byte[] dst, int dstIndex, int length) throws IOException {
        int available = available();
        if (available == 0) {
            return -1;
        }

        length = Math.min(available, length);
        buffer.readBytes(dst, dstIndex, length);
        return length;
    }

    public long skip(long bytes) throws IOException {
        int readable = buffer.readableBytes();
        if (readable < bytes) {
            bytes = readable;
        }
        buffer.readerIndex((int) (buffer.readerIndex() + bytes));
        return bytes;
    }
}
