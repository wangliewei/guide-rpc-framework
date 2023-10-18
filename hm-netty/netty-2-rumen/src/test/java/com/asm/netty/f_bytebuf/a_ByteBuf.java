package com.asm.netty.f_bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * 测试看出：
 *  ByteBuf 初始长度 256； 【--- 可变字符序列 ，长度不够*2 ---】
 */
@Slf4j
public class a_ByteBuf {

    /**
     *
     PooledUnsafeDirectByteBuf(ridx: 0, widx: 0, cap: 256)
     PooledUnsafeDirectByteBuf(ridx: 0, widx: 300, cap: 512)

     read index:0 write index:0 capacity:256

     read index:0 write index:300 capacity:512
              +-------------------------------------------------+
              |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
     +--------+-------------------------------------------------+----------------+
     |00000000| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
     |00000010| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
     |00000020| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
     |00000030| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
     |00000040| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
     |00000050| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
     |00000060| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
     |00000070| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
     |00000080| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
     |00000090| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
     |000000a0| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
     |000000b0| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
     |000000c0| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
     |000000d0| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
     |000000e0| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
     |000000f0| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
     |00000100| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
     |00000110| 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 61 |aaaaaaaaaaaaaaaa|
     |00000120| 61 61 61 61 61 61 61 61 61 61 61 61             |aaaaaaaaaaaa    |
     +--------+-------------------------------------------------+----------------+
     */
    public static void main(String[] args) {



        // 池化基于直接内存 的
        final ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();// ByteBuf 初始长度 256 【获取直接内存，获取堆内存 heapBuffer】
        System.out.println(buf.getClass());// class io.netty.buffer.PooledUnsafeDirectByteBuf

        System.out.println("1111111111111111111111111111111111111111111111111111111111");
        log(buf);
        System.out.println("2222222222222222222222222222222222222222222222222222222222");

        final StringBuilder sb = new StringBuilder();// StringBuilder 初始长度16 超出一直*2

        for (int i = 0; i < 300; i++) {
            sb.append("a");
        }

        buf.writeBytes(sb.toString().getBytes());// 300长度字符串 写入 ByteByf  查看长度
        log(buf);
        /*
        read index:0 write index:0 capacity:256

        read index:0 write index:300 capacity:512
        ...
        ...
        ...
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
