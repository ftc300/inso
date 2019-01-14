package com.inshow.watch.android.model;

import com.inshow.watch.android.view.indexable.IndexableEntity;

/**
 * Created by chendong on 2017/5/31.
 * vip联系人界面
 */
public class VipEntity implements IndexableEntity {
    public int contactId;//手机联系人的DB id
    public int id;//写入手表的id
    public String number;
    public String name;
    public boolean status;

    public VipEntity(int contactId, int id, String number, String name, boolean status) {
        this.id = id;
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

    }

    @Override
    public String toString() {
        return "VipEntity :" + contactId + "," + id + "," + number + "," + name + "," + status;
    }
}
