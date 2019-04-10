package com.inso.entity.http.post;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/3/21
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class Bind  extends FatherParam{
    public String device_id;
    public String product_model;
    public long timestamp;
    public String mac;
    public String sn;

    public Bind(String device_id, String product_model, long time, String mac, String sn) {
        this.device_id = device_id;
        this.product_model = product_model;
        this.timestamp = time;
        this.mac = mac;
        this.sn = sn;
    }

    @Override
    public String toString() {
        return "Bind{" +
                "system='" + system + '\'' +
                ", device_id='" + device_id + '\'' +
                ", product_model='" + product_model + '\'' +
                ", timestamp=" + timestamp +
                ", mac='" + mac + '\'' +
                ", sn='" + sn + '\'' +
                '}';
    }
}
