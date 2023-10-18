package com.asm.netty.c4_nio.b_nonblock;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import static com.asm.netty.utils.ByteBufferUtil.debugAll;


/**
 * 服务端
 *  这里用 单线程 调试 非阻塞模式
 *
 * 缺点：
 *  while一直循环 消耗cpu性能
 */
@Slf4j
public class Server {


    /*
        使用 nio 来理解  非阻塞模式
        这里使用 单线程
    */
    @Test
    public void testServer1() throws IOException {

        // 0. 创建全局的 ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 1. 创建一个 服务器 对象
        ServerSocketChannel ssc = ServerSocketChannel.open();
        /**
         *
         * ##########################################
         * 切换 ServerSocketChannel 为 非阻塞 模式， 默认true是阻塞的
         *  将 影响 accept 变成 非阻塞方法
         * ##########################################
         */
        ssc.configureBlocking(false);


        // 2. 绑定一个 监听端口
        ssc.bind(new InetSocketAddress(8080));

        // 3. 连接 集合
        ArrayList<SocketChannel> channels = new ArrayList<>();

        log.debug("connecting...");

        /**
         * 这里 线程没有停，一直在循环
         */
        while(true)  // accept 能 多次调用 使用 wile循环
        {

            // 4. 建立 客户端连接,通过 TCP三次握手， accept
            // 返回一个 SocketChannel 读写通道，方便与客户端通信 进行数据读写
            SocketChannel sc = ssc.accept(); // 此时是 非阻塞方法， 【如果没建立连接 sc=null】


            if(sc != null)
            {
                log.debug("connected... {}", sc);
                /**
                 *
                 * ##########################################
                 * 切换 SocketChannel 为 非阻塞 模式， 默认true是阻塞的
                 *  将 影响 read 变成 非阻塞方法
                 * ##########################################
                 */
                sc.configureBlocking(false);
                // 添加 连接通道
                channels.add(sc);
            }


            // 5. 接收 客户端的 数据， 进行遍历处理
            for(SocketChannel channel : channels)
            {


                int len = channel.read(buffer); // 此时是 非阻塞方法， 【如果没读到数据 返回 0】

                if(len > 0)
                {
                    log.debug("before read ... {}", channel);

                    buffer.flip(); //  读模式 (position下标 = 0) 进行 从头get

                    debugAll(buffer);

                    buffer.clear(); //  写模式(position下标 = 容量)，继续 循环写

                    log.debug("after read ... {}", channel);

                }
            }
        }

    }

}
