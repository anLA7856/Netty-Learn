package anla.netty.api.nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import static anla.netty.api.io.FileReaderTest.FILE_NAME;

/**
 * @author luoan
 * @version 1.0
 * @date 2020/11/14 11:58
 **/
public class MappedByteBufferTest {
    // static int length = 0x8FFFFFF; // 128 Mb
    static int length = 0xFFFFFFF; //

    public static void main(String[] args) throws Exception {
        MappedByteBufferTest t = new MappedByteBufferTest();
        t.testByteBufferRead();
        t.testMappedByteBufferRead();
    }

    public void testByteBufferRead() {
        try {
            int length = 0XFFFFFFF;
            MappedByteBuffer out = null;
            try {
                out = new RandomAccessFile(FILE_NAME, "rw").getChannel()
                        .map(FileChannel.MapMode.READ_WRITE, 0, length);

                for (int i = 0; i < length; i++)
                    out.put((byte) 'x');
                System.out.println("Finished writing");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Obtain a channel
            ReadableByteChannel channel = new FileInputStream(FILE_NAME)
                    .getChannel();

            // Create a direct ByteBuffer; see also e158 Creating a ByteBuffer
            ByteBuffer buf = ByteBuffer.allocateDirect(10);

            int numRead = 0;
            while (numRead >= 0) {
                // read() places read bytes at the buffer's position so the
                // position should always be properly set before calling read()
                // This method sets the position to 0
                buf.rewind();

                // Read bytes from the channel
                numRead = channel.read(buf);

                // The read() method also moves the position so in order to
                // read the new bytes, the buffer's position must be set back to
                // 0
                buf.rewind();

                // Read bytes from ByteBuffer; see also
                // e159 Getting Bytes from a ByteBuffer
                for (int i = 0; i < 10; i++) {
                    byte b = buf.get(i);
                    System.out.println((char) b);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void testMappedByteBufferRead() {
        int length = 0XFFFFFFF;
        MappedByteBuffer out = null;
        try {
            out = new RandomAccessFile(FILE_NAME, "rw").getChannel().map(
                    FileChannel.MapMode.READ_WRITE, 0, length);

            for (int i = 0; i < length; i++)
                out.put((byte) 'x');
            System.out.println("Finished writing");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int i = length / 2; i < length / 2 + 6; i++)
            System.out.print((char) out.get(i)); // read file
    }
}