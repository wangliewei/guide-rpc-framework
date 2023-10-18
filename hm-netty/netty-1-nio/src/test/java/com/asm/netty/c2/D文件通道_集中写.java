package com.asm.netty.c2;

import org.junit.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import static com.asm.netty.utils.ByteBufferUtil.debugAll;




/**
 * 集中写
 * 内容：
 * 1.   获取三个 buffer
 * 2.   getChannel 获取通道
 * 3.   write([]) 集中写
 *
 */
public class D文件通道_集中写 {


    public static void main(String[] args) {

        ByteBuffer b1 = StandardCharsets.UTF_8.encode("Hello");
        ByteBuffer b2 = StandardCharsets.UTF_8.encode("word");
        ByteBuffer b3 = StandardCharsets.UTF_8.encode("三口");

//debugAll(b2);
        try {
            FileChannel ch = new RandomAccessFile("./hm-netty/netty-1-nio/writes.txt", "rw").getChannel();

            ch.write(new ByteBuffer[]{b1, b2, b3});



        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 一个汉字 占用 三个字节
    @Test
    public void test1()
    {
        ByteBuffer b1 = ByteBuffer.allocate(4);
        ByteBuffer b2 = ByteBuffer.allocate(3);
        ByteBuffer b3 = ByteBuffer.allocate(6);

        b1.put(new byte[]{'g','u','a','n'});
        b2.put(new byte[]{'n','a','n'});
        b3.put("三口".getBytes());

        b1.flip(); debugAll(b1);
        b2.flip(); debugAll(b2);
        b3.flip(); debugAll(b3);

        try {
            FileChannel ch = new RandomAccessFile("writes.txt", "rw").getChannel();

            ch.write(new ByteBuffer[]{b1, b2, b3});


        } catch (IOException e) {
            e.printStackTrace();
        }



    }



}
