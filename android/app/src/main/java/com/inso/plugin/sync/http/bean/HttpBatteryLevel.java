package com.inso.plugin.sync.http.bean;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2018/1/15
 * @ 描述:
 */


public class HttpBatteryLevel {
    public int level;
    public int peak_level;
    public int valley_level;

    public HttpBatteryLevel() {
    }

    public HttpBatteryLevel(int level, int peak_level, int valley_level) {
        this.level = level;
        this.peak_level = peak_level;
        this.valley_level = valley_level;
    }
}
