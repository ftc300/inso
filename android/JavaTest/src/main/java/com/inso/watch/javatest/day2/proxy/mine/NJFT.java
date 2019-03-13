package com.inso.watch.javatest.day2.proxy.mine;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/2/21
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class NJFT implements IFT {

    @Override
    public void sell() {
        System.out.println(" nj sell $100110");
    }

    @Override
    public void repaire() {
        System.out.println(" nj repaire $101100");
    }

    @Override
    public int getSaleNum(int i) {
        System.out.println(" nj getSaleNum 12");
        return 12;
    }
}
