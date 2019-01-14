package com.inso.plugin.sync.http.bean;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2018/2/1
 * @ 描述:
 */

//"server_time":1490814514,"pointer_time":3600
public class HttpAdjustTime {
    public long server_time ;
    public long pointer_time ;

    public HttpAdjustTime(long server_time, long pointer_time) {
        this.server_time = server_time;
        this.pointer_time = pointer_time;
    }
}
