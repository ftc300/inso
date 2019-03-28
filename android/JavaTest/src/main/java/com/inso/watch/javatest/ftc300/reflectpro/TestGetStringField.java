package com.inso.watch.javatest.ftc300.reflectpro;

import java.lang.reflect.Field;

public class TestGetStringField {

    public void test() throws NoSuchFieldException, IllegalAccessException {
        StringDemo stringDemo = new StringDemo();
        Class<StringDemo> stringDemoClass = StringDemo.class;

        Field ss = stringDemoClass.getDeclaredField("s");
        ss.setAccessible(true);

        String o = (String) ss.get(stringDemo);
        System.out.println(o);

        ss.set(stringDemo,"helloworld!");

        String o2 = (String) ss.get(stringDemo);
        System.out.println(o2);
    }
}
