package com.asm.netty.z_othertest.model;

public class Student implements Person {
    String name = null;
    Integer    age = 0;
    String school = null;

    public Student(String name, Integer age, String school) {
        this.name = name;
        this.age = age;
        this.school = school;
    }

    public Student() {
    }

    private String thisIsStudent(String name, Integer age, String school) {
        this.name = name;
        this.age = age;
        this.school = school;

        return "设置好了";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", school='" + school + '\'' +
                '}';
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
}
