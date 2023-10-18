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
 *  super.channelRead 唤醒下一个处理器 并把数据交给它
 *  生产线流程 必须 <red>ctx.fireChannelRead 或 super.channelRead 二选一</red>，不然链 会断
 *
 */
public class A_Pipeline2 {

    /**
     *
     * 客户端发送数据，将依次执行 三个 handler
     17:59:16 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline2 - 11111
     17:59:16 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline2 - 22222 王五
     17:59:16 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline2 - 33333  msg = A_Pipeline2.Student(name=王五); class=class com.asm.netty.e_handler_pipeline.A_Pipeline2$Student
     17:59:16 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline2 - hout_66666
     17:59:16 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline2 - hout_55555
     17:59:16 [DEBUG] [nioEventLoopGroup-2-2] c.a.n.e.A_Pipeline2 - hout_44444
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

                        // 获取到msg 转Bytebuf 交给 super.channelRead
                        pipeline.addLast("h1",new ChannelInboundHandlerAdapter(){// 当前handler起名 h1
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                                ByteBuf buf = (ByteBuf) msg;
                                String s = buf.toString(Charsets.UTF_8);
                                log.debug("11111 {}",s);

                                super.channelRead(ctx, s);
                            }
                        });
                        // 获取到msg new 对象 交给 super.channelRead
                        pipeline.addLast("h2",new ChannelInboundHandlerAdapter(){// 当前handler起名 h2
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("22222 {}" , msg.toString());
                                final Student student = new Student(msg.toString());
                                super.channelRead(ctx, student);
                            }
                        });

                        // super.channelRead(ctx, msg); 不需调用 无需唤醒下一个入站处理器
                        pipeline.addLast("h3",new ChannelInboundHandlerAdapter(){// 当前handler起名 h3
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                                log.debug("33333  msg = {}; class={}",msg,msg.getClass());
//                                ctx.channel().write(msg);   //  【从尾部开始查找出站处理器】
                                ch.writeAndFlush(ctx.alloc().buffer().writeBytes("service write 33333333333".getBytes()));
                            }
                        });
                        // 添加 三个 出站 的 handler, ChannelOutbound 出站 方向 相反
                        pipeline.addLast("h4", new ChannelOutboundHandlerAdapter(){

                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("hout_44444");
                                super.write(ctx, msg, promise);  //  == ctx.write 【是从当前节点找上一个出站处理器】

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

    /**
     * 创建类
     */
    @Data
    @AllArgsConstructor
    static class Student{
        private String name;
    }



}
