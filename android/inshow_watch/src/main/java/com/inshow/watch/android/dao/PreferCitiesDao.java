package com.inshow.watch.android.dao;

/**
 * Created by chendong on 2017/3/20.
 */
public class PreferCitiesDao {
    public long id;
    public String zh_cn;
    public String en;
    public String zh_tw;
    public String zh_hk;
    public String zone;
    public boolean isSel;
    public boolean isDefault;

    public PreferCitiesDao(long id, String zh_cn, String en, String zh_tw, String zh_hk, String zone, boolean isSel, boolean isDefault) {
        this.id = id;
        this.zh_cn = zh_cn;
        this.en = en;
        this.zh_tw = zh_tw;
        this.zh_hk = zh_hk;
        this.zone = zone;
        this.isSel = isSel;
        this.isDefault = isDefault;
    }

    public PreferCitiesDao(long id, String zh_cn, String zone) {
        this.id = id;
        this.zh_cn = zh_cn;
        this.zone = zone;
    }

}
