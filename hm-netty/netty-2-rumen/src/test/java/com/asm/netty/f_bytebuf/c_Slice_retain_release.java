package com.asm.netty.f_bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.junit.Test;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 #### slice
 【零拷贝】的体现之一，
 对原始 ByteBuf 进行切片成多个 ByteBuf，
 切片后的 ByteBuf 并没有发生内存复制，还是使用原始 ByteBuf 的内存，
 切片后的 ByteBuf 维护独立的 read，write 指针

 注意点：1. 切片的 ByteBuf 将已限制最大容量，不得追加
        2. ByteBuf 和 slice 分别维护自己独立的 read，write 指针
        3. 对 ByteBuf release【引用计数-1】操作之后，不得使用 slice切片的数据，除非 先使用 retain【引用计数+1】
        4. 正确用法：对单个切片进行 retain 和 release 成对处理，不会乱
 5. 【--- 无论 对 原buf 还是 slice 得到的 ByteBuf引用计数 都是一样的，都是对物理内存进行引用计数 ---】
 */
public class c_Slice_retain_release {

    public static void main(String[] args) {

        final ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','i','j'});

        log(buf);
        /*
        read index:0 write index:10 capacity:10
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 63 64 65 66 67 68 69 6a                   |abcdefghij      |
+--------+-------------------------------------------------+----------------+
         */

        // 切片，没有发生数据复制
        final ByteBuf f1 = buf.slice(0,5);
        log(f1);
        /*
        read index:0 write index:5 capacity:5
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 63 64 65                                  |abcde           |
+--------+-------------------------------------------------+----------------+
         */

        System.out.println("*********************************** 修改 首个字节 将看到，原始ByteBuf值也被修改了 *********************************** ");
        f1.setByte(0,'z'); // 或 buf.setByte(0,'z');  效果一样，因为是同一个物理内存
        log(f1);
        log(buf);
        /*
        read index:0 write index:5 capacity:5
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7a 62 63 64 65                                  |zbcde           |
+--------+-------------------------------------------------+----------------+
read index:0 write index:10 capacity:10
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7a 62 63 64 65 66 67 68 69 6a                   |zbcdefghij      |
+--------+-------------------------------------------------+----------------+
         */

    }
    // 1. 切片的 ByteBuf 将已限制最大容量，不得追加
    @Test
    public void test0()
    {
        final ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','i','j'});

        final ByteBuf f1 = buf.slice(0, 5);

        f1.writeByte('k');
        log(buf);
        /*
        报异常：
        java.lang.IndexOutOfBoundsException:
            writerIndex(5) + minWritableBytes(1) exceeds maxCapacity(5):
                UnpooledSlicedByteBuf(ridx: 0, widx: 5, cap: 5/5, unwrapped: PooledUnsafeDirectByteBuf(ridx: 0, widx: 10, cap: 10))
         */
    }

    // 2. ByteBuf 和 slice 分别维护自己独立的 read，write 指针
    @Test
    public void readWrite_index()
    {
        final ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','i','j'});
        log(buf);
/*
        read index:0 write index:10 capacity:10
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 63 64 65 66 67 68 69 6a                   |abcdefghij      |
+--------+-------------------------------------------------+----------------+
 */
        // slice 一个
        final ByteBuf f1 = buf.slice(0, 5);
        log(f1);
/*
read index:0 write index:5 capacity:5
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 63 64 65                                  |abcde           |
+--------+-------------------------------------------------+----------------+
 */
        // **************** 进行 读数据 *******************
        System.out.println("*********************************** 进行 读数据 *********************************** ");
        System.out.println(buf.readByte());// 97  【ASCII中'a' 的十六进制61，十进制97 】
        System.out.println(buf.readByte());// 98
        System.out.println(buf.readByte());// 99
/*
*********************************** 进行 读数据 ***********************************
97
98
99
 */
        // ByteBuf 的指针改变了， slice指针没变
        log(buf);
        log(f1);
 /*

read index:3 write index:10 capacity:10
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 64 65 66 67 68 69 6a                            |defghij         |
+--------+-------------------------------------------------+----------------+
read index:0 write index:5 capacity:5
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 61 62 63 64 65                                  |abcde           |
+--------+-------------------------------------------------+----------------+
         */
    }


    // 3. 对 ByteBuf release【引用计数-1】操作之后，不得使用 slice切片的数据，除非 先使用 retain【引用计数+1】
    @Test
    public void release()
    {
        final ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','i','j'});
        log(buf);

        final ByteBuf f1 = buf.slice(0, 5);
        buf.release();// 释放内存
        log(f1);// 异常：io.netty.util.IllegalReferenceCountException: refCnt: 0
    }

    // 4. 对单个切片进行 retain【引用计数+1】 和 release【引用计数-1】 成对处理，不会乱
    @Test
    public void reatin_and_release()
    {
        final ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','i','j'});
        log(buf);

//        final ByteBuf f1 = buf.slice(0, 5);
//        f1.retain(); // 对切片=>先引用计数+1
//        log(f1);
//
//        final ByteBuf f2 = buf.slice(0, 5);
//        f2.retain(); // 对切片=>先引用计数+1
//        log(f2);
//
//        System.out.println("****************************** 释放原有 ByteBuf内存 【切片不受影响】*********************************** ");
//        buf.release();
//        log(f1);
//
//        f1.release(); // 再 引用计数-1
//        f2.release(); // 再 引用计数-1
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
