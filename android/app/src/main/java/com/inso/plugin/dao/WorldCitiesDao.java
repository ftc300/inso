package com.inso.plugin.dao;

/**
 * Created by chendong on 2017/3/20.
 */
public class WorldCitiesDao {
    public int id;
    public String zh_cn;
    public String zone;

    public WorldCitiesDao(int id, String zh_cn, String zone) {
        this.id = id;
        this.zh_cn = zh_cn;
        this.zone = zone;
    }
}
