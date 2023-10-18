package com.asm.netty.c4_nio.f_multi_thread;

import com.asm.netty.utils.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 第三部分： 开多个 worker线程 使用 "线程安全队列"，进行分离 测试
 *
 * 这里是 MultiThreadServer 的 升级版 ， 多 worker  使用 数组保存多 内部类对象
 *
 */
@Slf4j
public class MultiThreadServerMultiWorker {

    @Test
    public void test() throws IOException {

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


        /**
           1. 创建固定数量的 worker 并初始化
            数组的线程数 至少设置为 CPU 的核心数
            得到 CPU核心数 ： Runtime.getRuntime().availableProcessors() 【注意拿的不是容器申请的核心数，而是物理的CPU核心数，直到 JDK10才修复】

            建议 手工指定下 更好
            根据实际情况，如果是 CPU 密集型运算, 线程数 设为 CPU核心数
            如果 IO频繁， CPU用的少 ，参考 阿姆达尔定律 ，根据 IO跟 计算的比例 来确认 多少线程 ，一般是 大于 CPU核心的
         *
         */
        final Worker[] workers = new Worker[2];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-" + i);
        }

        // 计数器
        final AtomicInteger index = new AtomicInteger(); // 初始值 是 0


        while(true)
        {
            boss.select();

            System.out.println(">>>>>>>>>>>>>>> while beging >>>>>>>>>>>>>>>");

            final Iterator<SelectionKey> iter = boss.selectedKeys().iterator();

            while(iter.hasNext())
            {
                final SelectionKey key = iter.next();
                iter.remove();

                // 连接事件 后 的 操作 内容
                if(key.isAcceptable())
                {
                    // 与客户端  读写通道
                    final SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);

                    log.debug("connected... {}", sc.getRemoteAddress());

                    log.debug("before register... {}", sc.getRemoteAddress());

                    /**
                     * 注意：这里必须先 sc.register关注 OP_READ 事件，然后 再 静态内部类 里执行 selector.select() 才能顺利完成读取
                     *
                     * select() 先执行的话，会将 register阻塞住
                     */
                    /**
                     * 使用 round robin 轮流交替使用， 负载均衡的 方法
                     *  index.getAndIncrement 获取索引 并 自增一次
                     *
                     * 0 % 2 = 0
                     * 1 % 2 = 1
                     * 2 % 2 = 0
                     * 3 % 2 = 1
                     * 4 % 2 = 0
                     */
                    workers[index.getAndIncrement() % workers.length] . register(sc);

                    /**
                     * 这个程序已经让 ，worker内部类 执行了
                     * 如果先执行 worker的 select()将先阻塞住了
                     * 有事件发生了 将执行队列里的 程序代码
                     */
                    // 关联 worker 里的 选择器 selector
                    // 这里的 客户端读写通道  注册到 内部类worker里的 selector
                    // sc.register(worker.selector, SelectionKey.OP_READ, null); // 可以直接访问 内部类 的 私有变量

                    log.debug("after  register... {}", sc.getRemoteAddress());

                }

            }


            System.out.println("<<<<<<<<<<<<<<< while    end <<<<<<<<<<<<<<<");
        }
        /*

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
         * 创建 一个 线程安全的队列 ，用来 让两个线程之间传输数据
         *  目的：就是把 要注册的 channel传过来
         * < 任务对象 >
         */
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();


        /**
         * volatile 声明变量的值可能随时会别的线程修改
         *          修饰的变量会强制 将修改的值立即写入主存
         *          主存中值的更新会使缓存中的值失效
         *                  (
         *                  非volatile变量不具备这样的特性，非volatile变量的值会被缓存，
         *                  线程A更新了这个值，线程B读取这个变量的值时可能读到的并不是是线程A更新后的值
         *                  )
         *  volatile具有可见性、有序性，不具备原子性
         *
         *
         *
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
        public void register(SocketChannel sc) throws IOException
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
            // boss 线程 里 ： 向 队列里 添加任务 ，但是并没有执行
            // 任务内容：主线程的 读写通道 注册到 子线程的 selector，交给子线程管理
            queue.add(()->{
                try {
                    sc.register(selector, SelectionKey.OP_READ, null);
                    System.out.println("*** 子线程 里 注册 读写通道 到 selector 并关注 读事件 ***");
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });

            selector.wakeup(); // 唤醒 select方法 为了执行 关注 read事件


        }

        /**
         * 这 先 select()   【因为在boss线程里面已经 强行wakeup() 了 所以 首次 不阻塞】
         * 再 register() 【先注册 关注了read事件，首次 有事件将继续，没事件将到下个循环   】
         */
        @Override
        public void run() {
            while(true)
            {
                log.debug("worker-select()阻塞中...");
                try{
                    selector.select();// 有未处理或未取消事件，不阻塞 继续运行 ； 否则 阻塞
                    log.debug(">>>>>>> worker beging ......");
                    /**
                     * 从 队列里拿出 任务程序 进行执行
                     */
                    final Runnable task = queue.poll();
                    if(task != null)
                    {
                        task.run(); // worker-0 县城里执行了 ： sc.register(selector, SelectionKey.OP_READ, null);
                    }


                    /**
                     * 首次 到这里 可能还没 任务事件，但是 读事件 已经注册好了(就可以 关注 客户端是否传数据了)
                     */
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
//                            ByteBufferUtil.debugAll(buffer);
                            final String string = Charset.defaultCharset().decode(buffer).toString();
                            System.out.println("读取内容： " + string);

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
