package com.asm.netty.e_handler_pipeline;

import com.google.common.base.Charsets;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 *  在 A_Pipeline 基础 
 *  ch.writeAndFlush  、 ctx.channel().writeAndFlush     【最后一个handler往前找】
 *  ctx.writeAndFlush                                    【当前节点往上找 出站处理器】
 */
public class A_Pipeline3 {

    /**
     * ch.writeAndFlush && ctx.channel().writeAndFlush  尾部往上找 出站处理器        入站正序 + 出站倒叙
     14:53:28 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline3 - 11111
     14:53:28 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline3 - 33333
     14:53:28 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline3 - hout_66666
     14:53:28 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline3 - hout_55555
     14:53:28 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline3 - hout_44444
     14:53:28 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline3 - hout_22222

     * (80行改为) ctx.writeAndFlush 当前节点往上找 出站处理器                        入站正序 + 出站倒叙
     18:18:12 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline3 - 11111
     18:18:12 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline3 - 33333
     18:18:12 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline3 - hout_22222

     *

     */
    public static void main(String[] args) {

        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                // 以下全部添加 Inbound 的 handler
                .childHandler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 1. 通过 channel 拿到 pipeline
                        final ChannelPipeline pipeline = ch.pipeline();

                        // 入站
                        pipeline.addLast("h1",new ChannelInboundHandlerAdapter(){// 当前handler起名 h1
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("11111");
                                super.channelRead(ctx, msg);
                            }
                        });
                        // 出站
                        pipeline.addLast("h2",new ChannelOutboundHandlerAdapter(){// 当前handler起名 h2

                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("hout_22222");
                                super.write(ctx, msg, promise);

                            }
                        });

                        // 入站
                        pipeline.addLast("h3",new ChannelInboundHandlerAdapter(){// 当前handler起名 h3
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                                log.debug("33333");
                                final byte[] bytes = "service  write 33333333333".getBytes();
//                                ch.writeAndFlush(ctx.alloc().buffer().writeBytes(bytes));      // 【最后一个handler往前找】
//                                ctx.writeAndFlush(ctx.alloc().buffer().writeBytes(bytes)); // 【当前节点往上找 出站处理器】
                                ctx.channel().writeAndFlush(bytes);                              // 【最后一个handler往前找】
                            }
                        });
                        // 出站
                        pipeline.addLast("h4", new ChannelOutboundHandlerAdapter(){

                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("hout_44444");
                                super.write(ctx, msg, promise);

                            }
                        });
                        pipeline.addLast("h5", new ChannelOutboundHandlerAdapter(){

                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("hout_55555");
                                super.write(ctx, msg, promise);
                            }
                        });
                        pipeline.addLast("h6", new ChannelOutboundHandlerAdapter(){

                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("hout_66666");
                                super.write(ctx, msg, promise);
                            }
                        });


                    }
                })
                .bind(8080);



    }


}
