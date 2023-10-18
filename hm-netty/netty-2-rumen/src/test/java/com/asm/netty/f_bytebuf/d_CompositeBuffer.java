package com.asm.netty.f_bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/*
 CompositeByteBuf
1.    【零拷贝】的体现之一，可以将多个 ByteBuf 合并为一个逻辑上的 ByteBuf，避免拷贝
2.    与slice相反
缺点：更复杂的维护  还要注意 retain release
 */
@Slf4j
public class d_CompositeBuffer {

    // 1. writeBytes 链式赋值拷贝   readableBytes 返回可读取的字节数
    @Test
    public void writeBytes_copy()
    {
        final ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
        buf1.writeBytes(new byte[]{1,2,3,4,5});

        final ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();
        buf2.writeBytes(new byte[]{6,7,8,9,10});

        System.out.println(buf1.getClass()); // : class io.netty.buffer.PooledUnsafeDirectByteBuf  池化直接内存

        // readableBytes 返回可读取的字节数
        final ByteBuf bufferWriteCopy = ByteBufAllocator.DEFAULT.buffer(buf1.readableBytes()+buf2.readableBytes());
        bufferWriteCopy.writeBytes(buf1).writeBytes(buf2);
        log(bufferWriteCopy);
        /*
read index:0 write index:10 capacity:10
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 05 06 07 08 09 0a                   |..........      |
+--------+-------------------------------------------------+----------------+
         */
    }

    // 2. copy方法 重新开辟内存
    @Test
    public void copy_copy()
    {
        final ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
        buf1.writeBytes(new byte[]{1,2,3,4,5});

        // copy buf1
        final ByteBuf bufferCopy = buf1.copy();

        // 修改 buf1
        buf1.setByte(0,9);

        log(bufferCopy);  // 不会因为 buf1 的修改 而改变
        /*
        read index:0 write index:5 capacity:5
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 05                                  |.....           |
+--------+-------------------------------------------------+----------------+
         */
        log(buf1);
        /*
        read index:0 write index:5 capacity:256
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 09 02 03 04 05                                  |.....           |
+--------+-------------------------------------------------+----------------+
         */
    }

    // 3. composite 【零拷贝-false】
    @Test
    public void composite_copy1()
    {
        final ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
        buf1.writeBytes(new byte[]{1,2,3,4,5});

        final ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();
        buf2.writeBytes(new byte[]{6,7,8,9,10});

        final CompositeByteBuf compositeByteBuf1 = ByteBufAllocator.DEFAULT.compositeBuffer();
        compositeByteBuf1.addComponents(buf1, buf2);

        log.debug(String.valueOf(compositeByteBuf1.getClass()));
        log(compositeByteBuf1);
        /*
11:17:19 [DEBUG] [main] c.a.n.f.d_CompositeBuffer - class io.netty.buffer.CompositeByteBuf
read index:0 write index:0 capacity:10
         */
    }

    // 4. composite 【零拷贝-true】  布尔参数 true : 表示自动增长写指针
    @Test
    public void composite_copy2()
    {
        final ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
        buf1.writeBytes(new byte[]{1,2,3,4,5});

        final ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();
        buf2.writeBytes(new byte[]{6,7,8,9,10});

        final CompositeByteBuf compositeByteBuf1 = ByteBufAllocator.DEFAULT.compositeBuffer();
        compositeByteBuf1.addComponents(true,buf1, buf2);
        log(compositeByteBuf1);
        /*
        read index:0 write index:10 capacity:10
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 05 06 07 08 09 0a                   |..........      |
+--------+-------------------------------------------------+----------------+

         */

        System.out.println("************************ 修改了源buf 影响到 compositeBufferd()得到的数据 ************************");
        buf1.setByte(0,15);
        log(compositeByteBuf1);
        /*
        ************************ 修改了源buf 影响到 compositeBufferd()得到的数据 ************************
read index:0 write index:10 capacity:10
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 0f 02 03 04 05 06 07 08 09 0a                   |..........      |
+--------+-------------------------------------------------+----------------+
         */
    }

    private static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE); // Import static constant... netty的
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf.toString());
    }

}
