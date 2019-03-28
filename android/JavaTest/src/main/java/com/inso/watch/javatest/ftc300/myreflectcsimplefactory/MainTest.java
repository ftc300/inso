package com.inso.watch.javatest.ftc300.myreflectcsimplefactory;

import java.lang.reflect.Method;

//使用反射实现简单工厂模式
public class MainTest {
    public static void main(String[] args) {
        CarFactory carFactory = new CarFactory();
        Class<CarFactory> carFactoryClass = CarFactory.class;
        Method getCar = null;
        Car car = null;
        try {
            getCar = carFactoryClass.getDeclaredMethod("getCar", String.class);
            car = (Car) getCar.invoke(carFactory, "com.inso.watch.javatest.ftc300.myreflectcsimplefactory.Porche");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String name = car.getName();
        System.out.println("car name:"+name);
    }
}
