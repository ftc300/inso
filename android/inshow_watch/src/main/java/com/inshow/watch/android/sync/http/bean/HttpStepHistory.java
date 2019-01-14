package com.inshow.watch.android.sync.http.bean;

/**
 * Created by chendong on 2017/5/16.
 */

public class HttpStepHistory {
    public int start;
    public int end;
    public int count;

    public HttpStepHistory() {
    }

    public HttpStepHistory(int start, int end, int count) {
        this.start = start;
        this.end = end;
        this.count = count;
    }


}
