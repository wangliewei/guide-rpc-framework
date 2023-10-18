package com.asm.netty.c4_nio.f_multi_thread;

import com.asm.netty.utils.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * 这里 与 MultiThreadServer 类不同
 *  未使用 ConcurrentLinkedQueue 线程安全队列 来 ( 把注册程序 以任务形式 加到 队列 让 子线程 执行 )
 */
@Slf4j
public class TestServer1 {

    @Test
    public void test() throws IOException, InterruptedException {

        // 创建主线程 命名为 boss
        Thread.currentThread().setName("boss");

        final ServerSocketChannel ssc = ServerSocketChannel.open();

        ssc.configureBlocking(false);//  selector必须 工作在 非阻塞模式， 影响acept() 编程非阻塞方法

        // 绑定8080端口
        ssc.bind(new InetSocketAddress(8080));

        // 创建 selector来管理多个channel
        final Selector boss = Selector.open();

        // 将 服务器 通道注册到 selector
        final SelectionKey bossKey = ssc.register(boss, 0, null);
        // 关注连接事件
        bossKey.interestOps(SelectionKey.OP_ACCEPT);

        // 1. 创建固定数量的 worker 并初始化
        final Worker worker = new Worker("worker-0");

        while(true)
        {
            boss.select();

            System.out.println(">>>>>>>>>>>>>>> while beging >>>>>>>>>>>>>>>");

            final Iterator<SelectionKey> iter = boss.selectedKeys().iterator();

            while(iter.hasNext())
            {
                final SelectionKey key = iter.next();
                iter.remove();

                if(key.isAcceptable())
                {
                    // 与客户端  读写通道
                    final SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);

                    log.debug("connected... {}", sc.getRemoteAddress());

                    log.debug("before register... {}", sc.getRemoteAddress());

                    /**
                     * 注意：这里必须先 sc.register关注 read事件，然后 再 静态内部类 里执行 selector.select() 才能顺利完成读取
                     *
                     * select() 先执行的话，会将 register阻塞住
                     *
                     */
                    worker.register(); // 这里 将 开启 多线程 并 和 下面方法 同步执行

//Thread.sleep(1000);// 如果 暂停1秒 将 不会 先 关注 read事件
System.out.println("睡眠后  。。。。");


                    // 关联 worker 里的 选择器 selector
                    // 这里的 客户端读写通道  注册到 内部类worker里的 selector
                    sc.register(worker.selector, SelectionKey.OP_READ, null); // 可以直接访问 内部类 的 私有变量
System.out.println(" sc.register后 。。。。");
                    log.debug("after  register... {}", sc.getRemoteAddress());

                }

            }


            System.out.println("<<<<<<<<<<<<<<< while    end <<<<<<<<<<<<<<<");
        }
/*

>>>>>>>>>>>>>>> while beging >>>>>>>>>>>>>>>  开启 Thread.sleep(1000);
09:42:51 [DEBUG] [boss] c.a.n.c.f.TestServer1 - connected... /127.0.0.1:4233
09:42:51 [DEBUG] [boss] c.a.n.c.f.TestServer1 - before register... /127.0.0.1:4233
09:42:51 [DEBUG] [worker-0] c.a.n.c.f.TestServer1 - worker-select()阻塞中...
睡眠后  。。。。

------------------------------------------------------ 开启 Thread.sleep(1000);  +  内部类 注册 开启  selector.wakeup(); 后
>>>>>>>>>>>>>>> while beging >>>>>>>>>>>>>>>
09:44:42 [DEBUG] [boss] c.a.n.c.f.TestServer1 - connected... /127.0.0.1:5203
09:44:42 [DEBUG] [boss] c.a.n.c.f.TestServer1 - before register... /127.0.0.1:5203
09:44:42 [DEBUG] [worker-0] c.a.n.c.f.TestServer1 - worker-select()阻塞中...
09:44:42 [DEBUG] [worker-0] c.a.n.c.f.TestServer1 - >>>>>>> worker beging ......
09:44:42 [DEBUG] [worker-0] c.a.n.c.f.TestServer1 - <<<<<<< worker   end ......
09:44:42 [DEBUG] [worker-0] c.a.n.c.f.TestServer1 - worker-select()阻塞中...
睡眠后  。。。。

------------------------------------------------------ 关闭 Thread.sleep(1000)  关闭 内部类 注册 selector.wakeup()
>>>>>>>>>>>>>>> while beging >>>>>>>>>>>>>>>
09:53:21 [DEBUG] [boss] c.a.n.c.f.TestServer1 - connected... /127.0.0.1:2276
09:53:21 [DEBUG] [boss] c.a.n.c.f.TestServer1 - before register... /127.0.0.1:2276
睡眠后  。。。。
 sc.register后 。。。。
09:53:21 [DEBUG] [boss] c.a.n.c.f.TestServer1 - after  register... /127.0.0.1:2276
<<<<<<<<<<<<<<< while    end <<<<<<<<<<<<<<<
09:53:21 [DEBUG] [worker-0] c.a.n.c.f.TestServer1 - worker-select()阻塞中...
09:53:21 [DEBUG] [worker-0] c.a.n.c.f.TestServer1 - >>>>>>> worker beging ......
09:53:21 [DEBUG] [worker-0] c.a.n.c.f.TestServer1 - read...... /127.0.0.1:2276
+--------+-------------------- all ------------------------+----------------+
position: [0], limit: [16]
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 30 31 32 33 34 35 36 37 38 39 61 62 63 64 65 66 |0123456789abcdef|
+--------+-------------------------------------------------+----------------+
09:53:21 [DEBUG] [worker-0] c.a.n.c.f.TestServer1 - <<<<<<< worker   end ......
09:53:21 [DEBUG] [worker-0] c.a.n.c.f.TestServer1 - worker-select()阻塞中...

*/
    }

    /**
     * worker 的内部类
     * 有独立的线程 和 自己的 selector
     */
    static class Worker implements Runnable{

        private Thread thread;
        private Selector  selector;
        private String name; // 每个 selector的名字

        /**
         * volatile 声明变量的值可能随时会别的线程修改
         *          修饰的变量会强制 将修改的值立即写入主存
         *          主存中值的更新会使缓存中的值失效
         *                  (
         *                  非volatile变量不具备这样的特性，非volatile变量的值会被缓存，
         *                  线程A更新了这个值，线程B读取这个变量的值时可能读到的并不是是线程A更新后的值
         *                  )
         *  volatile具有可见性、有序性，不具备原子性
         */
        private volatile boolean start = false; // 还未初始化


        public Worker(String name)
        {
            this.name = name;
        }

        /**
         * 初始化 线程 和 selector
         * 期望 一个 worker 使用一个线程，创建了 Thread就不再重复创建了
         */
        public void register() throws IOException
        {
            // 保证 这段代码 只执行一遍
            if(!start)
            {
                thread = new Thread(this, name);
                // 先创建 selector,然后再start线程。不然selector会报空指针
                selector = Selector.open();
                thread.start();
                start = true;

            }

            //selector.wakeup(); // 唤醒 select方法

        }

        @Override
        public void run() {
            while(true)
            {
                log.debug("worker-select()阻塞中...");
                try{
                    selector.select();// 有未处理或未取消事件，不阻塞 继续运行 ； 否则 阻塞
                    log.debug(">>>>>>> worker beging ......");

                    final Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

                    while(iter.hasNext())
                    {
                        final SelectionKey key = iter.next();
                        iter.remove();

                        if(key.isReadable())
                        {
                            final ByteBuffer buffer = ByteBuffer.allocate(16);
                            final SocketChannel channel = (SocketChannel) key.channel();

                            log.debug("read...... {}", channel.getRemoteAddress());

                            channel.read(buffer);

                            buffer.flip();
                            ByteBufferUtil.debugAll(buffer);


                        }
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
                log.debug("<<<<<<< worker   end ......");
            }

        }
    }



}
