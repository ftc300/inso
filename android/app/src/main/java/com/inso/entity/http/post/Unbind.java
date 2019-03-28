package com.inso.entity.http.post;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/3/21
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class Unbind  extends FatherParam{
    public String mac;
    public String sn;

    public Unbind(String mac, String sn) {
        this.mac = mac;
        this.sn = sn;
    }
}
