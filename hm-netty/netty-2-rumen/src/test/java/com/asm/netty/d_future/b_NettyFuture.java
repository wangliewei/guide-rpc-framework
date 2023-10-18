package com.asm.netty.d_future;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Future 里【 同步 和 异步 】获取结果
 * Future ：线程间 传递结果的容器
 * nettyFuture 继承自 jdk的Future
 */
@Slf4j
public class b_NettyFuture {

    /**
     *  1. 同步获取结果
     18:45:05 [DEBUG] [main] c.a.n.d.b_NettyFuture - 等待结果......
     18:45:05 [DEBUG] [nioEventLoopGroup-2-1] c.a.n.d.b_NettyFuture - 执行计算...
     18:45:07 [DEBUG] [main] c.a.n.d.b_NettyFuture - 结果是 : 666


     *  2. 异步获取结果
     18:52:56 [DEBUG] [main] c.a.n.d.b_NettyFuture - 等待结果......
     18:52:56 [DEBUG] [nioEventLoopGroup-2-1] c.a.n.d.b_NettyFuture - 执行计算...
     18:52:58 [DEBUG] [nioEventLoopGroup-2-1] c.a.n.d.b_NettyFuture - 结果是 : 666

     */
    public static void main(String[] args) {


        /**
         * 1. EventLoopGroup 里包含 多个EventLoop
         *    一个 EventLoop里面包含一个线程
         */
        final NioEventLoopGroup group = new NioEventLoopGroup();

        final EventLoop eventLoop = group.next();

        final Future<Integer> future = eventLoop.submit(() -> {
            log.debug("执行计算...");
            Thread.sleep(2000);

            return 666;
        });

        // 3. 主线程通过 future 来获取结果
        log.debug("等待结果......");

        // >>>>>>>>>>>> 1. 同步获取结果
//        final Integer i = future.get(); // 阻塞方法，等待 子线程运行完毕
//        log.debug("结果是 : {}", i);

        // ============ 2. 异步获取结果
        // 注意这里不能用 ChannelFutureListener，因为上面 NioEventLoopGroup 不属于channel的
        future.addListener(future1 -> {
            final Integer i = (Integer) future1.getNow(); // getNow非阻塞方法； get()阻塞方法也可以（因为当前回调方法执行了，说明子线程已经完成了）
            log.debug("结果就是 : {}", i);
        });
        log.debug("bottom......");
        // <<<<<<<<<<<<<
    }

}
