package com.asm.netty.a_stickandhalf_bag_5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Random;

public class Client5 {
/*
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 */
    static final Logger log = LoggerFactory.getLogger(Client5.class);

    public static void main(String[] args) {

        send();
        System.out.println("send......finish......");

    }

    private static void send() {
        final NioEventLoopGroup worker = new NioEventLoopGroup();

        try{
            final Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(worker);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        // channel连接建立好之后 出发 channelActive() 时间
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            log.debug("sending...");
                            final ByteBuf buf = ctx.alloc().buffer();

                            send(buf, "hello, world");
                            send(buf, "hi");

                            ctx.writeAndFlush(buf);
                        }
                    });
                }
            });

            final ChannelFuture channelFuture = bootstrap.connect("localhost", 8080).sync();
            channelFuture.channel().closeFuture().sync();


        } catch (InterruptedException e)
        {
            log.error("Client error", e);
        } finally {
            worker.shutdownGracefully();
        }
    }

    private static void send(ByteBuf buf, String s) {
        byte[] bytes = s.getBytes();        // 实际内容
        final int length = bytes.length;    // 实际内容长度

        buf.writeInt(length);               // 指定长度 和 存储模式为： 大端模式  【writeInt本身长度 4字节】 【服务端 lengthFieldLength设为4】

        buf.writeBytes(new byte[]{'1','0','0'}); // 版本号 100，  【服务端 lengthAdjustment 设为 3】 【注意：如果设0 会报错(除非主体内容最后四位都是0)】

        buf.writeBytes(bytes);
    }
}
