package com.inso.plugin.sync.http.bean;

import java.util.List;

/**
 * Created by chendong on 2017/5/5.
 * 注1:status为on或off，default为true或false，
 * 注2：闹钟类型(normal_alarm/type)为 Bit15-0
 * 0: 响一次；
 * 1: 每天；
 * 2: 法定工作日；
 * 3: 法定节假日；
 * 4: 周一；
 * 5: 周二；
 * 6: 周三；
 * 7: 周四；
 * 8: 周五；
 * 9: 周六；
 * 10: 周日
 */
public class HttpAlarm {
    public int id;
    public String status;
    public int time;
    public int nextring;
    public List<Integer> type;
    public String label;


    public HttpAlarm(int id, String status, int time,int nextring, List<Integer> type, String label) {
        this.id = id;
        this.status = status;
        this.time = time;
        this.nextring = nextring;
        this.type = type;
        this.label = label;
    }
}
