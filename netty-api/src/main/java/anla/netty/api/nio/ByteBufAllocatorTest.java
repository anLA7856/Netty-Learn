package anla.netty.api.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * 内存池学习：https://blog.csdn.net/youaremoon/article/details/47910971
 * @author luoan
 * @version 1.0
 * @date 2020/11/26 22:29
 **/
public class ByteBufAllocatorTest {

    public static void main(String[] args) {
        System.out.println("测试buf回收====");
        ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;
        // tiny
        ByteBuf buf1 = allocator.directBuffer(1); // 分配的内存最大长度为496
        buf1.release();
        ByteBuf buf2 = allocator.directBuffer(16777); // 分配的内存最大长度为496
        buf2.writeBytes(new byte[]{1});
        buf2.writeBytes(new byte[]{2});
        System.out.printf("buf1: 0x%X%n", buf2.memoryAddress());
        buf2.release(); // 此时会被回收到tiny 的512b格子中
    }
}
