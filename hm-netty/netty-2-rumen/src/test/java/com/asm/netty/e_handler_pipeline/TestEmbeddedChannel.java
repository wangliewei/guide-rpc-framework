package com.asm.netty.e_handler_pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * netty提供的 EmbeddedChannel 测试方法
 *  可以 绑定 很多的 handler 进行测试
 *
 *
 * 入站：服务端 处理 客户端 write事件
 *
 */
@Slf4j
public class TestEmbeddedChannel {

    public static void main(String[] args) {
        // 1. 入站
        ChannelInboundHandlerAdapter h1 = new ChannelInboundHandlerAdapter(){
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                log.debug("1");
                super.channelRead(ctx, msg);
            }
        };
        // 2. 入站
        ChannelInboundHandlerAdapter h2 = new ChannelInboundHandlerAdapter(){// 当前handler起名 h2
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                log.debug("2");
                super.channelRead(ctx, msg);
            }
        };
        // 3. 入站
        ChannelInboundHandlerAdapter h3 = new ChannelInboundHandlerAdapter(){
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                super.channelRead(ctx, msg);
                log.debug("3333333333333333");
                ctx.channel().writeAndFlush(msg);   //  【最后一个handler往前找】
//                ctx.writeAndFlush(msg);   //  【当前节点往上找 出站处理器】
            }
        };
        // 4. 出站
        ChannelOutboundHandlerAdapter h4 = new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                log.debug("4");
                super.write(ctx, msg, promise);
            }
        };
        // 5. 出站
        ChannelOutboundHandlerAdapter h5 = new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                log.debug("5");
                super.write(ctx, msg, promise);
            }
        };

        final EmbeddedChannel channel = new EmbeddedChannel(h1, h2, h3, h4, h5);

        // 模拟 入站
        channel.writeInbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("hello".getBytes()));
        System.out.println( ((ByteBuf)channel.readOutbound()) .toString(Charset.defaultCharset()));
        /*000000
        10:56:55 [DEBUG] [main] c.a.n.e.TestEmbeddedChannel - 1
        10:56:55 [DEBUG] [main] c.a.n.e.TestEmbeddedChannel - 2
        hello
         */
        // 模拟 出站
//        channel.writeOutbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("hellooutbound".getBytes()));
//        System.out.println( ((ByteBuf)channel.readOutbound()) .toString(Charset.defaultCharset()));
        /*
        11:05:41 [DEBUG] [main] c.a.n.e.TestEmbeddedChannel - 4
        11:05:41 [DEBUG] [main] c.a.n.e.TestEmbeddedChannel - 3
        hellooutbound
         */

    }


}
