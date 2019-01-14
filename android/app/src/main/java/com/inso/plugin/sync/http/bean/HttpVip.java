package com.inso.plugin.sync.http.bean;

/**
 * Created by chendong on 2017/6/2.
 */

public class HttpVip {
    /**
     * id:1
     * name : 张三
     * number : 13812345678
     * status : on
     */

    public int id;
    public String name;
    public String number;
    public String status;

    public HttpVip(int id, String name, String number, String status) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.status = status;
    }
}
