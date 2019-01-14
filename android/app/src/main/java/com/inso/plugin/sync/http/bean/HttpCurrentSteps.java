package com.inso.plugin.sync.http.bean;

/**
 * Created by chendong on 2017/5/5.
 */

public class HttpCurrentSteps {
    public int count;
    public int time ;

    public HttpCurrentSteps(int time,int count) {
        this.count = count;
        this.time = time;
    }
}
