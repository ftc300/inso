package com.inshow.watch.android.manager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2018/5/15
 * @ 描述:
 */


public class ScheduleMgr {
    private static volatile ScheduleMgr mInstance;
    private static volatile ScheduledExecutorService executor;

    public ScheduleMgr() {
        executor = new ScheduledThreadPoolExecutor(1);
    }

    public static ScheduleMgr getInstance() {
        if (mInstance == null) {
            synchronized (ScheduleMgr.class) {
                if (mInstance == null) {
                    mInstance = new ScheduleMgr();
                }
            }
        }
        return mInstance;
    }


    public void scheduleAtFixedRate(Runnable runnable,
                      long initialDelay,
                      long period,
                      TimeUnit unit) {
        //手表已经连接时启动
        executor.scheduleAtFixedRate(runnable, initialDelay, period, unit);
    }

    public void stopNow() {
        if (null != executor && !executor.isTerminated()) {
            executor.shutdownNow();
        }
        mInstance = null;
        executor = null;
    }
}
