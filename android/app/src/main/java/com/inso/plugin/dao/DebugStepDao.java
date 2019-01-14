package com.inso.plugin.dao;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/12/20
 * @ 描述:
 */


public class DebugStepDao {
    public long starttime;
    public long endtime;
    public int startstep;
    public int endstep;
    public int type;
    public int goal;
    public String mac;

    public DebugStepDao() {
    }


    public DebugStepDao(int goal, long starttime, long endtime, int startstep, int endstep, int type, String mac) {
        this.goal = goal;
        this.starttime = starttime;
        this.endtime = endtime;
        this.startstep = startstep;
        this.endstep = endstep;
        this.type = type;
        this.mac = mac;
    }

    @Override
    public String toString() {
        return "DebugStepDao："+goal +","+
        starttime+","+
        endtime+","+
        startstep+","+
        endstep+","+
        type +","+mac;
    }
}
