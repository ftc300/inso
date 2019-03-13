package com.inso.watch.javatest.day2.proxy.mine;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/2/21
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class GZFT implements IFT {
    @Override
    public void sell() {
        System.out.println(" gz sell $1000");
    }

    @Override
    public void repaire() {
        System.out.println(" gz repaire $1000");
    }

    @Override
    public int getSaleNum(int i) {
        System.out.println(" gz getSaleNum 11");
        return i + 11;
    }
}
