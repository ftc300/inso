package com.inso.watch.javatest.day2.proxy.mine;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/2/21
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class ProxyFT implements IFT{
    private IFT  proxy;

    public ProxyFT(IFT proxy) {
        this.proxy = proxy;
    }

    @Override
    public void sell() {
        proxy.sell();
    }

    @Override
    public void repaire() {
        proxy.repaire();
    }

    @Override
    public int getSaleNum(int i) {
        return i+11;
    }
}
