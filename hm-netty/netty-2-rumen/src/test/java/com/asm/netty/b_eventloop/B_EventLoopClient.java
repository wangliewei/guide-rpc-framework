package com.asm.netty.b_eventloop;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;

@Slf4j
public class B_EventLoopClient {

    public static void main(String[] args) throws InterruptedException, IOException {

        // 1. 启动类
        /*final Channel channel = new Bootstrap()
                // 2. 添加 EventLoop
                .group(new NioEventLoopGroup())
                // 3. 选择 客户端 channel实现
                .channel(NioSocketChannel.class)
                // 4. 添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {

                    // 连接建立 后 调用
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }

                })
                // 5. 连接到 服务器
                .connect(new InetSocketAddress("localhost", 8080))
                .sync()
                .channel();

        System.out.println(channel);

        channel.writeAndFlush("aaaaa");*/

// 拆分 ////////////////////////////////////////
        /**
         * channelFuture : 异步非阻塞 方法
         *  一般带 Future或Promise 都是跟 异步方法 配套使用的；
         */
        final ChannelFuture channelFuture = new Bootstrap()
                // 2. 添加 EventLoop
                .group(new NioEventLoopGroup())
                // 3. 选择 客户端 channel实现
                .channel(NioSocketChannel.class)
                // 4. 添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {

                    // 连接建立 后 调用
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }

                })
                /**
                 * 5. 连接到 服务器
                 * channelFuture : 异步非阻塞方法
                 *  main 发起调用， 真正 connect 连接的 是 NioEventLoopGroup 下某个 EventLoop某个线程
                 */
                .connect(new InetSocketAddress("localhost", 8080));


// <<<<<<< HEAD

        /*
         ------------------------------------------------------------
         |  6.1 同步处理结果； 阻塞 等待 ( 主线程等待，主线程自己拿结果 )  /
         ============================================================
         */
        channelFuture.sync(); // 【阻塞等待中...】 直到nio线程 连接建立完毕  再继续往下执行

        // 无阻塞的向下运行， 如果没有上面的 sync() 方法 的话，这里获取的 channel 将是未建立好连接的
        final Channel channel = channelFuture.channel();
        // log.debug("未使用sync()方法的 channel = {}", channel);// 17:29:36 [DEBUG] [main] c.a.n.b.B_EventLoopClient - 未使用sync()方法的 channel = [id: 0x78cc9e49]
        log.debug("使用sync()方法的 channel = {}", channel);// 18:14:26 [DEBUG] [main] c.a.n.b.B_EventLoopClient - 使用sync()方法的 channel = [id: 0xdc8e5146, L:/127.0.0.1:9361 - R:localhost/127.0.0.1:8080]

        channel.writeAndFlush("sync---2222");

// ============
        /*
         ------------------------------------------------------------
         |  6.2 使用 addListener(回调对象) 异步处理结果                /
         ============================================================
         */
/*        channelFuture.addListener(new ChannelFutureListener() {
            // nio 线程 建立好之后，会调用 此方法
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                final Channel channel = future.channel();
                log.debug("用addListener() 异步处理结果后 得到的 channel = {}", channel);
                // : 18:03:43 [DEBUG] [nioEventLoopGroup-2-1] c.a.n.b.B_EventLoopClient - 用addListener() 异步处理结果后 得到的 channel = [id: 0x8ed66f33, L:/127.0.0.1:10222 - R:localhost/127.0.0.1:8080]
                channel.writeAndFlush("线程建立好后的回调对象addListener异步处理结果---aaaaaaaa");
            }
        });*/

// >>>>>>>>>

        System.in.read();

    }


}
