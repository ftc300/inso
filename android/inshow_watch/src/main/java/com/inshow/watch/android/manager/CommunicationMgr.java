package com.inshow.watch.android.manager;

import com.inshow.watch.android.tools.L;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2018/5/15
 * @ 描述:     定时读取当前步数，为手表提供通讯包，保持连接
 */


public class CommunicationMgr {
    private static volatile CommunicationMgr mInstance;
    ScheduledExecutorService executor;
    public static final int INTERVAL = 30;//间隔30s
    private String MAC;

    public CommunicationMgr(String MAC) {
        this.MAC = MAC;
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public static CommunicationMgr getInstance(String MAC) {
        if (mInstance == null) {
            synchronized (CommunicationMgr.class) {
                if (mInstance == null) {
                    mInstance = new CommunicationMgr(MAC);
                }
            }
        }
        return mInstance;
    }


    public void startHeartbeat() {
        //手表已经连接时启动
        if (XmBluetoothManager.getInstance().getConnectStatus(MAC) == XmBluetoothManager.STATE_CONNECTED && null != executor) {
            executor.scheduleAtFixedRate(new HeartbeatRunnable(MAC), 0, INTERVAL, TimeUnit.SECONDS);
        }else{
            L.e("startHeartbeat  fail");
        }
    }

    public void stopHeartbeatNow() {
        if (null!=executor && !executor.isTerminated()) {
            executor.shutdownNow();
        }
        mInstance = null;
        executor = null;
    }
}
