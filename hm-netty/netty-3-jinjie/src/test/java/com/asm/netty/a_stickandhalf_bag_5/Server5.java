package com.asm.netty.a_stickandhalf_bag_5;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * ################################################
 * ######       基于长度字段的 帧解码器       ########
 * ################################################
 *
 *  * BEFORE DECODE (14 bytes)         AFTER DECODE (14 bytes)
 *  * +--------+----------------+      +--------+----------------+
 *  * | Length | Actual Content |----->| Length | Actual Content |
 *  * | 0x000C | "HELLO, WORLD" |      | 0x000C | "HELLO, WORLD" |
 *  * +--------+----------------+      +--------+----------------+
 *  服务器：获取内容长度，从0(lengthFieldOffset=0)开始，先读两(lengthFieldLength=2)个字节，000c = 12，知道内容长度是12
 *         再读 12个字节
 *  * </pre>
 * LengthFieldBasedFrameDecoder(
 *          int maxFrameLength,     限制最大长度，超过他没找到分隔符报错
 *          int lengthFieldOffset,  长度字段 的 偏移量
 *          int lengthFieldLength,  长度字段 本身长度
 *          int lengthAdjustment,   长度字段 为基准，跳过几个字节 才是内容
 *          int initialBytesToStrip 从头剥离 几个字节，解析后将不出现
 * )
 *
 */
@Slf4j
public class Server5 {

    void start(){
        final NioEventLoopGroup boss = new NioEventLoopGroup();
        final NioEventLoopGroup worker = new NioEventLoopGroup();

        try{
            final ServerBootstrap bs = new ServerBootstrap();
            bs.channel(NioServerSocketChannel.class);

            // 调整系统的接收缓冲区 设置字节 【使 发生半包】
            // bs.option(ChannelOption.SO_RCVBUF, 10);
            // 调整netty的接受缓冲区 (byteBuf)  【这里最小就是16，因为他是16的整数倍】 【注意还要调整 客户端每次发送字节 > 16个】
            // bs.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(16,16,16));

            bs.group(boss, worker);
            bs.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
// head<<<<<<<<<
                    // 解码器 【以下参数 要和 客户端约定好 】
                    ch.pipeline().addLast(
                        new LengthFieldBasedFrameDecoder(
                            1024,0,4,3,7)
                    );
// =============

// end>>>>>>>>>>
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));

                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf buf = (ByteBuf) msg;
                            System.out.println("有信息进来了。。。。");
                            log.debug( buf.toString(Charset.defaultCharset()) );
                        }
                    });
                }
            });

            final ChannelFuture channelFuture = bs.bind(8080).sync();
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
        new Server5().start();
    }

}
