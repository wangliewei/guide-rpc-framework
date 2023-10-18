package com.asm.netty.c2;

import org.junit.Test;

import java.nio.ByteBuffer;
import static com.asm.netty.utils.ByteBufferUtil.debugAll;

public class A内部读写方法ByteBuffer {

    /**
     * 1. allocate 分配内存
     * 2. put 几个字符
     * 3. flip 切换读模式  切换至 读模式 [ position指针指向开头， limit指向写入的最后位置 (内存长度)
     * 4. get 获取两个字节
     *          打印内存
     * 5. rewind 下标 position 设 0 , 从头开始，而不会改limit值 ------------------------------
     *          打印内存
     * 6. mark 一下，给position做标记
     *
     * 7. get() 两次
     *
     * 8. reset 将 postion 重置到 mark 位置
     *
     *
     *
     *
     * @param args
     */
    public static void main(String[] args) {

        ByteBuffer buffer = ByteBuffer.allocate(10);

        buffer.put(new byte[]{'a','b','c','d'});
        debugAll(buffer);

        buffer.flip();// // 切换至 读模式 [ position指针指向开头， limit指向写入的最后位置 (内存长度) ]
        debugAll(buffer);
        System.out.println("KKKKKKKKKKKKKKKKKK get 获取两个字节 KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");
        // get(byte[]) 会改变position的位置
        buffer.get(new byte[2]);

        debugAll(buffer);// ##### 检查内存 #####

        // rewind 下标position设0 ，从头开始 -----------------------------------------
        System.out.println("KKKKKKKKKKKKKKKKKK rewind 下标设为0 KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");
        buffer.rewind();

        debugAll(buffer);// ##### 检查内存 #####

        System.out.println("KKKKKKKKKKKKKKKKKK get =" + (char) buffer.get()  + " KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");

        debugAll(buffer);// ##### 检查内存 #####

        // mark 一下，给position做标记  // : java.nio.HeapByteBuffer[pos=1 lim=4 cap=10]
        System.out.println("KKKKKKKKKKKKKKKKKK  mark  得到的角标 = " + buffer.mark() + " KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");

        // 再次 get() 两次
        System.out.println("KKKKKKKKKKKKKKKKKK  第二个字节转为字符 =" + (char) buffer.get() + " KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");
        System.out.println("KKKKKKKKKKKKKKKKKK  第三个字节转为字符 =" + (char) buffer.get() + " KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");
        /*
        读读读读读度读读  第二个字节转为字符 =b
        读读读读读度读读  第三个字节转为字符 =c
        */

        debugAll(buffer);

        // reset 将 postion 重置到 mark 位置
        buffer.reset();
        System.out.println("KKKKKKKKKKKKKKKKKK reset KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");

        debugAll(buffer);
        System.out.println("KKKKKKKKKKKKKKKKKK  第二个字节转为字符 =" + (char) buffer.get() + " KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");
        System.out.println("KKKKKKKKKKKKKKKKKK  第三个字节转为字符 =" + (char) buffer.get() + " KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK");
        /*
        读读读读读度读读  第二个字节转为字符 =b
        读读读读读度读读  第三个字节转为字符 =c
        */


    }

    // get(i) 指定下标获取，下标不会改变
    @Test
    public void testGetParam()
    {
        ByteBuffer buffer = ByteBuffer.allocate(10);

        buffer.put(new byte[]{'a','b','c','d'});

        buffer.flip();// // 切换至 读模式 [ position指针指向开头， limit指向写入的最后位置 (内存长度) ]
        System.out.println("读读读读读度读读  第1个字节转为字符 =" + (char) buffer.get());
        System.out.println("读读读读读度读读  第2个字节转为字符 =" + (char) buffer.get());

        debugAll(buffer);

        /*
        读读读读读度读读  第二个字节转为字符 =a
        读读读读读度读读  第三个字节转为字符 =b
        */

        System.out.println("指定下标读=====" + (char)buffer.get(0));
        System.out.println("指定下标读=====" + (char)buffer.get(0));
        /*
        指定下标读=====a
        指定下标读=====a
        */
        debugAll(buffer);

        // 可以看到 经过两次get(i) 指定下标获取之后，下标并不会改变
    }





}
