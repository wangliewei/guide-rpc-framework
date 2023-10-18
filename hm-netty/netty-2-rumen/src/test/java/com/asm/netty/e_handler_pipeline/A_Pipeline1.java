package com.asm.netty.e_handler_pipeline;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * ctx.channel().write(msg) vs ctx.write(msg) 的区别
 */
@Slf4j
public class A_Pipeline1 {

    /**
     * 【 ----------- 顺序 测试 ------------ 】
     * 入站处理器中，ctx.fireChannelRead(msg) 是 **调用下一个入站处理器**
     * 如果注释掉 1 处代码，则仅会打印 1
     * 如果注释掉 2 处代码，则仅会打印 1 2
     *
     * 3 处的 ctx.channel().write(msg) 会 **从尾部开始触发** 后续出站处理器的执行
     * 如果注释掉 3 处代码，则仅会打印 1 2 3
     * 类似的，出站处理器中，<b>ctx.write(msg, promise)</b> 的调用也会 **触发上一个出站处理器**
     * 如果注释掉 6 处代码，则仅会打印 1 2 3 6
     *
     * 【  ctx.channel().write(msg)  和  ctx.write(msg)  】
     * 都是触发出站处理器的执行
     * <b>ctx.channel().write(msg)</b> 从尾部开始查找出站处理器
     * <b>ctx.write(msg)</b>           是从当前节点找上一个出站处理器
     * 3 处的 ctx.channel().write(msg) 如果改为 ctx.write(msg) 仅会打印 1 2 3，因为节点3 之前没有其它出站处理器了
     * 6 处的 ctx.write(msg, promise) 如果改为 ctx.channel().write(msg) <error>会打印 1 2 3 6 6 6...</error>
     */
    public static void main(String[] args) {

        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {

//                        ch.pipeline().addLast(new ChannelOutboundHandlerAdapter(){
//                            @Override
//                            public void write(ChannelHandlerContext ctx, Object msg,
//                                              ChannelPromise promise) {
//                                System.out.println("000");
//                                /*-------------------------------------
//                                |  ctx.write 触发上一个 出站处理器       |
//                                |  从当前节点找上一个出站处理器           |
//                                ======================================*/
//                                ctx.write(msg, promise); // 0
//                            }
//                        });

                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("1");
                                /*-------------------------------------------------------------
                                |   super.channelRead(ctx, msg) == ctx.fireChannelRead(msg)    |
                                |   同样向下找 入站Handler                                       |
                                ===============================================================*/
                                ctx.fireChannelRead(msg); // 1

                            }
                        });
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                log.debug("2");
                                ctx.fireChannelRead(msg); // 2
                            }
                        });
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                log.debug("3");
//                                ctx.channel().write(msg); // 3   【从尾部开始查找出站处理器】
                                /*-------------------------------------------------------
                                |   入站里 调用 ch.writeAndFlush()方法 才能触发出站 动作    |
                                |   [从尾部开始查找出站处理器]                             |
                                |   而 ctx.writeAndFlush 是从当前节点向上                 |
                                ========================================================*/
                                ch.writeAndFlush(ctx.alloc().buffer().writeBytes("service write 33333333333".getBytes()));
                            }
                        });
                        ch.pipeline().addLast(new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg,
                                              ChannelPromise promise) {
                                log.debug("4");
                                /*-------------------------------------
                                |  ctx.write 触发上一个 出站处理器       |
                                |  从当前节点找上一个出站处理器           |
                                ======================================*/
                                ctx.write(msg, promise); // 4
//                                ctx.channel().write(msg);// [[[ 123654 654 654 。。。 报错 ]]]
                            }
                        });
                        ch.pipeline().addLast(new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg,
                                              ChannelPromise promise) {
                                log.debug("5");
                                ctx.write(msg, promise); // 5       触发上一个 出站处理器 4
                            }
                        });
                        ch.pipeline().addLast(new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg,
                                              ChannelPromise promise) {
                                log.debug("6");
                                ctx.write(msg, promise); // 6       触发上一个 出站处理器 5
                            }
                        });
                    }
                })
                .bind(8080);


    }
}
