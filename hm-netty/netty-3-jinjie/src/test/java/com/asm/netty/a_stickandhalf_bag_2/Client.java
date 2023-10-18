package com.asm.netty.a_stickandhalf_bag_2;

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

/*
 * ################################################
 * ##############   短链接 解决黏包    ##############
 * ################################################
 */
public class Client {
/*
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 */
    static final Logger log = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {

        for (int i = 0; i < 10; i++) {
            send();
        }

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
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        // channel连接建立好之后 出发 channelActive() 时间
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            // super.channelActive(ctx);
                            final ByteBuf buf = ctx.alloc().buffer(16);
                            buf.writeBytes(new byte[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16});
                            ctx.writeAndFlush(buf);
                            /**
                             * 【使 每发完一次消息 就关闭一次 ，这样连接建立到连接断开之间就是消息的边界】
                             */
                            ctx.channel().close();
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


}
