package com.inso.watch.javatest.day2.proxy.mine;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/2/21
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class Proxy2Ft implements InvocationHandler{
    private IFT ft;


    public Proxy2Ft(IFT ft) {
        this.ft = ft;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(ft,args);
    }
}
