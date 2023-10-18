package com.asm.netty.e_handler_pipeline;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.io.IOException;

public class A_Client {

    static int  port = 8080; // 6000
    public static void main(String[] args) throws IOException {
        // 一个ChannelFuture对象代表尚未发生的IO操作
        final ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect("127.0.0.1", port)
                .addListener((ChannelFutureListener) future -> {
//                    future.channel().writeAndFlush("00202 02110 00002 20010 28032 000FF FFFFF FFFFF FFFF0 00000 01000 00000 00000 00000 08000 00000 00080 00000 01");
                    future.channel().writeAndFlush("王五");
                });

        System.in.read();
    }

}
