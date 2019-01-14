package com.inshow.watch.android.tools;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by chendong on 2017/3/1.
 */
public class ThreadPoolExeutor {
    private static ScheduledThreadPoolExecutor executor ;
    static {
        executor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
    }
    public static  void scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
    {
        executor.scheduleAtFixedRate( command,  initialDelay,  period,  unit);
    }
}
