package com.inshow.watch.android.manager;

import android.os.Handler;

import com.inshow.watch.android.tools.L;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2018/5/17
 * @ 描述:   为守护连接回调 ，防止退出插件了还做同步
 */

public class DaemonThread {

    private static volatile DaemonThread mInstance;
    private static volatile ExecutorService executor;
    private static final int DELAY_INTERVAL = 20000;

    public static DaemonThread getInstance() {
        if (mInstance == null) {
            synchronized (DaemonThread.class) {
                if (mInstance == null) {
                    mInstance = new DaemonThread();
                }
            }
        }
        return mInstance;
    }

    //防止重连又开启新的守护线程
    public void start() {
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                L.e("DaemonThread start");
            }
        });
    }

    public boolean isShutdown() {
        return executor == null|| executor.isShutdown();  // try to set this to "false" and see what happens
    }

    public void stopNow() {
        if (null != executor && !executor.isTerminated()) {
            L.e("DaemonThread stopNow");
            executor.shutdownNow();
        }
    }

    public void stopDelay(Handler handler) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != executor && !executor.isTerminated()) {
                    L.e("DaemonThread stopNow");
                    executor.shutdownNow();
                }
            }
        }, DELAY_INTERVAL);
    }

    public void realseDaemon() {
        stopNow();
        if (null != mInstance) {
            mInstance = null;
        }
        if (null != executor) {
            executor = null;
        }
    }

}
