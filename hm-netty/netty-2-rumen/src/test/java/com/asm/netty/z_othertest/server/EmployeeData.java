package com.asm.netty.z_othertest.server;

import com.asm.netty.z_othertest.model.Employee;

import java.util.ArrayList;
import java.util.List;

public class EmployeeData {

    public static List<Employee> getEmployees()
    {
        final ArrayList<Employee> list = new ArrayList<>();
        list.add(new Employee(1,"王1狗", 11, 1000));
        list.add(new Employee(2,"王2狗", 22, 7000));
        list.add(new Employee(3,"王3狗", 33, 8000));
        list.add(new Employee(4,"王4狗", 44, 9000));
        list.add(new Employee(5,"马5狗", 55, 14000));
        list.add(new Employee(6,"马6狗", 66, 14000));
        list.add(new Employee(7,"马7狗", 77, 14000));

        return list;
    }

}
