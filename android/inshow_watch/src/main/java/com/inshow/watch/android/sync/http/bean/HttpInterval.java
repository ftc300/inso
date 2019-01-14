package com.inshow.watch.android.sync.http.bean;

/**
 * Created by chendong on 2017/5/5.
 */

public class HttpInterval {
    public int interval;
    public int start;
    public String status;

    public HttpInterval( int interval, int start,String status) {
        this.status = status;
        this.interval = interval;
        this.start = start;
    }
}
