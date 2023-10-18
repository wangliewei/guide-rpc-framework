package com.asm.netty.d_future;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * Promise 继承自 Future
 * Promise 属于主动创建的 结果容器
 */
@Slf4j
public class c_NettyPromise {

    /**
     * 注释掉 1/0
     20:32:59 [DEBUG] [main] c.a.n.d.c_NettyPromise - 等待结果...
     20:32:59 [DEBUG] [Thread-0] c.a.n.d.c_NettyPromise - 开始计算......
     20:33:01 [DEBUG] [main] c.a.n.d.c_NettyPromise - 结果是： 80
     *
     * 开启 1/0
     20:36:38 [DEBUG] [main] c.a.n.d.c_NettyPromise - 等待结果...
     20:36:38 [DEBUG] [Thread-0] c.a.n.d.c_NettyPromise - 开始计算......
     Exception in thread "main" java.util.concurrent.ExecutionException: java.lang.ArithmeticException: / by zero
     at io.netty.util.concurrent.AbstractFuture.get(AbstractFuture.java:41)
     at com.asm.netty.d_future.c_NettyPromise.main(c_NettyPromise.java:55)
     Caused by: java.lang.ArithmeticException: / by zero
     at com.asm.netty.d_future.c_NettyPromise.lambda$main$0(c_NettyPromise.java:37)
     at java.lang.Thread.run(Thread.java:748)

     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 1. 准备 EventLoop 对象
        final EventLoop eventLoop = new NioEventLoopGroup().next();

        // 2. 主动创建 promise，结果容器
        final DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);

        // 3. 任意一线程 执行计算，计算完毕后向 promise 填充结果
        new Thread(()->{

            log.debug("开始计算......");
            try {

                Thread.sleep(2000);

//                int i = 1/0;

                // 设置成功结果
                promise.setSuccess(80);

            } catch (Exception e) {
//                e.printStackTrace();
                // 设置失败结果
                promise.setFailure(e);
            }


        }).start();

        log.debug("等待结果...");

        // 4. 主线程接收 结果
        log.debug("结果是： {}", promise.get()); // get 同步阻塞
    }



}
