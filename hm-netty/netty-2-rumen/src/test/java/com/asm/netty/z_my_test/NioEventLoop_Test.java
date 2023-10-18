package com.asm.netty.z_my_test;

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

@Slf4j
public class NioEventLoop_Test {

    public static void main(String[] args) throws InterruptedException {

        new ServerBootstrap()
                // 增加两个 nio 工人
                .group(new NioEventLoopGroup(1),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {

                        ch.pipeline().addLast(
                                new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                        super.channelRead(ctx, msg);
                                        ByteBuf byteBuf = msg instanceof ByteBuf ? ((ByteBuf) msg) : null;

                                        if( byteBuf != null)
                                        {
                                            final byte[] buf = new byte[16];
                                            byteBuf.readBytes(buf, 0, byteBuf.readableBytes());

                                            log.debug(new String(buf));

                                        }

                                    }

                                }
                        );

                    }
                })
                .bind(8080)
                .sync();
    /*
    两个工人轮流处理 channel， 工人与 channel 之间进行了绑定
13:41:23 [DEBUG] [nioEventLoopGroup-3-1] c.a.n.z.NioEventLoop_Test - aaaa
13:41:25 [DEBUG] [nioEventLoopGroup-3-1] c.a.n.z.NioEventLoop_Test - aaaa
13:41:33 [DEBUG] [nioEventLoopGroup-3-2] c.a.n.z.NioEventLoop_Test - bbbb
13:41:35 [DEBUG] [nioEventLoopGroup-3-2] c.a.n.z.NioEventLoop_Test - bbbb
13:41:42 [DEBUG] [nioEventLoopGroup-3-1] c.a.n.z.NioEventLoop_Test - cccc
13:41:44 [DEBUG] [nioEventLoopGroup-3-1] c.a.n.z.NioEventLoop_Test - cccc
     */



    }




    @Test
    public void onlyTest()
    {
        log.debug("abc");
    }

}


