package com.asm.netty.a_stickandhalf_bag_4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * ################################################
 * ######   行解码器 【效率低 每个字节检查】   ########
 * ################################################
 * 即：分隔符 解决消息的边界
 * 使用：基于行的帧解码器 类 【分隔符 ：'\n'】     LineBasedFrameDecoder(构造方法给最大长度，超过这个长度还没换行符则抛异常)
 *    还有一种类似的【自定义分隔符】 DelimiterBasedFrameDecoder(构造指定长度，ByteBuf类型的 分隔符)
 * 说明：通过linux换行:\n  和  win换行:\r\n 换行
 *
 */
@Slf4j
public class Server4 {

    void start(){
        final NioEventLoopGroup boss = new NioEventLoopGroup();
        final NioEventLoopGroup worker = new NioEventLoopGroup();

        try{
            final ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);

            // 调整系统的接收缓冲区 设置字节 【使 发生半包】
            // serverBootstrap.option(ChannelOption.SO_RCVBUF, 10);
            // 调整netty的接受缓冲区 (byteBuf)  【这里最小就是16，因为他是16的整数倍】 【注意还要调整 客户端每次发送字节 > 16个】
            // serverBootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(16,16,16));

            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
// head<<<<<<<<<
                    /*################################################*/
                    /*####                 行解码器                 ###*/
                    /*################################################*/
//                    ch.pipeline().addLast(new LineBasedFrameDecoder(1024)); // 超出1024报错

// =============
                    /*################################################*/
                    /*####            自定义定界符 帧解码器           ###*/
                    /*################################################*/
                    final ByteBuf buf = Unpooled.wrappedBuffer(new byte[]{'\r','\n'});// 客户端分隔符必须 "\r\n"
                    ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,buf)); // 超出1024报错
// end>>>>>>>>>>
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));

/*                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf buf = (ByteBuf) msg;
                            System.out.println("有信息进来了。。。。");
                            log.debug( buf.toString(Charset.defaultCharset()) );
                        }
                    });*/
                }
            });

            final ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();

        }catch (InterruptedException e)
        {
            log.error("server error", e);

        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        new Server4().start();
    }

}
