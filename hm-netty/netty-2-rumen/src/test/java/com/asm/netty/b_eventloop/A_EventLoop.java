package com.asm.netty.b_eventloop;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
/**
 * EventLoop 事件循环组 几个方法
 *  next()
 *  next().submit()
 *  next().scheduleAtFixedRate()
 *
 */
public class A_EventLoop {

    @Test
    public void test1()
    {
        final int i = NettyRuntime.availableProcessors();
        System.out.println(i);
    }


    public static void main(String[] args) {

        // 1. 创建事件循环组 未指定的话，则默认读取系统配置io.netty.eventLoopThreads，没有则根据cpu核心数*2创建线程
        // Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));
        EventLoopGroup group1 = new NioEventLoopGroup(2); // 能处理 ： io 事件，普通任务，定时任务
        //EventLoopGroup group2 = new DefaultEventLoopGroup(); // 普通任务， 定时任务

        // 2. 获取下一个 循环对象
        System.out.println(group1.next());
        System.out.println(group1.next());
        System.out.println(group1.next());
        System.out.println(group1.next());
        System.out.println(group1.next());
        /*
            io.netty.channel.nio.NioEventLoop@6d00a15d
            io.netty.channel.nio.NioEventLoop@51efea79
            io.netty.channel.nio.NioEventLoop@6d00a15d
            io.netty.channel.nio.NioEventLoop@51efea79
            io.netty.channel.nio.NioEventLoop@6d00a15d
        */


        // 3. 普通任务 交给EventLoop 执行
        //   提交到 事件循环组 中，让 某一个事件循环对象 去执行
        //   作用： 譬如 某件耗时的工作 交给子线程 完成
        group1.next().submit(()->{  // 也可以 ...execute(()->...
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("1️⃣子线程运行。。。");
        });

        log.debug("2️⃣主线程 运行。。。");

        // 4. 定时任务  初始延时事件0，间隔时间1， 时间单位 TimeUnit.SECONDS
        group1.next().scheduleAtFixedRate(()->{log.debug("3️⃣子线程运行......");}, 0, 3, TimeUnit.SECONDS);
    }

}
