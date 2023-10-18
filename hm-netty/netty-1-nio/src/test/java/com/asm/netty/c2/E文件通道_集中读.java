package com.asm.netty.c2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import static com.asm.netty.utils.ByteBufferUtil.debugAll;


/**
 * 内容：
 * 1. new 一个文件通道
 * 2. allocate 分配三个内存
 * 3. read([]) 文件中分散读取 到 三个内存
 * 4. flip  三个内存
 *          调试打印
 *
 */
public class E文件通道_集中读 {

    /**
     * 分散读取 : 重点是 channel.read([])
     * @param args
     */
    public static void main(String[] args) throws IOException {

        FileChannel channel = new RandomAccessFile("./hm-netty/netty-1-nio/words.txt", "rw").getChannel();

        ByteBuffer bu1 = ByteBuffer.allocate(3);
        ByteBuffer bu2 = ByteBuffer.allocate(3);
        ByteBuffer bu3 = ByteBuffer.allocate(5);

        channel.read(new ByteBuffer[]{bu1,bu2,bu3});
/*        channel.read(bu1);
        channel.read(bu2);
        channel.read(bu3);*/

        bu1.flip();
        bu2.flip();
        bu3.flip();

        debugAll(bu1);
        debugAll(bu2);
        debugAll(bu3);
    }



}
