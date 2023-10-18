package com.asm.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import org.junit.Test;

import java.net.InetSocketAddress;

public class HelloClient {

    public static void main(String[] args)  throws InterruptedException{

//    }
//
//    @Test
//    public  void helloNettyClient() throws InterruptedException {
        // 1. 客户端 启动类 ，组装 协调 下面很多组件 进行启动
        new Bootstrap()
                // 2. 添加 EventLoop, 包含线程 和 选择器
                .group(new NioEventLoopGroup())
                // 3. 选择客户端 channel 实现
                .channel(NioSocketChannel.class)
                // 4. 添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    // 5. 连接建立后被调用
                    protected void initChannel(NioSocketChannel ch) throws Exception {

                        //  StringEncoder 把 下面发送的字符串 编码
                        ch.pipeline().addLast(new StringEncoder());


                    }
                })
                // 5. 连接 服务器
                .connect(new InetSocketAddress("localhost", 8080))
                // 6. sync为了让客户端先同步的方式连上，然后再执行后面信息发送逻辑
                .sync()
                .channel()
                // 向服务器 发送信息
                .writeAndFlush("hello.word");







    }




}
