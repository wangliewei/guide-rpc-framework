package com.asm.netty.z_othertest;

import com.asm.netty.z_othertest.model.Employee;
import com.asm.netty.z_othertest.server.EmployeeData;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamTest {

    /*
    ##################################################################
    #######################    创建Stream     ########################
    ##################################################################
     */
    /**
     * 创建Stream方式一：通过集合
     */
    @Test
    public void createTest1()
    {
        List<Employee> emp1 = EmployeeData.getEmployees();
        // 返回一个顺序流
        Stream<Employee> stream1 = emp1.stream();
        // 返回一个并行流
        Stream<Employee> stream2 = emp1.parallelStream();
    }
    /**
     * 创建Stream方式二：通过数组
     */
    @Test
    public void createTest2()
    {
        // 基本数据类型
        int[] intArr = new int[]{1,2,3,4,5,6};
        IntStream intStream = Arrays.stream(intArr);
        // 自定义类
        Employee[] empArr = new Employee[]{
                new Employee(1,"Bob"),
                new Employee(2,"Tom"),
        };
        Stream<Employee> empStream = Arrays.stream(empArr);
    }

    /**
     * 创建Stream方式三：通过of()
     */
    @Test
    public void createTest3()
    {
        Stream<Employee> emp = Stream.of(new Employee(1, "Bob"), new Employee(2, "Tom"));
    }

    /**
     * 创建Stream方式四：创建无限流
     */
    @Test
    public void createTest4()
    {
        // 迭代   遍历偶数
        Stream.iterate(0,t->t+2).limit(3).forEach(System.out::println);
        System.out.println("---------------------------------------------------------------");
        // 生成
        Stream.generate(Math::random).limit(5).forEach(System.out::println);
    }

    /*
    ###################################################################
    #######################    筛选 与 切片     ########################
    ###################################################################
     */
    @Test
    public void test5()
    {
        /**
         *  filter()    ：过滤条件
         *  limit()     ：限制取出数据
         *  skip()      ：跳过多少数据
         *  distinct()  ：根据对象里的 hashCode() equals() 方法去重
         *
         */
        // 1. 创建
        List<Employee> list = EmployeeData.getEmployees();
        Stream<Employee> stream = list.stream();
        // 2. 中间操作
        stream.filter(e -> e.getSalary()>7000).limit(2).forEach(System.out::println);
        System.out.println("----------------------------");

        int page      = 2;
        int pageCount = 3;
        list.stream().skip(page*pageCount).limit(pageCount).forEach(System.out::println);

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        list.add(new Employee(1008, "扎克伯格", 35, 2500.32));// 加入重复数据
        list.add(new Employee(1008, "扎克伯格", 35, 2500.32));// 加入重复数据
        list.add(new Employee(1008, "扎克伯格", 36, 2500.33));// 加入重复数据
        list.stream().distinct().forEach(System.out::println);
    }


    /*
    ###################################################################
    #######################        映射        ########################
    ###################################################################
     */
    /**
     * 映射
     * map(Function f)          函数作为参数 应用到每个元素上
     * flatmap(Function f)      接收一个函数作为参数 将流中的每个值都换成另一个流，然后把所有流连接成一个流。
     */
    @Test
    public void test2(){
        // 1. map()
        List<String> list = Arrays.asList("aa", "bb", "ccc");
        list.stream().map(s->s.toUpperCase()).forEach(System.out::println);

        System.out.println("----------------------------");

        // 获取 姓名 流
        List<Employee> emp = EmployeeData.getEmployees();
        Stream<String> nameStream = emp.stream().map(e -> e.getName());
        // 打印 姓马的
        nameStream.filter(s -> s.startsWith("马")).forEach(System.out::println);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        // 2. flatMap() 接收一个函数作为参数 将流中的每个值都换成另一个流，然后把所有流连接成一个流。
        list.stream().flatMap(StreamTest::fromStringToStream).forEach(System.out::println);

        System.out.println("=============================");

        // 3. mapToInt(ToIntFunction f)
        List<Integer> intList = Arrays.asList(100, 200, 300);
        intList.stream().mapToInt(i -> i*100).forEach(System.out::println);
    }
    // 将 字符串转字符对应的Stream实例
    private static Stream<String> fromStringToStream(String s)
    {
        ArrayList<String> charList = new ArrayList<>();
        char[] chars = s.toCharArray();
        for (char c : chars)
        {
            charList.add(c+"pppppp");
        }
        return charList.stream();
    }

    /*
    ###################################################################
    #######################        排序        ########################
    ###################################################################
     */
    /**
     * 排序
     * sorted() 自然排序
     * sorted(Comparator c) 按实例比较器排序
     */
    @Test
    public void streamSort()
    {
        // sorted() 自然排序
        List<Integer> intList = Arrays.asList(200, 300,100, 900);
        intList.stream().sorted().forEach(System.out::println);

        System.out.println("----------------------------");

        // sorted(Comparator c) 比较器排序
        List<Employee> empList = EmployeeData.getEmployees();
        empList.stream().sorted((o1, o2) -> Double.compare(o1.getSalary(),o2.getSalary()))
                .forEach(System.out::println);
    }

    /*
    ###################################################################
    #######################       终止操作      ########################
    ###################################################################
     */
    /**
     *  匹配与查找
     *  allMatch(Predicate p)       是否匹配所有元素
     *  anyMatch(Predicate p)       是否至少匹配一元素
     *  noneMatch(Predicate p)      是否匹配不到所有元素
     *  findFirst()                 返回第一个元素
     *  findAny()                   返回任一元素
     */
    @Test
    public void test1(){
        List<Employee> list = EmployeeData.getEmployees();

        // 1. allMatch()
        boolean b1 = list.stream().allMatch(e -> e.getAge() > 12);
        System.out.println("allMatch(e -> e.getAge() > 12) = " + b1);
        // 2. anyMatch() 至少匹配一个
        boolean b2 = list.stream().anyMatch(e -> e.getSalary() > 9500);
        System.out.println("anyMatch(e -> e.getSalary() > 9500) = " + b2);
        // 3. noneMatch() 匹配不到所有元素 (是否：所有元素都不满足)
        boolean b3 = list.stream().noneMatch(e -> e.getSalary() > 14000);
        System.out.println("noneMatch(e -> e.getSalary() > 1000) = " + b3);

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        // 4. findFirst()
        Optional<Employee> first = list.stream().findFirst();
        System.out.println(first);
        // 5. findAny()
        Optional<Employee> any = list.stream().findAny();
        System.out.println(any);
    }
    /**
     *  count()             返回流元素总数
     *  max() min()         返回最大或最小的元素
     *  forEach(Comsumer c) 内部迭代
     */
    @Test
    public void testCountMaxMinForEach(){
        List<Employee> list = EmployeeData.getEmployees();
        // 1. count()
        long c = list.stream().filter(e -> e.getAge() > 40).count();
        System.out.println(c);
        // 2. max() min()
        Optional<Employee> max = list.stream().max((i1, i2) -> Integer.compare(i1.getAge(), i2.getAge()));
        System.out.println(max);

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        // 3. forEach()
        list.stream().forEach(e -> System.out.println(e.getName()));
    }

    /*
    ###################################################################
    #######################        归约        ########################
    ###################################################################
     */
    /**
     * 归约
     * reduce(T iden,BinaryOperator< T,T,T> b)  将流中元素反复结合 得到一个值 T
     * reduce(BinaryOperator< T,T,T> b)         将流中元素反复结合 得到一个值 返回Optional< T>
     */
    @Test
    public void test3()
    {
        // 1. reduce(T iden,BinaryOperator< T,T,T> b)
        List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5);

        Integer reduce1 = list1.stream().reduce(100, (i1,i2) -> i1 + i2);
        System.out.println(reduce1);

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        // 2. reduce(BinaryOperator< T,T,T> b)
        List<Employee> list2 = EmployeeData.getEmployees();
        Optional<Double> reduce2 = list2.stream().map(e -> e.getSalary()).reduce(Double::sum);
        System.out.println(reduce2);
    }

    /*
    ###################################################################
    #######################        收集        ########################
    ###################################################################
     */
    /**
     * 收集 => 收集成 列表或集合 等
     *  collect(Collectors.toList())
     *  collect(Collectors.toSet())
     */
    @Test
    public void test4()
    {
        List<Employee> list = EmployeeData.getEmployees();

        // 1. collect(Collectors.toList())          收集成列表
        List<Employee> salaryList = list.stream().filter(e -> e.getSalary()>6000).collect(Collectors.toList());
        salaryList.forEach( employee -> System.out.println(employee));

        // 2. collect(Collectors.toSet())           收集成集合
        Set<Double> salarySet = list.stream().map(e -> e.getSalary()).collect(Collectors.toSet());
        System.out.println(salarySet);
    }


}
