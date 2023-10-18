package com.asm.netty.a_stickandhalf_bag_3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * ################################################
 * ##########    定长解码器 解决黏包      ###########
 * ################################################
 * 建议：找到所有可能的消息 以最长的消息 来定长
 * 1. 譬如固定长 3
 * 2. 进来数据只有2 暂不把消息传给下一个handler，则等拼成3后发送
 * 3. 进来数据超过3，就把长度3先发送下一个handler，多余消息先保留
 *
 */
@Slf4j
public class Server {

    void start(){
        final NioEventLoopGroup boss = new NioEventLoopGroup();
        final NioEventLoopGroup worker = new NioEventLoopGroup();

        try{
            final ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);

            // 调整系统的接收缓冲区 设置字节 【使 发生半包】
            // serverBootstrap.option(ChannelOption.SO_RCVBUF, 10);
            // 调整netty的接受缓冲区 (byteBuf)  【这里最小就是16，因为他是16的整数倍】 【注意还要调整 客户端每次发送字节 > 16个】
            serverBootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(16,16,16));

            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
// head<<<<<<<<<
                    /*################################################*/
                    /*####     定长解码器  【注意放 流水线前面】       ###*/
                    /*################################################*/
                    ch.pipeline().addLast(new FixedLengthFrameDecoder(10)); // 每次定长10
// =============
// end>>>>>>>>>>
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
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
        new Server().start();
    }

    /*
    21:47:45 [DEBUG] [nioEventLoopGroup-3-1] i.n.h.l.LoggingHandler - [id: 0xa41822e8, L:/127.0.0.1:8080 - R:/127.0.0.1:4105] REGISTERED
21:47:45 [DEBUG] [nioEventLoopGroup-3-1] i.n.h.l.LoggingHandler - [id: 0xa41822e8, L:/127.0.0.1:8080 - R:/127.0.0.1:4105] ACTIVE
21:47:45 [DEBUG] [nioEventLoopGroup-3-1] i.n.h.l.LoggingHandler - [id: 0xa41822e8, L:/127.0.0.1:8080 - R:/127.0.0.1:4105] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 61 04 05 06 07 08 09 0a                   |..a.......      |
+--------+-------------------------------------------------+----------------+
21:47:45 [DEBUG] [nioEventLoopGroup-3-1] i.n.h.l.LoggingHandler - [id: 0xa41822e8, L:/127.0.0.1:8080 - R:/127.0.0.1:4105] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 05 61 07 08 09 0a                   |.....a....      |
+--------+-------------------------------------------------+----------------+
21:47:45 [DEBUG] [nioEventLoopGroup-3-1] i.n.h.l.LoggingHandler - [id: 0xa41822e8, L:/127.0.0.1:8080 - R:/127.0.0.1:4105] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 05 61 07 08 09 0a                   |.....a....      |
+--------+-------------------------------------------------+----------------+
21:47:45 [DEBUG] [nioEventLoopGroup-3-1] i.n.h.l.LoggingHandler - [id: 0xa41822e8, L:/127.0.0.1:8080 - R:/127.0.0.1:4105] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 05 06 61 08 09 0a                   |......a...      |
+--------+-------------------------------------------------+----------------+
21:47:45 [DEBUG] [nioEventLoopGroup-3-1] i.n.h.l.LoggingHandler - [id: 0xa41822e8, L:/127.0.0.1:8080 - R:/127.0.0.1:4105] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 61 04 05 06 07 08 09 0a                   |..a.......      |
+--------+-------------------------------------------------+----------------+
21:47:45 [DEBUG] [nioEventLoopGroup-3-1] i.n.h.l.LoggingHandler - [id: 0xa41822e8, L:/127.0.0.1:8080 - R:/127.0.0.1:4105] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 61 03 04 05 06 07 08 09 0a                   |.a........      |
+--------+-------------------------------------------------+----------------+
21:47:45 [DEBUG] [nioEventLoopGroup-3-1] i.n.h.l.LoggingHandler - [id: 0xa41822e8, L:/127.0.0.1:8080 - R:/127.0.0.1:4105] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 61 03 04 05 06 07 08 09 0a                   |.a........      |
+--------+-------------------------------------------------+----------------+
21:47:45 [DEBUG] [nioEventLoopGroup-3-1] i.n.h.l.LoggingHandler - [id: 0xa41822e8, L:/127.0.0.1:8080 - R:/127.0.0.1:4105] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 05 06 07 08 09 61                   |.........a      |
+--------+-------------------------------------------------+----------------+
21:47:45 [DEBUG] [nioEventLoopGroup-3-1] i.n.h.l.LoggingHandler - [id: 0xa41822e8, L:/127.0.0.1:8080 - R:/127.0.0.1:4105] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 61 04 05 06 07 08 09 0a                   |..a.......      |
+--------+-------------------------------------------------+----------------+
21:47:45 [DEBUG] [nioEventLoopGroup-3-1] i.n.h.l.LoggingHandler - [id: 0xa41822e8, L:/127.0.0.1:8080 - R:/127.0.0.1:4105] READ: 10B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 03 04 61 06 07 08 09 0a                   |....a.....      |
+--------+-------------------------------------------------+----------------+
21:47:45 [DEBUG] [nioEventLoopGroup-3-1] i.n.h.l.LoggingHandler - [id: 0xa41822e8, L:/127.0.0.1:8080 - R:/127.0.0.1:4105] READ COMPLETE
     */
}
