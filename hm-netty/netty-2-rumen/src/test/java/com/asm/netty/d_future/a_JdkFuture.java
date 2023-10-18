package com.asm.netty.d_future;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * Future ：线程间 传递结果的容器
 *
 */
@Slf4j
public class a_JdkFuture {
    /**
     *
     16:43:23 [DEBUG] [pool-1-thread-1] c.a.n.d.a_JdkFuture - 执行计算...
     16:43:23 [DEBUG] [main] c.a.n.d.a_JdkFuture - 等待结果......
     16:43:25 [DEBUG] [main] c.a.n.d.a_JdkFuture - 结果是 : 50
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 1. 创建线程池
        final ExecutorService service = Executors.newFixedThreadPool(2);

        // 2. 提交任务 ，call()执行完毕，结果填充Future里，然后把下面get()阻塞方法 唤醒
        final Future<Integer> future = service.submit(() -> {
            log.debug("执行计算...");
            Thread.sleep(2000);
            return 50;
        });

        // 3. 主线程通过 future 来获取结果
        log.debug("等待结果......");
        final Integer i = future.get(); // 阻塞方法，等待 子线程运行完毕

        log.debug("结果是 : {}", i);
    }


}
