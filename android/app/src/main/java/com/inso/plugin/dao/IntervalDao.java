package com.inso.plugin.dao;

/**
 * Created by chendong on 2017/5/19.
 */

public class IntervalDao {
    public int id;
    public int time;
    public int start;
    public String status;

    public IntervalDao() {
    }

    public IntervalDao(int time, int start, String status) {
        this.time = time;
        this.start = start;
        this.status = status;
    }

    @Override
    public String toString() {
        return "IntervalDao{" +
                "id=" + id +
                ", time=" + time +
                ", start=" + start +
                ", status='" + status + '\'' +
                '}';
    }
}
