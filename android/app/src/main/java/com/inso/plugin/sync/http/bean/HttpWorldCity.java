package com.inso.plugin.sync.http.bean;

/**
 * Created by chendong on 2017/5/5.
 */
public class HttpWorldCity {
    public long id;
    public boolean select;
    public String zh_cn;
    public String zh_tw;
    public String zh_hk;
    public String zone;
    public String en;

    public HttpWorldCity(long id, boolean select, String zh_cn, String zh_tw, String zh_hk, String zone, String en) {
        this.id = id;
        this.select = select;
        this.zh_cn = zh_cn;
        this.zh_tw = zh_tw;
        this.zh_hk = zh_hk;
        this.zone = zone;
        this.en = en;
    }
}
