package com.asm.netty.c4_nio.e_write;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;


/**
    主要内容：
    1). |-  首先创建 服务器通道 并注册到selector中
循环里：
    2). |-  事件集合 迭代判断
    3). |----- 如果是 连接事件，
          ---------- 1️⃣ 创建读写通道，注册到selector；
          ---------- 2️⃣ 创建大量数据放入buffer中，并 向客户端write 一次
    4).   ---------------- 如果这次写不完：关注可写事件 + 挂载buffer数据到key

    5). |----- 如果是 可写事件(等待下一次缓冲区 可写了 继续写),
          ---------- 1️⃣ 从key里获取挂载的buffer 和 SocketChannel channel 进行写
          ---------- 2️⃣ 因为：只要向 channel 发送数据时，socket 缓冲可写，这个事件会频繁触发
                        所以：如果 写完了， 没有可写内容了， 将取消挂载的数据，buffer内存 将得到 释放

 */
public class WriteServer {

    public static void main(String[] args) throws IOException {

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false); // selector 必须工作在非阻塞模式下  影响 accept 变成 非阻塞方法
        ssc.bind(new InetSocketAddress(8080));

        Selector selector = Selector.open();// 创建selector管理多个channel
        ssc.register(selector, SelectionKey.OP_ACCEPT);// 服务器通道 注册到 selector 并 关注 连接事件

        int count = 0;

        while(true)
        {
            selector.select(); // 有未处理或未取消事件，不阻塞 继续运行 ； 否则 阻塞
System.out.println(">>>>>>>>>>>>>>> while beging >>>>>>>>>>>>>>>");

            // 迭代 事件集合
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

            while(iter.hasNext())
            {
                SelectionKey key = iter.next();
                iter.remove();

                // 连接事件 发生后：创建读写通道 注册到selector + 创建大量数据 向客户端发送write
                if(key.isAcceptable())
                {
System.out.println("------ 检测到 读事件");
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false); // 影响 read 变成 非阻塞方法
                    SelectionKey scKey = sc.register(selector, 0, null);


                    // 向客户端 发送大量 数据
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 30000000; i++) {

                        sb.append("a");

                    }

                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());


                    // >>>>> ===================================
                    /**
                     * 很多字节 发送客户端时，网络资源有限，通过write缓冲区 容易把channel写满了写不进去了，
                     * 将分多次写，这样导致其他客户端的channel将被阻塞
                     */
/*

                    while(buffer.hasRemaining()) // 是否有剩余字节
                    {
                        // 底层是 发送缓冲区 控制一次发多少的
                        int write = sc.write(buffer); // 实际写入 字节数


                        System.out.println(write);
                        count += write;
                    }

                }

*/

                    // =========================================
                    /*
                     将 上面的 while循环 处理成 多个可写事件
                    */
                    // 先写一次 ，没写完有剩余字节 将关注写事件，将未写完数据挂到 scKey

                    int write = sc.write(buffer); // 实际写入 字节数

                    System.out.println(write);

                    // 写不完了：关注可写事件 + 挂载buffer数据到key
                    if(buffer.hasRemaining()) // 是否有剩余字节
                    {
                        // 关注 可写事件 , 不破坏原来关注的事件  读1 + 写4
                        // 也可以使用 位运算  scKey.interestOps() | SelectionKey.OP_WRITE
                        // 因为：只要向 channel 发送数据时，socket 缓冲可写，这个事件会频繁触发，
                        // 所以：下一轮进入 selector.select() 将又一次 触发 写事件，将进入 else if
                        scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);

                        // 未写完的数据 挂到 scKey 里
                        scKey.attach(buffer);

                    }

                // 等待下一次缓冲区 可写了 继续写
                }else if(key.isWritable())
                {
System.out.println("++++++ 检测到 写事件");
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel sc = (SocketChannel) key.channel();

                    int write = sc.write(buffer);
                    System.out.println(write);
                    count += write;

                    // 因为：只要向 channel 发送数据时，socket 缓冲可写，这个事件会频繁触发
                    // 所以：如果 写完了， 没有可写内容了， 将取消挂载的数据，buffer内存 将得到 释放
                    if(!buffer.hasRemaining())
                    {
                        key.attach(null);

                        // 写完了， 取消 关注 可写事件
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                    }

                }
                    // <<<<< ===================================


            }


System.out.println("<<<<<<<<<<<<<<< while    end <<<<<<<<<<<<<<<");
        }


    }







}
