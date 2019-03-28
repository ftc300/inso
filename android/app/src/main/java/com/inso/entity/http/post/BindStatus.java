package com.inso.entity.http.post;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/3/21
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class BindStatus extends FatherParam{
    public String sn;
    public String mac;

    public BindStatus(String sn, String mac) {
        this.sn = sn;
        this.mac = mac;
    }

    @Override
    public String toString() {
        return "BindStatus{" +
                "sn='" + sn + '\'' +
                ", mac='" + mac + '\'' +
                '}';
    }
}
