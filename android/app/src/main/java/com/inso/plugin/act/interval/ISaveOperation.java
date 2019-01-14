package com.inso.plugin.act.interval;


import com.inso.plugin.dao.IntervalDao;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/9/15
 * @ 描述:
 */
public interface ISaveOperation {
    IntervalDao getItem();//获取间隔的item
    void saveData();//保存数据到本地数据库
    int getInterval();//获取间隔时间
    int getStartTime();//获取当前的时间点 网络时间
}
