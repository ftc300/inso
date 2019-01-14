package com.inso.plugin.model;

import android.text.TextUtils;

import com.inso.plugin.view.indexable.IndexableEntity;


/**
 * Created by chendong on 2017/5/31.
 * vip联系人新增界面 status：select
 */
public class PickVipEntity implements IndexableEntity {
    public String number;
    public String name;
    public boolean status;
    public int contactId;

    public PickVipEntity(int contactId, String number, String name, boolean status) {
        this.contactId = contactId;
        this.number = number;
        this.name = name;
        this.status = status;
    }

    @Override
    public String getFieldIndexBy() {
        return name;
    }

    @Override
    public void setFieldIndexBy(String indexField) {
        this.name = indexField;
    }

    @Override
    public void setFieldPinyinIndexBy(String pinyin) {
        // 需要用到拼音时(比如:搜索), 可增添pinyin字段 this.pinyin  = pinyin
        // 见 CityEntity
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PickVipEntity entity = (PickVipEntity) o;
//        return ((TextUtils.equals(name, entity.name) && TextUtils.equals(number, entity.number)))||contactId == entity.contactId;
        return (TextUtils.equals(name, entity.name) && TextUtils.equals(number, entity.number));
    }

    @Override
    public int hashCode() {
        return contactId;
    }
}
