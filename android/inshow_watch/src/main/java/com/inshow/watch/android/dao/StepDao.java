package com.inshow.watch.android.dao;

/**
 * Created by chendong on 2017/4/18.
 */
public class StepDao {

    public int start;
    public int end;
    public int step;
    public int duration;
    public int distance;
    public int consume;
    public String day; //eg:4/27
    public String week;//eg:4/24-4/30
    public String mon;//eg:4月
    public String year;//eg:2017

    public StepDao() {
    }

    ;

    public StepDao(int start, int end, int step) {
        this.start = start;
        this.end = end;
        this.step = step;
    }

    public StepDao(int start, int end, int step, int duration, int distance, int consume, String day, String week, String mon, String year) {
        this.start = start;
        this.end = end;
        this.step = step;
        this.duration = duration;
        this.distance = distance;
        this.consume = consume;
        this.day = day;
        this.week = week;
        this.mon = mon;
        this.year = year;
    }

    @Override
    public String toString() {
        return "StepDao{" +
                "start=" + start +
                ", end=" + end +
                ", step=" + step +
                ", duration=" + duration +
                ", distance=" + distance +
                ", consume=" + consume +
                ", day='" + day + '\'' +
                ", week='" + week + '\'' +
                ", mon='" + mon + '\'' +
                ", year='" + year + '\'' +
                '}';
    }
}
