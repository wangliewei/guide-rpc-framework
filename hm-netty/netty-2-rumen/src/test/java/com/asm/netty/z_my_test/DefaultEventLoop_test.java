package com.asm.netty.z_my_test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultEventLoop_test {

    public static void main(String[] args) throws InterruptedException {

        // 新增两个 非 nio工人
        final DefaultEventLoopGroup normalWorkers = new DefaultEventLoopGroup(2);

        new ServerBootstrap()
                .group(new NioEventLoopGroup(1), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                // ChannelInitializer 触发处理器(SocketChannel连接后 才执行)，负责添加别的 handler;
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    // initChannel可以添加更多处理器
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 添加具体 handler
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(normalWorkers, "myhandler",
                                // ChannelInboundHandlerAdapter 自定义的 handler
                                new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        final ByteBuf byteBuf = msg instanceof ByteBuf ? ((ByteBuf) msg) : null;
                                        if(byteBuf != null)
                                        {
                                            final byte[] buf = new byte[16];
                                            final ByteBuf len = byteBuf.readBytes(buf, 0, byteBuf.readableBytes());
                                            log.debug(new String(buf));
                                        }
                                    }
                                }
                        );
                    }
                })
                .bind(8080).sync();



    }






}
