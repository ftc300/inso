package com.inso.watch.javatest.day1;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/3/25
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class A
{
    static String a = "1";
    static {
        a = "2";
    }
    public static String getA(){
        System.out.println("qqq0"+a);
        a = "3";
        return a;
    }

    public static void main(String[] args) {
        System.out.println("qqq1"+a);
        System.out.println("qqq2"+getA());
        System.out.println("qqq3"+a);
    }
}
