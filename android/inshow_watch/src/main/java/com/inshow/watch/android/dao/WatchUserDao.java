package com.inshow.watch.android.dao;

/**
 * Created by chendong on 2017/5/19.
 */

public class WatchUserDao {
    public int id;
    public int height;
    public int weight;
    public String gender;
    public String birth;

    public WatchUserDao() {
    }

    public WatchUserDao(int height, int weight, String gender, String birth) {
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.birth = birth;
    }
}
