package com.asm.netty.c1;

import java.nio.ByteBuffer;

public class TestByteBufferAllocate {


    public static void main(String[] args) {

        System.out.println(ByteBuffer.allocate(16).getClass());
        System.out.println(ByteBuffer.allocateDirect(16).getClass());
        /*
            class java.nio.HeapByteBuffer    -java堆内存， 读写效率低， 受垃圾回收 GC的影响
                                             -适用于数据量较小、频繁创建和释放的场景


            class java.nio.DirectByteBuffer  -直接内存，读写效率高(少一次拷贝)，不会受 GC的影响
                                             -使用完后 需要彻底的释放，以免内存泄露
                                             -适用于需要频繁进行I/O操作的场景，对于大量的数据存取操作，可以提高性能。

         */


        /*
        HeapByteBuffer 类具有以下属性和方法：

position（位置）：position 属性表示当前缓冲区的位置，即下一个要读取或写入的字节的索引。初始时，position 的值为 0。
                通过调用 position() 方法可以获取当前位置的值，
                通过调用 position(int newPosition) 方法可以设置新的位置值。
                每一次 get() or get(byte[])时，position++; (注意不是get(i),get(i)不会让position++)

limit（限制）   ：limit 属性表示缓冲区中可读取或写入的字节的索引的上限。初始时，limit 的值等于缓冲区的容量。
                通过调用 limit() 方法可以获取当前限制的值，
                通过调用 limit(int newLimit) 方法可以设置新的限制值。

capacity（容量）：capacity 属性表示缓冲区的容量，即缓冲区可以存储的最大字节数。
                通过调用 capacity() 方法可以获取当前容量的值。

此外，HeapByteBuffer 类还提供了一些其他的方法，用于读取和写入字节数据，例如 get()、put()、get(byte[] dst)、put(byte[] src) 等方法。

需要注意的是，position 的值必 <= limit 的值，
          而 limit 的值必 <= capacity 的值


flip    切换读模式  切换至 读模式 [ position指针指向开头， limit指向写入的最后位置 (内存长度)
rewind  下标 position 设 0 , 从头开始，而不会改limit值

mark    mark一下，给position做标记  // : java.nio.HeapByteBuffer[pos=1 lim=4 cap=10]
reset   将 postion 重置到 mark 位置

compact 未读完部分向前压缩，以便在下一次调用该方法时继续读取剩余的数据。


         */


    }




}
