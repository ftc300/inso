package com.inso.plugin.model;

/**
 * Created by chendong on 2017/3/23.
 */

public class DataSheetEntity  {
    public  String year;
    public String dateString;
    public  String  week;
    public int step;//步数
    public int duration;//运动时长 秒
    public int distance;//里程 单位m >1000转换成km
    public  int consume;//

    public DataSheetEntity(String year,String dateString) {
        this.year = year;
        this.dateString = dateString;
        this.step = 0;
        this.duration = 0;
        this.distance = 0;
        this.consume = 0;
    }
    public DataSheetEntity( String year,String dateString,String week) {
        this.year = year;
        this.dateString = dateString;
        this.week = week;
        this.step = 0;
        this.duration = 0;
        this.distance = 0;
        this.consume = 0;
    }

    public DataSheetEntity(String year, String dateString, int step, int duration, int distance, int consume) {
        this.year = year;
        this.dateString = dateString;
        this.step = step;
        this.duration = duration;
        this.distance = distance;
        this.consume = consume;
    }

    @Override
    public String toString() {
        return "DataSheetEntity{" +
                "year='" + year + '\'' +
                ", dateString='" + dateString + '\'' +
                ", week='" + week + '\'' +
                ", step=" + step +
                ", duration=" + duration +
                ", distance=" + distance +
                ", consume=" + consume +
                '}';
    }

}
