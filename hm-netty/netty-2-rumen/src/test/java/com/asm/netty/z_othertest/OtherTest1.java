package com.asm.netty.z_othertest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OtherTest1 {

    @Test
    public void test1()
    {
        String receiveStr1 = "023030393430303030323032303231313030303034303230303130303034363230303046464646464646464646464646464646303030303030303130303030303030303030303030303030303030383030303030303030303030383030303030303030";
//        String receiveStr2 = "3032333033303339333433303330333033303332333033323330333233313331333033303330333033343330333233303330333133303330333033343336333233303330333034363436343634363436343634363436343634363436343634363436343634363330333033303330333033303330333133303330333033303330333033303330333033303330333033303330333033303330333033303338333033303330333033303330333033303330333033303338333033303330333033303330333033303033";
        final String substring = receiveStr1.substring(receiveStr1.length() - 2, receiveStr1.length());

        if ( substring.equals("03")) {
            System.out.println("1");

        }else{

            System.out.println("0");
        }
    }

    @Test
    public void test2()
    {
//        String length = String.format("%06d", 30); // 000030
        System.out.printf("100的一半是：%06d %n", 100/2);
    }

    @Test
    public void testMapCompute()
    {
        //进行使用compute属性进行求解字符串出现的词的频率
        String str1 = "hello java, i am vary happy! nice to meet you";

        // jdk1.8的写法
        HashMap<Character, Integer> result2 = new HashMap<>(45);
        for (int i = 0; i < str1.length(); i++) {
            char curChar = str1.charAt(i);

            //compute是返回最新的值
            result2.compute(curChar, (k, v) -> {
                System.out.println(curChar + "\t : \t" + "k=" + k + " \t v=" + v);
                if (v == null) {
                    v = 1;
                } else {
                    v += 1;
                }
                return v;
            });
        }
        System.out.println(result2);
    }

    @Test
    public void testMapCompute2()
    {
        //创建一个 HashMap
        HashMap<String, Integer> prices = new HashMap<>();

        // 往HashMap中添加映射项
        prices.put("Shoes", 200);
        prices.put("Bag", 300);
        prices.put("Pant", 150);
        System.out.println("HashMap: " + prices);

        // 重新计算鞋子打了10%折扣后的值
        int newPrice = prices.compute("Shoes", (key, value) -> value - value * 10/100);
        System.out.println("Discounted Price of Shoes: " + newPrice);

        // 输出更新后的HashMap
        System.out.println("Updated HashMap: " + prices);
    }

    @Test
    public void beginZero()
    {
        LocalDateTime today_start = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);//当天零点
        String st = String.valueOf(today_start.toEpochSecond(ZoneOffset.of("+8")));
        System.out.println(st);
    }
    @Test
    public void endStamp()
    {
        LocalDateTime today_start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);//当天零点
        String st = String.valueOf(today_start.toEpochSecond(ZoneOffset.of("+8")));
        System.out.println(st);
    }

    static final byte[] regByte = {(byte)0x7e, (byte)0x00, (byte)0x14, (byte)0x00, (byte)0x02, (byte)0x30, (byte)0x30, (byte)0x32, (byte)0x30, (byte)0x32,
            (byte)0x30, (byte)0x32, (byte)0x31, (byte)0x31, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x36, (byte)0x30, (byte)0xcd,
            (byte)0xfb, (byte)0x7e};

    @Test
    public void test3()
    {
        /*###############################*/
        /*###       当成 服务器端       ###*/
        /*###############################*/
        final EmbeddedChannel ch = new EmbeddedChannel(
                // 注意：解码器 放debug上面
                new LoggingHandler(LogLevel.DEBUG)
        );

        final ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        final ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();


        final int len = regByte.length;
        System.out.println(len);

        buf.writeBytes(regByte);

        buf2.writeBytes(new byte[]{0x7a});
        buf2.writeBytes(regByte);
        buf2.writeBytes(new byte[]{0x7a});

        System.out.println(buf2);
        // 将消息 写入 Channel
        ch.writeInbound(buf2);
    }

    @Test
    public void test4()
    {
        final HashMap<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);

        final Integer c = map.putIfAbsent("b", 3); // 添加成功返回 null
        System.out.println(c);
        System.out.println(map.toString());
    }

}
