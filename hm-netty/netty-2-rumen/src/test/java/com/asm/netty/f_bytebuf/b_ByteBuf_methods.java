package com.asm.netty.f_bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.junit.Test;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 ByteBuf.markReaderIndex    做一个标记
 ByteBuf.resetReaderIndex   重置到上面标记 【可重复读】
 */
public class b_ByteBuf_methods {

    @Test
    public void testWriteBytes()
    {
        final ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        buf.writeBytes(new byte[]{1,2,3,4});  // 写入字节数组
        log(buf);
        /*
        read index:0 write index:4 capacity:256
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04                                     |....            |
+--------+-------------------------------------------------+----------------+
         */
        buf.writeInt(5);     //  先写高位   【占用 4个字节】
        log(buf);
        /* 先写高位
        read index:0 write index:8 capacity:256
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 00 00 00 05                         |........        |
+--------+-------------------------------------------------+----------------+
         */
        buf.writeIntLE(6);  //  先写低位    【占用 4个字节】
        log(buf);
        /* 先写低位
        read index:0 write index:12 capacity:256
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 00 00 00 05 06 00 00 00             |............    |
+--------+-------------------------------------------------+----------------+
         */


      /*################################################################
        #################            读取数据           #################
        ################################################################*/
        System.out.println(buf.readByte()); // 1
        System.out.println(buf.readByte()); // 2
        System.out.println(buf.readByte()); // 3
        System.out.println(buf.readByte()); // 4

        log(buf);  // 可以看出 读指针往前偏移了，之前读过的数据都废弃了
        /*
        read index:4 write index:12 capacity:256
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 00 00 00 05 06 00 00 00                         |........        |
+--------+-------------------------------------------------+----------------+
         */
       /*################################################################
        #################         做标记，读整数         #################
        ################################################################*/
        buf.markReaderIndex();            // 打一个标记
        System.out.println("做一个标记后 读取整数：" + buf.readInt());// 读取一个整数 【四位】
        log(buf);
        /*
        做一个标记后 读取整数：5
read index:8 write index:12 capacity:256
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 06 00 00 00                                     |....            |
+--------+-------------------------------------------------+----------------+
         */
        buf.resetReaderIndex();          // 重置到上一个 打标记的位置
        System.out.println("重置到上一个标记后 读取整数：" + buf.readInt());// 读取一个整数 【四位】
        log(buf);
        /*
        重置到上一个标记后 读取整数：5
read index:8 write index:12 capacity:256
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 06 00 00 00                                     |....            |
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
