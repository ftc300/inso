package com.inso.plugin.model;


import android.content.Context;

import com.inso.plugin.tools.Configuration;
import com.inso.plugin.view.indexable.IndexableEntity;


/**
 *
 */
public class CityEntity implements IndexableEntity {
    private long id;
    private String name;
    public String zh;
    public String zh_tw;
    public String en;
    public String zh_hk;
    private String pinyin;
    private String zone;
    public boolean status;
    private Context context;

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public CityEntity(Context context) {
        this.context = context;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        Configuration.getInstance().LocaleHandler(context, new Configuration.LocaleHandler() {
            @Override
            public void cnHandle() {
                name = zh;
            }

            @Override
            public void twHandle() {
                name = zh_tw;
            }

            @Override
            public void hkHandle() {
                name = zh_hk;
            }

            @Override
            public void enHandle() {
                name = en;
            }

            @Override
            public void defaultHandle() {
                name = zh;
            }
        });
        return name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    @Override
    public String getFieldIndexBy() {
        return getName();
    }

    @Override
    public void setFieldIndexBy(String indexByField) {
        this.name = indexByField;
    }

    @Override
    public void setFieldPinyinIndexBy(String pinyin) {
        this.pinyin = pinyin;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * 两个数据源去重
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CityEntity entity = (CityEntity) o;
        if (entity.getId() != id) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }
}
