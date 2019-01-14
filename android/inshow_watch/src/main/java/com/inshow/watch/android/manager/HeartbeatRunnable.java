package com.inshow.watch.android.manager;

import com.inshow.watch.android.sync.SyncDeviceHelper;
import com.inshow.watch.android.tools.L;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2018/5/15
 * @ 描述:     保持通信
 */


public class HeartbeatRunnable implements Runnable {
    private String MAC;

    public HeartbeatRunnable(String MAC) {
        this.MAC = MAC;
    }

    @Override
    public void run() {
        if (XmBluetoothManager.getInstance().getConnectStatus(MAC) == XmBluetoothManager.STATE_CONNECTED) {
            SyncDeviceHelper.heartbeatTest(MAC, new SyncDeviceHelper.BtCallback() {
                @Override
                public void onBtResponse(byte[] bytes) {
                    L.e("HeartbeatRunnable:communication success");
                }
            });
        }else {
            L.e("HeartbeatRunnable:disconnect from watch");
        }
    }
}
