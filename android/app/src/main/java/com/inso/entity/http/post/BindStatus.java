package com.inso.entity.http.post;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/3/21
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class BindStatus extends FatherParam{
    public String device_id;
    public String mac;

    public BindStatus(String device_id, String mac) {
        this.device_id = device_id;
        this.mac = mac;
    }

    @Override
    public String toString() {
        return "BindStatus{" +
                "device_id='" + device_id + '\'' +
                ", mac='" + mac + '\'' +
                '}';
    }
}
