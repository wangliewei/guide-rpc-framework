package com.asm.netty.a_stickandhalf_bag_3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Random;

/*
 * ################################################
 * ##########   定长解码器 解决黏包    ##############
 * ################################################
 */
public class Client {
/*
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 */
    static final Logger log = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {

        send();
        System.out.println("send......finish......");

    }

    private static void send() {
        final NioEventLoopGroup worker = new NioEventLoopGroup();

        try{
            final Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(worker);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        // channel连接建立好之后 出发 channelActive() 时间
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            log.debug("sending...");
                            final Random r = new Random();
                            final ByteBuf buf = ctx.alloc().buffer();


                            for (int i = 0; i < 10; i++) {
                                final int idx = r.nextInt(10) + i*10;
                                buf.writeBytes(new byte[]{1,2,3,4,5,6,7,8,9,10});
                                buf.setByte(idx,'a');
                            }

                            ctx.writeAndFlush(buf);
                        }
                    });
                }
            });

            final ChannelFuture channelFuture = bootstrap.connect("localhost", 8080).sync();
            channelFuture.channel().closeFuture().sync();


        } catch (InterruptedException e)
        {
            log.error("Client error", e);
        } finally {
            worker.shutdownGracefully();
        }
    }
    /*
    Connected to the target VM, address: '127.0.0.1:14808', transport: 'socket'
21:47:45 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0xdebb6a4a] REGISTERED
21:47:45 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0xdebb6a4a] CONNECT: localhost/127.0.0.1:8080
21:47:45 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0xdebb6a4a, L:/127.0.0.1:4105 - R:localhost/127.0.0.1:8080] ACTIVE
21:47:45 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0xdebb6a4a, L:/127.0.0.1:4105 - R:localhost/127.0.0.1:8080] WRITE: 100B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 02 61 04 05 06 07 08 09 0a 01 02 03 04 05 61 |..a............a|
|00000010| 07 08 09 0a 01 02 03 04 05 61 07 08 09 0a 01 02 |.........a......|
|00000020| 03 04 05 06 61 08 09 0a 01 02 61 04 05 06 07 08 |....a.....a.....|
|00000030| 09 0a 01 61 03 04 05 06 07 08 09 0a 01 61 03 04 |...a.........a..|
|00000040| 05 06 07 08 09 0a 01 02 03 04 05 06 07 08 09 61 |...............a|
|00000050| 01 02 61 04 05 06 07 08 09 0a 01 02 03 04 61 06 |..a...........a.|
|00000060| 07 08 09 0a                                     |....            |
+--------+-------------------------------------------------+----------------+
21:47:45 [DEBUG] [nioEventLoopGroup-2-1] i.n.h.l.LoggingHandler - [id: 0xdebb6a4a, L:/127.0.0.1:4105 - R:localhost/127.0.0.1:8080] FLUSH
     */


}
