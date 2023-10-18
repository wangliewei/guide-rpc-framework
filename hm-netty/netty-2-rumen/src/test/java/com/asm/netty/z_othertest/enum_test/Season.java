package com.asm.netty.z_othertest.enum_test;

// 自定义枚举类
class Season1{
    // 1. 声明属性 private final修饰
    private final String seasonName;
    private final String seasonDesc;
    // 2. 私有化类构造器
    private Season1(String seasonName, String seasonDesc)
    {
        this.seasonName = seasonName;
        this.seasonDesc = seasonDesc;
    }
    // 3. 提供多个对象
    public static final Season1 SPRING = new Season1("春天","春暖花开");
    public static final Season1 SUMMER = new Season1("夏天","夏日炎炎");
    public static final Season1 AUTUMN = new Season1("秋天","秋高气爽");
    public static final Season1 WINTER = new Season1("冬天","冰天雪地");
    // 4. 获取对象属性
    public String getSeasonName(){
        return this.seasonName;
    }
    public String getSeasonDesc() {
        return seasonDesc;
    }
    @Override
    public String toString() {
        return "Season{" +
                "seasonName='" + seasonName + '\'' +
                ", seasonDesc='" + seasonDesc + '\'' +
                '}';
    }
}

// Enum 关键字创建 枚举类
enum Season2{
    // 1. 提供多个对象逗号分隔
    SPRING("春天","春暖花开"),
    SUMMER("夏天","夏日炎炎"),
    AUTUMN("秋天","秋高气爽"),
    WINTER("冬天","冰天雪地");

    // 2. 声明属性 private final修饰
    private final String seasonName;
    private final String seasonDesc;

    // 3. 私有化类构造器
    Season2(String seasonName, String seasonDesc)
    {
        this.seasonName = seasonName;
        this.seasonDesc = seasonDesc;
    }
    // 4. 获取对象属性
    public String getSeasonName(){
        return this.seasonName;
    }
    public String getSeasonDesc() {
        return seasonDesc;
    }
}

class TestSeason{
    public static void main(String[] args) {
/*        final Season1 spring = Season1.SPRING;
        System.out.println(spring.getSeasonDesc());*/

        final Season2 summer = Season2.SUMMER;
        System.out.println(summer.getSeasonDesc());

        final Season2 autumn = Season2.valueOf("AUTUMN");
        System.out.println(autumn.getSeasonDesc());

        final int ordinal = summer.ordinal();
        System.out.println("该枚举排列第几个 = " + ordinal);

    }
}