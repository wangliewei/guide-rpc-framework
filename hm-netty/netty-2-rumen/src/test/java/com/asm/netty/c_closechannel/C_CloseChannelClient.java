package com.asm.netty.c_closechannel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

@Slf4j
/**
 * 客户端 使用控制台 ，不停 向 服务端 传值
 * 直到输入 "q" 才停止

 * closeFuture() 用来处理 channel 的关闭
     * sync 方法作用是同步等待 channel 关闭
     * 而 addListener 方法是异步等待 channel 关闭

 q
 14:30:44 [DEBUG] [input] c.a.n.b.C_CloseChannelClient - 准备关闭...
 14:30:44 [DEBUG] [input] c.a.n.b.C_CloseChannelClient - 关闭已提交...
 14:30:44 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0xb4d13fc9, L:/127.0.0.1:2531 - R:localhost/127.0.0.1:8080] CLOSE
 14:30:44 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0xb4d13fc9, L:/127.0.0.1:2531 ! R:localhost/127.0.0.1:8080] INACTIVE
 14:30:44 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0xb4d13fc9, L:/127.0.0.1:2531 ! R:localhost/127.0.0.1:8080] UNREGISTERED


 *
 * 第一种 同步 执行关闭后处理
 q
 14:53:42 [DEBUG] [input] c.a.n.b.C_CloseChannelClient - 准备关闭...
 14:53:42 [DEBUG] [input] c.a.n.b.C_CloseChannelClient - 关闭已提交...
 14:53:42 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0x33fd18b8, L:/127.0.0.1:10202 - R:localhost/127.0.0.1:8080] CLOSE
 14:53:42 [DEBUG] [main] c.a.n.b.C_CloseChannelClient - 已关闭...
 14:53:42 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0x33fd18b8, L:/127.0.0.1:10202 ! R:localhost/127.0.0.1:8080] INACTIVE
 14:53:42 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0x33fd18b8, L:/127.0.0.1:10202 ! R:localhost/127.0.0.1:8080] UNREGISTERED

 * 第二种 异步关闭线程 执行关闭后处理
 q
 15:03:01 [DEBUG] [input] c.a.n.b.C_CloseChannelClient - 准备关闭...
 15:03:01 [DEBUG] [input] c.a.n.b.C_CloseChannelClient - 关闭已提交...
 15:03:01 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0x152f4d23, L:/127.0.0.1:8031 - R:localhost/127.0.0.1:8080] CLOSE
 15:03:01 [DEBUG] [nioEventLoopGroup-2-1] c.a.n.b.C_CloseChannelClient - 已关闭... 来自异步关闭线程
 15:03:01 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0x152f4d23, L:/127.0.0.1:8031 ! R:localhost/127.0.0.1:8080] INACTIVE
 15:03:01 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0x152f4d23, L:/127.0.0.1:8031 ! R:localhost/127.0.0.1:8080] UNREGISTERED
 *
 * 加入 优雅关闭  group.shutdownGracefully()
 q
 15:53:35 [DEBUG] [input] c.a.n.b.C_CloseChannelClient - 准备关闭...
 15:53:35 [DEBUG] [input] c.a.n.b.C_CloseChannelClient - 关闭已提交...
 15:53:35 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0xffdad7c2, L:/127.0.0.1:7848 - R:localhost/127.0.0.1:8080] CLOSE
 15:53:35 [DEBUG] [nioEventLoopGroup-2-1] c.a.n.b.C_CloseChannelClient - 已关闭... 来自异步关闭线程
 15:53:35 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0xffdad7c2, L:/127.0.0.1:7848 ! R:localhost/127.0.0.1:8080] INACTIVE
 15:53:35 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0xffdad7c2, L:/127.0.0.1:7848 ! R:localhost/127.0.0.1:8080] UNREGISTERED
 Disconnected from the target VM, address: '127.0.0.1:10263', transport: 'socket'
 */
public class C_CloseChannelClient {

    private static int port = 8080;

    public static void main(String[] args) throws InterruptedException {

        final NioEventLoopGroup group = new NioEventLoopGroup();
        final ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG)); // debug channel 流程 和 状态【注意logback.xml也要配置】
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", port));
        /*
            让线程进入wait状态，也就是main线程暂时不会执行到finally里面，
            nettyserver也持续运行，如果监听到关闭事件，可以优雅的关闭通道和nettyserver
         */
        final Channel channel = channelFuture.sync().channel();

        log.debug("channel === {}", channel);

        // 子线程 接收 控制台 输入，并传输给 服务端
        new Thread(()->{

            final Scanner scanner = new Scanner(System.in);

            while(true)
            {
                System.out.println("before....nextLine.........");
                final String line = scanner.nextLine();
                System.out.println("after....nextLine.........");
                if("q".equals(line)) // 输入 "q" 停止操作
                {
                    log.debug("准备关闭...");
                    channel.close(); // 异步操作， close调完了 ，交给其他线程操作的 (譬如：1s之后操作)
                    log.debug("关闭已提交...");
                    break;
                }
                channel.writeAndFlush(line);
            }

        },"input").start();

        final ChannelFuture closeFuture = channel.closeFuture();

// <<<<<<<<<<<<<<<< HEAD 第一种 同步 执行关闭后处理

//        closeFuture.sync();// 阻塞 等待 channel.close() 掉 再继续往下走
//        log.debug("已关闭...");
//        group.shutdownGracefully();


// ================= 第二种 异步关闭线程 执行关闭后处理
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                log.debug("已关闭... 来自异步关闭线程");
                // 优雅 关闭：1）首先切换 `EventLoopGroup` 到关闭状态 拒绝新的任务的加入 2）任务队列的任务都处理完成后，停止线程的运行
                group.shutdownGracefully();
            }
        });

// >>>>>>>>>>>>>>>>> END



    }





}
