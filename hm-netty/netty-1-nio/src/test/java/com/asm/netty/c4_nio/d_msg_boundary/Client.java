package com.asm.netty.c4_nio.d_msg_boundary;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * 客户端
 */
public class Client {


    /**
     *
     * 与服务器约定好，这里 必须 每个消息 用 \n 结尾
     * @throws IOException
     */
    @Test
    public void testClient1() throws IOException {

        SocketChannel sc = SocketChannel.open();

        // 指定要连接的 服务器 和 端口号
        sc.connect(new InetSocketAddress("localhost", 8080));

        // 向 服务端 写数据
//        sc.write(Charset.defaultCharset().encode("hello\nworld\n"));
        sc.write(Charset.defaultCharset().encode("0123456789abcdef3333\nworld\n"));

        // 等待控制台输入
        System.in.read(); // 阻塞方法

    }


}
