package com.asm.netty.z_othertest.reflection;

import com.asm.netty.z_othertest.model.Employee;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.util.Properties;
import java.util.Set;

/*
// 创建、 读取资源文件 两个主要方法
  1.    类加载过程：

        编译：程序经过 javac.exe命令后，会生成一个或多个字节码文件(.class结尾)

        加载：接着用 java.exe 对某个字节码文件解释运行==字节码文件加载到内存

                加载到内存中的类本身(Class clazz=Person.class 这里就是Class的一个实例 ) 叫：运行时的类 == 就作为Class的一个实例

Class的一个实例 对应着 一个运行时 类 ()
 */
public class Test1 {

    // 创建
    @Test
    public void test1() throws ClassNotFoundException {

        final Class<Employee> clazz1 = Employee.class;
        System.out.println(clazz1);// : class com.asm.netty.z_othertest.model.Employee

        final Employee obj1 = new Employee(10, "aa");
        final Class<? extends Employee> clazz2 = (Class<Employee>) obj1.getClass();
        System.out.println(clazz2);// : class com.asm.netty.z_othertest.model.Employee

        // 编译不确定 【常用】
        final Class<?> clazz3 = Class.forName("com.asm.netty.z_othertest.model.Employee");
        System.out.println(clazz3);// : class com.asm.netty.z_othertest.model.Employee

        // 使用当前类加载器 加载指定类
        final ClassLoader cl = Test1.class.getClassLoader();
        final Class<?> clazz4 = cl.loadClass("com.asm.netty.z_othertest.model.Employee");
        System.out.println(clazz4);// : class com.asm.netty.z_othertest.model.Employee


    }

    /**
     * Class 所有对象种类
     */
    @Test
    public void test2()
    {
        /**
         * 以下 cn 都为初始化 打印都是 : class java.lang.Class
         */
        Class c1 = Object.class;        //                   class java.lang.Object
        Class c2 = Comparable.class;    // : 接口     类型    interface java.lang.Comparable
        Class c3 = ElementType.class;   // : 枚举     类型    class java.lang.annotation.ElementType
        Class c4 = Override.class;      // : 注解     类型    interface java.lang.Override
        Class c5 = void.class;          // : 返回值   类型    void
        Class c6 = String.class;        // : class java.lang.String
        Class c7 = String[].class;      // : class [Ljava.lang.String;
        Class c8 = int.class;           // : int
        Class c9 = int[].class;         // : class [I
        Class c10 = int[][].class;      // : class [[I

        // 只要元素类型 与 纬度一样，就是同一个CLass
        int[] n1 = new int[10];
        int[] n2 = new int[110];

        System.out.println(n1.getClass()); // : class [I

        System.out.println(n1.getClass() == n2.getClass());// : true
    }

    /**
     * 读取资源文件
     *  ClassLoader 读取的是当前Module/src 下
     */
    @Test
    public void test3() throws IOException {
        // 1. 实例 Properties对象
        Properties pros = new Properties();

        // 2. 操作文件
        // 2.1 使用本类加载器ClassLoader 获取配置文件流
        ClassLoader cl = Test1.class.getClassLoader();
        InputStream is = cl.getResourceAsStream("application.properties");
        // 2.2 加载读取
        pros.load(is);

        final Set<String> strings = pros.stringPropertyNames();// [mySerializer.algorithm, server.port]
        System.out.println(strings.toString());

        // 3. 获取数据 : server.port =6000;	 mySerializer.algorithm =Json
        System.out.println("server.port =" + pros.getProperty("server.port")
                + ";\t mySerializer.algorithm =" + pros.getProperty("mySerializer.algorithm"));
    }

}
