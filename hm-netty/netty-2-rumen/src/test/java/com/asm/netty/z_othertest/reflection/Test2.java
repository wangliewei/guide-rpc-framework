package com.asm.netty.z_othertest.reflection;

import com.asm.netty.z_othertest.model.Employee;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Random;

public class Test2 {

    /**
     * 动态调用 来创建 对象
     */
    @Test
    public void test2() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        int num = new Random().nextInt (3);
        String classPath = "";

        switch (num) {
            case 0:
                classPath = "java.util.Date";
                break;
            case 1:
                classPath = "com.asm.netty.z_othertest.model.Employee";
                break;
            case 2:
                classPath = "com.asm.netty.z_othertest.server.EmployeeData";
                break;
        }
        Class clazz = Class.forName(classPath);
        System.out.println(clazz.newInstance()); // 创建对象

    }

    /**
     * 获取 本类所有属性 [公开和非公开] [和实现的接口]
     */
    @Test
    public void getDeclaredFieldsTest()
    {
        // 1. 实例Class对象
        Class clazz = Employee.class;

        // 2. 获取父类 和 此类的 所有 public 属性
        Field[] fields = clazz.getDeclaredFields();
            /*
            [
              private int com.asm.netty.z_othertest.model.Employee.id,
              private java.lang.String com.asm.netty.z_othertest.model.Employee.name,
              private int com.asm.netty.z_othertest.model.Employee.age,
              private double com.asm.netty.z_othertest.model.Employee.salary
            ]
             */
        System.out.println(Arrays.toString(fields));
        // 3. 遍历 获取修饰符、获取数据类型、获取变量名
        for(Field f : fields)
        {
            System.out.println("字段名：" + f.getName() + "--------------------");
            // 3.1 修饰符
            int modifier = f.getModifiers();
            System.out.println("修饰符：" + modifier + "=" + Modifier.toString(modifier));
            // 3.2 数据类型
            Class<?> type = f.getType();
            System.out.println("数据类型：" + type.getName());
        }

        final Class[] interfaces = clazz.getInterfaces();
        System.out.println(Arrays.toString(interfaces));
    }
}
