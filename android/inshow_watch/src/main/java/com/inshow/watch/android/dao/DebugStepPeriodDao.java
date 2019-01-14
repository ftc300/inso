package com.inshow.watch.android.dao;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/12/20
 * @ 描述:
 */


public class DebugStepPeriodDao {
    public int period ;//当前状态 ，未开始跑，开始 ，结束
    public long starttime;
    public int startstep;
    public int type;
    public String mac;

    public DebugStepPeriodDao() {
    }

    public DebugStepPeriodDao(int period, int starttime, int startstep, int type, String mac) {
        this.period = period;
        this.starttime = starttime;
        this.startstep = startstep;
        this.type = type;
        this.mac = mac;
    }


    @Override
    public String toString() {
        return "DebugStepPeriodDao："+period +","+
        starttime+","+
        startstep+","+
        type+","+mac;
    }
}
