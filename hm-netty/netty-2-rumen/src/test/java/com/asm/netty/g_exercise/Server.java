package com.asm.netty.g_exercise;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.nio.charset.Charset;

@Slf4j
public class Server {

    public static void main(String[] args) {

        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {

                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                super.channelRead(ctx, msg);
                                ByteBuf buffer = (ByteBuf) msg;
                                log.debug(buffer.toString(Charset.defaultCharset()));

                                // 建议使用 ctx.alloc() 创建 ByteBuf
                                final ByteBuf response = ctx.alloc().buffer(); // class io.netty.buffer.PooledUnsafeDirectByteBuf
                                response.writeBytes("服务端:".getBytes());
                                response.writeBytes(buffer);
                                ctx.writeAndFlush(response);
                            }
                        });
                    }
                })
                .bind(8080);
    }

    @Test
    public void onlyTest()
    {
        String a = "abcdefg";
        System.out.println(a);

        System.exit(0);
        if(true)return;

        String b = "hijklmn";
        System.out.println(b);
    }
}
