package com.inso.watch.javatest.day2.proxy.mine;

import java.lang.reflect.Proxy;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/2/21
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class Client  {
    public static void main(String[] args) {
        ProxyFT ft = new ProxyFT(new NJFT());
        ft.sell();

        IFT ift = new GZFT();
        Object object = Proxy.newProxyInstance(ift.getClass().getClassLoader(),ift.getClass().getInterfaces(),new Proxy2Ft(ift));
        ((IFT)object).repaire();
        int i = ((IFT)object).getSaleNum(222);
        System.out.println(i+"");
    }
}
