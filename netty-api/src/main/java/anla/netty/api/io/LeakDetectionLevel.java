package anla.netty.api.io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/9/11 13:44
 **/
public class LeakDetectionLevel {
    public static void main(String[] args) {
        for (int i = 0; i < 500000; ++i) {
            ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.buffer(1024);
            byteBuf.release();   // 不会报错
            byteBuf = null;   // gc线程会报错。
        }
        System.gc();
    }
}
