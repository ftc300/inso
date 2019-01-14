package com.inshow.watch.android.dao;

/**
 * Created by chendong on 2017/3/7.
 */
public class AlarmDao {
    public int id;
    public int seconds;
    public int extend;
    public String repeatType;
    public boolean status;
    public String desc;

    public AlarmDao() {
    }

    public AlarmDao(int id, int seconds, int extend, String repeatType, boolean status, String desc) {
        this.id = id;
        this.seconds = seconds;
        this.extend = extend;
        this.repeatType = repeatType;
        this.status = status;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "AlarmDao{" +
                "id=" + id +
                ", seconds=" + seconds +
                ", extend=" + extend +
                ", repeatType='" + repeatType + '\'' +
                ", status=" + status +
                ", desc='" + desc + '\'' +
                '}';
    }
}
