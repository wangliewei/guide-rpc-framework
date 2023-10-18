package com.asm.netty.z_othertest.threadrunable_test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 * 多线程创建
 * 方式一：
 *  1.继承Thread子类
 *  2.重写父类run()
 *  3.子类对象调用父类start()
 *  注意：已经开始的线程不可以 重复执行， 可以重新创建一个子类对象调用
 *
 *  Thread 常用方法
 *  1. start(): 启动当前线程 调用当前线程run()
 *  2. run(): 重写的方法，将要创建线程执行代码写入
 *  3. currentThread(): 静态方法，返回当前代码的线程
 *  4. getName()、setName(): 获取、设置当前线程名字
 *  5. yield(): 释放当前cpu执行权，部分没效果 是因为cpu又分到当前线程了
 *  6. join(): A线程里 B调用join()时候；A进入阻塞状态，B线程执行完毕，才结束【阻塞】
 *  7. stop(已过时 不建议使用): 停止线程
 *  8. sleep(): 线程【阻塞】 xx 毫秒结束
 *  9. isAlive(): 判断当前线程是否存活
 */
class MyThread extends Thread{
    public void run()
    {
        int num = 100;
        for (int i = 0; i < num; i++) {
            if(i%2 == 0)
            {
                log.debug(Thread.currentThread().getName() + ":" +  i);
            }

            if(i == 20)
            {
//                yield();
            }

            if(i % 5 == 0)
            {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public MyThread(String name)
    {
        super(name);
    }
}

@Slf4j
class ThreadTest {
    public static void main(String[] args) {
        // 1 主线程 创建对象
        MyThread t1 = new MyThread("构造线程一");
        //t1.setName("线程一");

        // 2 开始 分线程执行 t1.start()
        /**
         *  2.1 启动 当前线程
         *  2.2 调用 当前线程 的 run()
         */
        t1.start();
        // 当前线程重命名
        // Thread.currentThread().setName("主线程");
        // 3 主线程 执行
        method(t1);
    }
    public static void method(MyThread obj)
    {
        int num = 100;
        for (int i = 0; i < num; i++) {
            if(i%2 == 0)
            {
                log.debug(Thread.currentThread().getName() + ":" +i);
            }
            /*if(i == 20)
            {
                try {
                    obj.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/
        } } }
