package com.inshow.watch.android.manager;


import android.os.Handler;

import com.inshow.watch.android.tools.L;

/**
 * Created by chendong on 2018/5/25.
 */

public class ConnectDaemon {
    public final static int DELAY_TIME = 20 * 1000;
    private int timeOutCount = 1;
    private boolean timeOutVar = true;//默认是超时的,如果有返回就重置为false

    public void setTimeOutVar(Boolean timeOutVar) {
        this.timeOutVar = timeOutVar;
    }

    public void startMonitor(Handler handler, final ITimeout timeoutListener) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (timeOutVar) {
                    if (timeOutCount == 1) {
                        L.e("startConnectMonitor 监测到第一次超时");
                        timeoutListener.onFirstTimeOut();
                        timeOutCount = 2;
                    } else if (timeOutCount == 2) {
                        timeoutListener.onSecondTimeOut();
                        L.e("startConnectMonitor 监测到第二次超时");
                        timeOutCount = 3;//两次以后就不管了
                    } else {
                        L.e("startConnectMonitor 超时多次");
                    }
                } else {
                    L.e("ConnectDaemon 没有超时 ");
                }
            }
        }, DELAY_TIME);
    }

    public interface ITimeout {
        void onFirstTimeOut();

        void onSecondTimeOut();
    }
}
