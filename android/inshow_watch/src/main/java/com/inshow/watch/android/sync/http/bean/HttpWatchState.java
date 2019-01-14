package com.inshow.watch.android.sync.http.bean;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2018/5/2
 * @ 描述:
 */


public class HttpWatchState {
    public int reset_register;
    public int reason_open;

    public HttpWatchState(int reset_register, int reason_open) {
        this.reset_register = reset_register;
        this.reason_open = reason_open;
    }
}
