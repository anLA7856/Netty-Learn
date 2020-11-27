package anla.netty.api.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.buffer.UnpooledHeapByteBuf;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/11/20 13:44
 **/
public class UnpooledHeapByteBufTest {
    public static String msg = "hello UnpooledDirectByteBufTest";

    public static void main(String[] args) {
        // emptyByteBufTest();
        // unpooledHeapByteBufTest();
        PooledByteBufAllocator allocator = new PooledByteBufAllocator();
        ByteBuf buffer = allocator.directBuffer();
        writeAndPrint(buffer);
    }

    private static void writeAndPrint(ByteBuf buffer) {
        buffer.writeBytes(msg.getBytes());
        System.out.println("长度: "+buffer.readableBytes());
        while (buffer.readableBytes() != 0){
            System.out.println("读到: "+buffer.readByte());
        }
    }

    private static void unpooledHeapByteBufTest() {
        ByteBuf empty = new UnpooledHeapByteBuf(PooledByteBufAllocator.DEFAULT, 100, 10000);
        // 由于是emptyByteBuf，写入数据会报错
        writeAndPrint(empty);
    }

    /**
     * 已栋
     */
    private static void emptyByteBufTest() {
        ByteBuf empty = new EmptyByteBuf(UnpooledByteBufAllocator.DEFAULT);
        // 由于是emptyByteBuf，写入数据会报错
        writeAndPrint(empty);
    }
}
