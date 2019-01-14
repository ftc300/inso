package com.inshow.watch.android.model;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/9/29
 * @ 描述:
 */


public class DebugLogEntity {
    public int eventID;
    public int modifyTime;
    public int argmentOne;
    public int argmentTwo;

    public DebugLogEntity(int eventID, int modifyTime, int argment, int extend) {
        this.eventID = eventID;
        this.modifyTime = modifyTime;
        this.argmentOne = argment;
        this.argmentTwo = extend;
    }

    @Override
    public String toString() {
        return eventID + "," + modifyTime + "," + argmentOne + "," + argmentTwo;
    }
}
