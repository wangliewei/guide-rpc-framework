package com.asm.netty.c3_filespaths;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 *  两个 channel 之间 传输数据
 */
public class TestTransferTo {


    public static void main(String[] args) {

        final long l1 = System.nanoTime();// 获取 纳秒

        try(
                final FileChannel from = new FileInputStream("from.txt").getChannel();
                final FileChannel to = new FileOutputStream("to.txt").getChannel()
        ){

            // 剩余 未传输的 字节数
            long size = from.size();


            for (long left = size; left > 0;)
            {
                System.out.println("position:  " + (size - left) + " left:" + left);

                // 比 输入输出流 效率高，底层使用操作系统的 零拷贝 进行优化
                // 上限：最大一次传输 2G 数据，超出不会被传输
                long transSize = from.transferTo(size-left, left, to);// 返回 实际传输 字节数

                left -= transSize;
            }



        }catch (IOException e)
        {
            e.printStackTrace();
        }

        final long l2 = System.nanoTime();// 获取 纳秒


        final long l3 = l2 - l1;
        System.out.println("用时：" + l3 / 1000_000.0);

    }


}
