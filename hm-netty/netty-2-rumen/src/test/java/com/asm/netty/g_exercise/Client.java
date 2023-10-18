package com.asm.netty.g_exercise;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.Scanner;

@Slf4j
public class Client {

    public static void main(String[] args) throws InterruptedException {

        final NioEventLoopGroup group = new NioEventLoopGroup();

        final Channel channel = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {

                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                super.channelRead(ctx, msg);
                                ByteBuf buffer = (ByteBuf) msg;
                                log.debug(buffer.toString());
                                log.debug(buffer.toString(Charset.defaultCharset()));

                            }
                        });

                    }
                })
//                .connect("47.100.173.102", 6000).sync().channel();
                .connect("localhost", 8000).sync().channel();


        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while(true){

                final String line = scanner.nextLine();
                if ("q".equals(line))
                {
                    channel.close();
                    break;
                }

                channel.writeAndFlush(line);
            }

        }).start();
        // closeFuture() 异步等待 channel关闭才执行
        channel.closeFuture().addListener(future -> {
            group.shutdownGracefully();
        });

    }


}
