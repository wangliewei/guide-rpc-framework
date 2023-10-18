package com.asm.netty.g_exercise;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ClientAshton {

    // 0001 初始化
    static final byte[] initByte = {(byte)0x7e, (byte)0x00, (byte)0xca, (byte)0x00, (byte)0x01, (byte)0x30, (byte)0x30, (byte)0x32, (byte)0x30, (byte)0x32,
            (byte)0x30, (byte)0x32, (byte)0x31, (byte)0x31, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x36, (byte)0x30, (byte)0x30,
            (byte)0x34, (byte)0x2e, (byte)0x32, (byte)0x35, (byte)0x30, (byte)0x31, (byte)0x3a, (byte)0xe5, (byte)0xb7, (byte)0xa5, (byte)0xe4, (byte)0xbd,
            (byte)0x9c, (byte)0xe4, (byte)0xbd, (byte)0x8d, (byte)0xe7, (byte)0xb1, (byte)0xbb, (byte)0xe5, (byte)0x88, (byte)0xab, (byte)0x3b, (byte)0x30,
            (byte)0x32, (byte)0x3a, (byte)0xe7, (byte)0x94, (byte)0xb5, (byte)0xe6, (byte)0x9c, (byte)0xba, (byte)0xe4, (byte)0xbd, (byte)0x8d, (byte)0xe6,
            (byte)0x8e, (byte)0xa7, (byte)0xe5, (byte)0x88, (byte)0xb6, (byte)0xe7, (byte)0xb1, (byte)0xbb, (byte)0xe5, (byte)0x88, (byte)0xab, (byte)0x3b,
            (byte)0x30, (byte)0x33, (byte)0x3a, (byte)0xe9, (byte)0x97, (byte)0xb8, (byte)0xe9, (byte)0x81, (byte)0x93, (byte)0xe4, (byte)0xbd, (byte)0x8d,
            (byte)0xe6, (byte)0x8e, (byte)0xa7, (byte)0xe5, (byte)0x88, (byte)0xb6, (byte)0xe7, (byte)0xb1, (byte)0xbb, (byte)0xe5, (byte)0x88, (byte)0xab,
            (byte)0x3b, (byte)0x30, (byte)0x34, (byte)0x3a, (byte)0xe6, (byte)0x95, (byte)0xb0, (byte)0xe6, (byte)0x8d, (byte)0xae, (byte)0xe9, (byte)0x87,
            (byte)0x87, (byte)0xe9, (byte)0x9b, (byte)0x86, (byte)0x49, (byte)0x4f, (byte)0xe7, (byte)0xb1, (byte)0xbb, (byte)0xe5, (byte)0x88, (byte)0xab,
            (byte)0x3b, (byte)0x30, (byte)0x35, (byte)0x3a, (byte)0xe5, (byte)0xb7, (byte)0xa5, (byte)0xe4, (byte)0xbd, (byte)0x9c, (byte)0xe4, (byte)0xbd,
            (byte)0x8d, (byte)0xe8, (byte)0x87, (byte)0xaa, (byte)0xe6, (byte)0xa3, (byte)0x80, (byte)0xe7, (byte)0xb1, (byte)0xbb, (byte)0xe5, (byte)0x88,
            (byte)0xab, (byte)0x3b, (byte)0x30, (byte)0x36, (byte)0x3a, (byte)0xe5, (byte)0xb7, (byte)0xa5, (byte)0xe4, (byte)0xbd, (byte)0x9c, (byte)0xe4,
            (byte)0xbd, (byte)0x8d, (byte)0x42, (byte)0x79, (byte)0x70, (byte)0x61, (byte)0x73, (byte)0x73, (byte)0xe7, (byte)0xb1, (byte)0xbb, (byte)0xe5,
            (byte)0x88, (byte)0xab, (byte)0x3b, (byte)0x30, (byte)0x37, (byte)0x3a, (byte)0x55, (byte)0x56, (byte)0xe8, (byte)0xae, (byte)0xbe, (byte)0xe5,
            (byte)0xa4, (byte)0x87, (byte)0xe7, (byte)0xb1, (byte)0xbb, (byte)0xe5, (byte)0x88, (byte)0xab, (byte)0x3b, (byte)0x30, (byte)0x46, (byte)0x3a,
            (byte)0xe6, (byte)0x9c, (byte)0xaa, (byte)0xe7, (byte)0x9f, (byte)0xa5, (byte)0xe7, (byte)0xb1, (byte)0xbb, (byte)0xe5, (byte)0x88, (byte)0xab,
            (byte)0x3b, (byte)0xf9, (byte)0x9a, (byte)0x7e};
    // 0002 注册
    static final byte[] regByte = {(byte)0x7e, (byte)0x00, (byte)0x14, (byte)0x00, (byte)0x02, (byte)0x30, (byte)0x30, (byte)0x32, (byte)0x30, (byte)0x32,
            (byte)0x30, (byte)0x32, (byte)0x31, (byte)0x31, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x36, (byte)0x30, (byte)0xcd,
            (byte)0xfb, (byte)0x7e};
    // 0000 心跳
    static final byte[] pingPangByte = {(byte)0x7e, (byte)0x00, (byte)0x3d, (byte)0x00, (byte)0x00, (byte)0x30, (byte)0x30, (byte)0x32, (byte)0x30, (byte)0x32,
            (byte)0x30, (byte)0x32, (byte)0x31, (byte)0x31, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x36,
            (byte)0x30, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x08, (byte)0x00, (byte)0x01, (byte)0x08,
            (byte)0x00, (byte)0x00, (byte)0x96, (byte)0xa1, (byte)0x7e};
    // 0010 LOG信息
//    static final byte[] logByte = {(byte)0x7e, (byte)0x00, (byte)0x26, (byte)0x00, (byte)0x10, (byte)0x30, (byte)0x30, (byte)0x32, (byte)0x30, (byte)0x32, (byte)0x30, (byte)0x32, (byte)0x31, (byte)0x31, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x36, (byte)0x30, (byte)0x03, (byte)0xc1, (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x0c, (byte)0x00, (byte)0x00, (byte)0x62, (byte)0x43, (byte)0x0e, (byte)0x2d, (byte)0x32, (byte)0x3e, (byte)0x33, (byte)0x33, (byte)0xaf, (byte)0x41, (byte)0xab, (byte)0xdc, (byte)0x7e};
    static final byte[] logByte = {(byte)0x7e, (byte)0x00, (byte)0x26, (byte)0x00, (byte)0x10, (byte)0x30, (byte)0x30, (byte)0x32, (byte)0x30, (byte)0x32, (byte)0x30, (byte)0x32, (byte)0x31, (byte)0x31, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x36, (byte)0x30, (byte)0x00, (byte)0x13, (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x0c, (byte)0x9a, (byte)0x99, (byte)0x66, (byte)0x43, (byte)0xa0, (byte)0x1a, (byte)0x2f, (byte)0x3e, (byte)0x33, (byte)0x33, (byte)0xa7, (byte)0x41, (byte)0xb4, (byte)0x7d, (byte)0x5d, (byte)0x7e};

    public static void main(String[] args) throws InterruptedException {

        final NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);

        final Channel channel = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {

                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(LOGGING_HANDLER);
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                super.channelRead(ctx, msg);
                                ByteBuf buffer = (ByteBuf) msg;
                                log.debug(buffer.toString(Charset.defaultCharset()));

                            }
                        });

                    }
                })
//                .connect("666", 6001).sync().channel();
                .connect("localhost", 6000).sync().channel();  //http://666:6000/

        /*
        channel.writeAndFlush("你好3");

        初始化 : initByte
        注册 : regByte
        心跳 : pingPangByte
        LOG日志 : logByte

        final ByteBuf buf9 = ByteBufAllocator.DEFAULT.buffer();
        buf9.writeBytes(  initByte  );
        channel1.writeAndFlush(buf9);

        */
        AtomicBoolean b = new AtomicBoolean(true);

        new Thread(() -> {


            Scanner scanner = new Scanner(System.in);
            while(true){
                final ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();

                final String line = scanner.nextLine();
                // 方便调试，先初始化 注册 再下一步操作
                if(line.equals("initByte")){

                    buf.writeBytes(  initByte  );
                    channel.writeAndFlush(buf);
                    log.debug("---------- initByte -------------------");

                }else if(line.equals("regByte")){
                    buf.writeBytes(  regByte  );
                    channel.writeAndFlush(buf);
                    log.debug("---------- regByte -------------------");

                }else if(line.equals("logByte")){
                    buf.writeBytes(  logByte  );
                    channel.writeAndFlush(buf);
                    log.debug("---------- logByte -------------------");

                }else{
                    log.debug("---------- 非法字符串 -------------------{}", line);
                }

            }

        }).start();
        // closeFuture() 异步等待 channel关闭才执行    0020202110000060
        channel.closeFuture().addListener(future -> {
            group.shutdownGracefully();
        });

    }

    /*
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7e 00 26 00 10 30 30 32 30 32 30 32 31 31 30 30 |~.&..00202021100|
|00000010| 30 30 30 36 30 00 13 00 02 00 0c 9a 99 66 43 a0 |00060........fC.|
|00000020| 1a 2f 3e 33 33 a7 41 b4 7d 5d 7e                |./>33.A.}]~     |
+--------+-------------------------------------------------+----------------+
  */
}

















