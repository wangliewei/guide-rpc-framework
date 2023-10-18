package com.asm.netty.z_othertest.reflection;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 ###############################################################################
 ###############################################################################
 #########                                                              ########
 #########                        动态代理 实际案例                       ########
 #########                                                              ########
 ###############################################################################
 ###############################################################################
 *
 * 要想实现动态代理，需要解决的问题？
 * 问题一：如何根据 内存中被代理类，动态的创建一个代理类及其对象。
 * 问题二：当代理类的对象调用方法a， 如何动态的去调用被代理类中的同名方法a。
 */
// 总接口
interface ClothFactory1{
    void produceCloth(Integer m);
}

// 被代理类1
class NikeFactory1 implements ClothFactory1{

    @Override
    public void produceCloth(Integer m)
    {
        System.out.println("Nike品牌服饰 " + m + " 件");
    }
}

// 代理类的 附加方法 【实现AOP 面向切面编程功能】
class UtilFactory1{

    public void prepareWork()
    {
        System.out.println("生产准备工作-----");
    }

    public void finishingWork()
    {
        System.out.println("结束工作------");
    }
}

/**
 * 调用处理 程序 --- 丰富 被代理类方法
 * 1.   绑定 被代理对象
 * 2.   重写 代理类对象调用方法(代理类 丰富 被代理类方法)
 */
class MyInvocationHandler implements InvocationHandler {
    // 被代理 对象
    private Object obj;
    // 绑定 被代理对象
    public MyInvocationHandler(Object obj)
    {
        this.obj = obj;
    }
    /**
     * 代理类对象 调用此方法 ===>>> 进一步调用 被代理类对象方法
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 工具类方法
        UtilFactory1 utilFactory1 = new UtilFactory1();
        utilFactory1.prepareWork();// 【实现AOP 面向切面编程功能】
        /**
         * @param obj 被代理对象
         * @param args 上层形参
         */
        Object res = method.invoke(obj,args);

        utilFactory1.finishingWork();// 【实现AOP 面向切面编程功能】

        return res;
    }
}


class ProxyTest {
    public static void main(String[] args) {

// 一. 创建动态代理类
        final NikeFactory1 obj = new NikeFactory1();
        // 1. 实例 调用处理程序类
        MyInvocationHandler handler = new MyInvocationHandler(obj);

        /**
         * 2. 返回 动态代理类的实例
         * 参数1，类加载器，用来加载 class文件获取类
         * 参数2，被代理类的接口，支持多个
         * 参数3，实现接口InvocationHandler，创建代理对象，写增强的方法
         */
        ClothFactory1 proxy = (ClothFactory1) Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), handler);
// 二. 调用方法
        proxy.produceCloth(20);


    }

}













