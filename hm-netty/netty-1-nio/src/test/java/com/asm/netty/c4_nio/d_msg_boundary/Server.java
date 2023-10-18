package com.asm.netty.c4_nio.d_msg_boundary;

import com.asm.netty.utils.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;


/**
 * 这里主要 处理 容量超出 问题
 *
 * 程序里 '\n' 分隔 处理的缺点是：
 *      首先要知道的是：主程序里 channel.read(buffer)读数据时， 超出buffer分配内存长度 将再次或多次触发读事件 进行循环
 *      所以当发送数据超过 分配内存长度时，第一次读取将会 丢失，因为没有 '\n'
 *
 * 解决思路：
     1.  首先将 channel.read(buffer) 里，这个 buffer 局部变量 改为全局变量 (方便一次性读取 客户端数据)
     2.  在每个客户端SocketChannel 注册Selector时，第三参数 附件里 加入 开辟的内存ByteBuffer buffer (这时的 buffer 的生命周期 将和SelectionKey 一样了)
     3.  然后在 每次读事件 里 attachment获取这个附件参数强转回 ByteBuffer
     4.  子方法compact 结束后，主方法 对比position==limit检查是否超出内存，超出内存说明当前没有读取到将 触发读事件再次循环
     5.  如果超出内存：开辟原内存字节长度 改为两倍，进入下一次循环继续判断，直到满足长度 处理完
 */
@Slf4j
public class Server {

    private static void split(ByteBuffer source)
    {

System.out.println("before split =============================================");

        // 读模式
        source.flip();
//System.out.println("Charset.defaultCharset ===========" + Charset.defaultCharset().decode(source));
        for (int i = 0; i < source.limit(); i++) {

            byte b = source.get(i);
            if (b == '\n')
            {
                int position = source.position();
                int length = i + 1 - position;
System.out.println("                      [ i  = "+ i +" , position = " + position + " length  = " + length + " ]");

                // 完整 消息 存入 新 buffer
                ByteBuffer target = ByteBuffer.allocate(length);

System.out.print("position=[");
                // 从 source 读 ，写入 target
                for (int j = 0; j < length; j++) {

                    target.put(source.get()); // 每一次 get 时， position++
System.out.print(source.position() + ",");
                }
System.out.println("]");
                ByteBufferUtil.debugAll(target);

System.out.println("------------------------------------ 有 \\n 的一个循环 --------------------------------------------------");

            }

        }

        // 未读完部分向前压缩
        source.compact(); // 首次 read 1234567890abcdef 进入时，compact后， position=limit=16
System.out.println("after split =============================================");

    }

    @Test
    public void testServer1() throws IOException {

        // 1. 创建 selector 来管理多个channel
        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();// 创建一个 服务器 对象 通道

        ssc.configureBlocking(false);// selector 必须工作在非阻塞模式下  影响 accept 变成 非阻塞方法

        ssc.bind(new InetSocketAddress(8080));// 绑定一个 监听端口


        // 2. 建立 selector 和 服务器 的连接 (将服务器连接通道 注册 到 selector)
        /**
         * 通过SelectionKey 可以知道事件 和 知道哪个channel通道
         *
         * 第二参数：0 不关注任何事件
         * 第三参数：附件，任意参数，跟每个独立的客户端 channel独有的参数，不会与其他客户端参数错乱
         */
        SelectionKey sscKey = ssc.register(selector, 0, null);
        // 指明 SelectionKey  绑定的事件 selector 才会关心
        sscKey.interestOps(SelectionKey.OP_ACCEPT);

        log.debug("register sscKey: {}", sscKey);

        while(true)
        {
            // 3. 选择器，有未处理或未取消事件，不阻塞 继续运行 ； 否则 阻塞
            selector.select();
System.out.println("before 循环  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

            // 4. 处理事件
            //  selectKeys 内部包含所有发生的事件，譬如两个客户端连上了 会有两个key
            //  如果 要在遍历里删除 必须用迭代器
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while(iter.hasNext())
            {
                SelectionKey key = iter.next();

                // 每次迭代完 要移除掉，不然 可读事件 进来 循环时，先判断肯定是ssc accept事件(accept事件先发生的，先存入集合)，
                // 但是此时没有连接事件(这个事件还是上一次的,事件不会自己删除)，所以在处理时 sc=channel.accept() 是null ，sc进一步处理时 报空指针异常
                iter.remove(); // selectedKeys 里删除

                // 5. 根据 事件类型 处理
                if(key.isAcceptable())     // accept  -客户端连接请求触发 (服务端 事件)
                {

                    // 获取 服务器对象通道
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();// 因为服务器就一个，所以可以直接： ssc.accept()
                    // 获取 读写通道
                    SocketChannel sc = channel.accept(); // 【【【调用accept方法 就意味着把事件 处理掉了，或者 取消  key.cancel();】】】
                    sc.configureBlocking(false); // selector 必须工作在非阻塞模式下



                    // 分配 16字节的一个内存 来 存放接收的数据
                    ByteBuffer buffer = ByteBuffer.allocate(16);

                    // 读写通道 SocketChannel 注册到 selector 上
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);


                }else if (key.isReadable()) // read    -可读事件
                {

                    /**
                     * try 处理 客户端的 read事件(异常关闭触发) 的 read 异常
                     *
                     * 1. 不cancel取消的话，即使remove，下次还 select出来
                     */
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();// SocketChannel才有 读权限

                        // 获取 accept事件里 注册的 内存附件
                        ByteBuffer buffer = (ByteBuffer) key.attachment();


                        // 客户端 正常断开  也会产生一个read事件，但是没数据， 返回的是 -1
                        int read = channel.read(buffer);
                        if(read == -1)
                        {
                            //  取消掉，让  select() 阻塞
                            key.cancel();
                        }else{

                            split(buffer);
                            // 如果内存空间写满了 没有 \n
                            /**
                             * 当发送数据 超出buffer分配内存长度 两次或多次触发读事件循环
                                然后 每次循环 扩容一倍，如果还是 超出buffer分配长度，则继续循环扩容
                             */
                            if(buffer.position() == buffer.limit())
                            {
System.out.println("########## buffer内存长度 ########## = " + buffer.capacity());
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
System.out.println("########## newBuffer内存长度 ########## = " + newBuffer.capacity());
                                buffer.flip(); // 老buffer 切换读模式， 给 newBuffer 复制
                                newBuffer.put(buffer); // 复制
                                // 修改 SelectionKey 的附件参数
                                key.attach(newBuffer);
                            }

                        }


                    } catch (IOException e) {
                        e.printStackTrace();

                        key.cancel();//  取消掉，让  select() 阻塞
                    }


                }

            }
System.out.println("end 循环 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        }
        /*

        */

    }

}
