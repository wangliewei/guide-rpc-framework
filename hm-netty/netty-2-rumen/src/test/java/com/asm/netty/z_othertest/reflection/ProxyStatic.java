package com.asm.netty.z_othertest.reflection;

/**
 * 静态代理类 测试
 *  特点：代理和被代理类 在编译期间就被确定下来了
 *
 *  代理工厂 和 原始对象 都实现 同一个接口
 *  代理工厂里 有 被代理类作为属性，然后实现方法对这个属性包装
 */

// 总接口
interface ClothFactory{
    void produceCloth();
}

// 代理类
class ProxyClothFactory implements ClothFactory{

    // 被代理类 作为属性
    private ClothFactory factory;

    // 构造方法
    public ProxyClothFactory(ClothFactory factory) {
        this.factory = factory;
    }

    @Override
    public void produceCloth() {
        System.out.println("代理工厂准备");
        factory.produceCloth();
        System.out.println("代理工厂收尾工作");
    }
}

// 被代理类1
class NikeFactory implements ClothFactory{

    @Override
    public void produceCloth() {
        System.out.println("Nike品牌鞋子");
    }
}
// 被代理类2
class AntaFactory implements ClothFactory{

    @Override
    public void produceCloth() {
        System.out.println("安踏品牌鞋子");
    }
}


// 测试 调用
class StaticProxy {

    public static void main(String[] args) {
        // 1. 实例代理类 被代理类为属性
        ClothFactory proxyNike = new ProxyClothFactory(new NikeFactory());

        // 2. 调用代理类方法 => 被代理类执行核心方法
        proxyNike.produceCloth();

        // 1. 实例代理类 被代理类为属性
        ClothFactory proxyAnta = new ProxyClothFactory(new AntaFactory());

        // 2. 调用代理类方法 => 被代理类执行核心方法
        proxyAnta.produceCloth();
    }
}
