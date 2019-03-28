package com.inso.watch.javatest.ftc300.reflectpro;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AutoDriveTest {
    public static void main(String[] args) throws IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        AutoDrive autoDrive = new AutoDrive();
        System.out.println("=================人工驾驶模式==================");
        autoDrive.drive();


        Class<AutoDrive> autoDriveClass = AutoDrive.class;
        AutoDrive autoDrive2 = autoDriveClass.getDeclaredConstructor(String.class, AutoDrive.Color.class)
                .newInstance("奔驰S级", AutoDrive.Color.YELLOW);

        Method drive = autoDriveClass.getDeclaredMethod("drive", null);

        System.out.println("=================自动驾驶模式==================");
        drive.invoke(autoDrive2,null);
    }
}
