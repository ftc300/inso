package com.inshow.watch.android.sync.http.bean;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/10/31
 * @ 描述:
 */


public class HttpVibrate {

    /**
     * starttime : 23:30
     * endtime : 07:00
     * isdoubletime : 1
     * isnodisturb : 1
     */
    public  int isdoubletime;
    public  int isnodisturb;
    public  int autoclosevibrate;
    public  String starttime;
    public  String endtime;

    public HttpVibrate(int isdoubletime, int isnodisturb, String starttime, String endtime,int autoclosevibrat) {
        this.isdoubletime = isdoubletime;
        this.isnodisturb = isnodisturb;
        this.starttime = starttime;
        this.endtime = endtime;
        this.autoclosevibrate = autoclosevibrat;
    }
}
