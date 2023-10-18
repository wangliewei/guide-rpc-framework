package com.asm.netty.z_othertest.reflection;

import com.asm.netty.z_othertest.model.Student;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Test3 {

    /*
    获取指定 参数类型 构造器
    并调用
     */
    @Test
    public void test1() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        // 1. 创建Class的一个实例
        final Class<Student> clazz = Student.class;

        // 2. 获取指定构造器参数 参数：指定构造器参数列表
        Constructor<Student> cons  = clazz.getDeclaredConstructor(String.class, Integer.class, String.class);
        cons.setAccessible(true);

        // 3. 创建 【运行时类的对象】
        Student p = cons.newInstance("Tom", 1, "汪圩小学");
        System.out.println(p);// : Student{studentID='10086', schoolName='汪圩小学'}
    }

    /*
    获取指定属性 并设置值
     */
    @Test
    public void test2() throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        // 1. 创建Class的一个实例 和 一个 【运行时类的对象】
        Class<Student> clazz = Student.class;
        Student obj = clazz.newInstance();

        // 2. 获取指定属性对象
        Field did = clazz.getDeclaredField("school");
        did.setAccessible(true);

        // 3. 设置值
        did.set(obj, "汪圩小学");
        Object o = did.get(obj);
        System.out.println(o);// : 1008899
    }

    /*
    获取指定方法 并代理调用
     */
    @Test
    public void test3() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        // 1. 创建Classyige 一个实例 和运行时的对象
        Class<Student> clazz = Student.class;

        Student o = clazz.newInstance();

        // 2. 获取指定对象方法
        Method show = clazz.getDeclaredMethod("thisIsStudent", String.class, Integer.class,String.class);
        show.setAccessible(true);
        final Object res = show.invoke(o, "张丽斯", 1996, "六(1)班");
        System.out.println(res);

    }

}
