package com.inso.plugin.dao;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/10/20
 * @ 描述:
 */

public class VibrationDao {

    public boolean stronger;
    public boolean notdisturb;
    public int startTime;
    public int endTime;//分钟

    public VibrationDao() {
    }

    public VibrationDao(boolean stronger, boolean notdisturb, int startTime, int endTime) {
        this.stronger = stronger;
        this.notdisturb = notdisturb;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
