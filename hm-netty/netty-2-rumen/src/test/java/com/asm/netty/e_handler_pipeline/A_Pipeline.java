package com.asm.netty.e_handler_pipeline;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 * 服务端 测试
 * 客户端 使用 C_CloseChannelClient
 *
 * 知识点： ChannelHandlerContext ctx.alloc().buffer() 分配一个 ByteBuf
 *         ChannelInbound  入站 方向 同加入顺序
 *                         入站里 调用 ch.writeAndFlush方法 才能触发出站 动作
 *         ChannelOutbound 出站 方向 相反
 *
 */
public class A_Pipeline {

    /**
     *
     * 客户端发送数据，将依次执行 三个 handler
     22:00:42 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline - 11111
     22:00:42 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline - 22222
     22:00:42 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline - 33333
     22:00:42 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline - hout_66666
     22:00:42 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline - hout_55555
     22:00:42 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline - hout_44444
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

                        // 2. 添加处理器  netty加handler时 有两个 handler  head -> tail
                        //    每次添加的位置 是 这两个中间 ： head -> h1 h2 h3 -> tail
                        pipeline.addLast("h1",new ChannelInboundHandlerAdapter(){// 当前handler起名 h1
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("11111");
                                super.channelRead(ctx, msg);
                            }
                        });

                        pipeline.addLast("h2",new ChannelInboundHandlerAdapter(){// 当前handler起名 h2
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("22222");
                                super.channelRead(ctx, msg);
                            }
                        });

                        pipeline.addLast("h3",new ChannelInboundHandlerAdapter(){// 当前handler起名 h3
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("33333");
                                super.channelRead(ctx, msg);
                                /*-------------------------------------------------------
                                |   入站里 调用 ch.writeAndFlush()方法 才能触发出站 动作    |
                                ========================================================*/
                                ch.writeAndFlush(ctx.alloc().buffer().writeBytes("service write 33333333333".getBytes()));
                            }
                        });
                        // 添加 三个 出站 的 handler, ChannelOutbound 出站 方向 相反
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
