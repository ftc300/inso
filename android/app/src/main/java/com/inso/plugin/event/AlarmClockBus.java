package com.inso.plugin.event;

/**
 * Created by chendong on 2017/2/14.
 */

public class AlarmClockBus {
    public boolean isAdd;
    public int id;
    public int seconds;
    public String repeatType;
    public boolean isOn;
    public String desc;

    public AlarmClockBus(boolean isAdd, int id, int seconds, String repeatType, boolean isOn, String desc) {
        this.isAdd = isAdd;
        this.id = id;
        this.seconds = seconds;
        this.repeatType = repeatType;
        this.isOn = isOn;
        this.desc = desc;
    }

    @Override
    public String
    toString() {
        return "AlarmClockBus{" +
                "isAdd=" + isAdd +
                ", id=" + id +
                ", seconds=" + seconds +
                ", repeatType='" + repeatType + '\'' +
                ", isOn=" + isOn +
                ", desc='" + desc + '\'' +
                '}';
    }
}
