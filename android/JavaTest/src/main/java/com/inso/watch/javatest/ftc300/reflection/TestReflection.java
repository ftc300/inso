package com.inso.watch.javatest.ftc300.reflection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/3/22
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class TestReflection
{
    @FunctionTime
    public void algo1() {
        ArrayList<Integer> l = new ArrayList<>();
        for (int i = 0; i < 10000000; i++) {
            l.add(1);
        }
    }

    @FunctionTime
    public void algo2() {
        LinkedList<Integer> l = new LinkedList<>();
        for (int i = 0; i < 10000000; i++) {
            l.add(1);
        }
    }

    public void algo3() {
        Vector<Integer> v = new Vector<>();
        for (int i = 0; i < 10000000; i++) {
            v.add(1);
        }
    }

    public static void main(String[] foo){
        FunctionTimer tu = new FunctionTimer();
        tu.getTime();
    }

}
