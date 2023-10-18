package com.asm.netty.z_othertest.threadrunable_test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 * 创建多线程方式二：
 * 1. 创建类RunTest要实现Runnable接口
 * 2. 实现抽象方法run()
 * 3. 创建RunTest对象
 * 4. 第3步的对象 传递到Thread类构造器中来创建Thread对象
 * 5. 第4步的对象 调用start()
 *
 */
public class RunnableTest{
    public static void main(String[] args) {

        RunTest r1 = new RunTest();

        Thread t1 = new Thread(r1,"窗口1---");
        t1.start();

        Thread t2 = new Thread(r1,"窗口2***");
        t2.start();
    }
}

@Slf4j
class RunTest implements Runnable{

    static int ticket = 100;

    @Override
    public void run() {

        while(true){

// >>>>>>>>>>>>>>>>>>>>>>>>>>
/*            synchronized (this){

                if(ticket > 0){

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    log.debug(Thread.currentThread().getName() + " : " + ticket);
                    ticket--;

                }else{

                    break;
                }
            }*/
// ==========================
            show();
            if(ticket <= 0)break;
// <<<<<<<<<<<<<<<<<<<<<<<<<<
        }
    }
    private synchronized void show(){
        if(ticket > 0){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug(Thread.currentThread().getName() + " : " + ticket);
            ticket--;
        }
    }
}
