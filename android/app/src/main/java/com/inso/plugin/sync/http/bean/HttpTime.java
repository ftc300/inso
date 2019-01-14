package com.inso.plugin.sync.http.bean;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2018/2/1
 * @ 描述:
 */

//{"server_time":1517456486,"local_time":1517456486}
public class HttpTime {
    public long server_time ;
    public long local_time ;

    public HttpTime(long server_time, long local_time) {
        this.server_time = server_time;
        this.local_time = local_time;
    }
}
