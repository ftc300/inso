package com.inshow.watch.android.sync.http.bean;

/**
 * Created by chendong on 2017/5/5.
 */

public class HttpUserInfo {

    public int weight;
    public int height;
    public String gender;
    public String birth;

    public HttpUserInfo(int weight, int height, String gender, String birth) {
        this.weight = weight;
        this.height = height;
        this.gender = gender;
        this.birth = birth;
    }

}
