//package com.inshow.watch.android.act.dfu;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.view.View;
//
//import com.inshow.watch.android.R;
//import com.inshow.watch.android.basic.BasicAct;
//import com.inshow.watch.android.manager.AppController;
//import com.inshow.watch.android.manager.SPManager;
//import com.inshow.watch.android.sync.SyncDeviceHelper;
//import com.inshow.watch.android.tools.L;
//import com.inshow.watch.android.view.DfuProgressView;
//import com.xiaomi.smarthome.bluetooth.Response;
//import com.xiaomi.smarthome.bluetooth.XmBluetoothDevice;
//import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;
//import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;
//import com.xiaomi.smarthome.device.api.XmPluginHostApi;
//
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import no.nordicsemi.android.dfu.DfuBaseTimerTask;
//import no.nordicsemi.android.dfu.DfuProgressListener;
//import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
//import no.nordicsemi.android.dfu.DfuServiceInitiator;
//import no.nordicsemi.android.dfu.IReconnect;
//
//import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_BLUETOOTH_CONNECTED;
//import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_DEVICE_NAME;
//import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_DFU_MODE;
//import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_FIRMWARE_VERSION;
//import static com.inshow.watch.android.tools.MessUtil.getIncrementedAddress;
//import static com.xiaomi.smarthome.bluetooth.XmBluetoothManager.SCAN_BLE;
//
///**
// * Created by chendong on 2017/4/17.
// */
//
//public class ProcessingAct extends BasicAct {
//
//    private DfuProgressView progressView;
//    private ScheduledExecutorService dfuPool;
//    private boolean hasScanFound = false;
//    private String URL;
//    private String mCurrentV;
//    private String mLastV;
//    private String mDownLoadFilePath;
//    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//    private AtomicInteger nowAtomicPercent = new AtomicInteger(0);
//    private AtomicInteger lastAtomicPercent = new AtomicInteger(0);
//    private int index = 0;
//    public static AtomicBoolean hasSwitch ;
//
//
//    @Override
//    protected int getContentRes() {
//        return R.layout.watch_act_dfu_processing;
//    }
//
//    @Override
//    protected void initViewOrData() {
//        setTitleText(mDBHelper.getCache(SP_ARG_DEVICE_NAME));
//        setActStyle(ActStyle.DFU);
//        hasSwitch = new AtomicBoolean(false);
//        findViewById(R.id.title_bar_return).setVisibility(View.GONE);
//        progressView = (DfuProgressView) findViewById(R.id.cirPro);
//        URL = getIntent().getStringExtra("url");
//        mCurrentV = getIntent().getStringExtra("cv");
//        mLastV = getIntent().getStringExtra("lv");
//        progressView.setProgress(0);
//        intoDfuModeUpgrade();
//        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                if (nowAtomicPercent.get() > lastAtomicPercent.get()) {
//                    L.d("固件升级中，一切正常！！");
//                    lastAtomicPercent.set(nowAtomicPercent.get());
//                } else if (lastAtomicPercent.get() == 0 && index > 12) {//两分钟后还是在连接状态 或者 进度没有更新
//                    L.d("固件升级停止，未连接上！！");
//                    showFailAct();
//                } else if (lastAtomicPercent.get() > 0
//                        && nowAtomicPercent.get() == lastAtomicPercent.get()
//                        && nowAtomicPercent.get() != 100 ) {
//                    L.d("固件升级中间出错了！！nowAtomicPercent:" + nowAtomicPercent.get());
//                    showFailAct();
//                }
//                index++;
//            }
//        }, 0, 5, TimeUnit.SECONDS);
//
//    }
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//
//    void releaseService() {
//        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
//            scheduledExecutorService.shutdownNow();
//        }
//        scheduledExecutorService = null;
//    }
//
//    private void intoDfuModeUpgrade() {
//        L.e("intoDfuModeUpgrade");
//        SyncDeviceHelper.syncSetControlFlag(MAC, new SyncDeviceHelper.BtCallback() {
//            @Override
//            public void onBtResponse(byte[] bytes) {
//                XmPluginHostApi.instance().downloadBleFirmware(URL, new Response.BleUpgradeResponse() {
//                    @Override
//                    public void onProgress(int progress) {
//                    }
//
//                    @Override
//                    public void onResponse(int code, final String filePath) {
//                        L.e("downloadBleFirmware filePath:" + filePath);
//                        if (XmBluetoothManager.Code.REQUEST_SUCCESS == code) {
//                            mDownLoadFilePath = filePath;
//                            progressView.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    showUpgradeProgress(1);
//                                }
//                            }, 1000);
//                            startOtaInDfuMode();
//                        }
//                    }
//                });
//
//            }
//        }, new int[]{5, 0, 0, 0});
//    }
//
//    public void startOtaInDfuMode() {
//        XmBluetoothManager.getInstance().disconnect(MAC);
//        SPManager.put(mContext, SP_ARG_DFU_MODE, true);
//        SPManager.put(mContext, SP_ARG_BLUETOOTH_CONNECTED, false);
//        L.e("getIncrementedAddress(MAC)：" + getIncrementedAddress(MAC));
//        final DfuServiceInitiator starter = new DfuServiceInitiator(getIncrementedAddress(MAC))
//                .setDisableNotification(true)//need't Notification Act
//                .setKeepBond(false)
//                .setDeviceName("DFU")
//                .setZip(mDownLoadFilePath);
//        final DfuBaseTimerTask timeTask = new DfuBaseTimerTask();
//        timeTask.context = mContext;
//        timeTask.setInshowDfuProgressListner(mDfuProgressListener);
//        timeTask.setReconnectListener(new IReconnect() {
//            @Override
//            public void restartTimer() {
//                L.e("Connect Failed ,So restartTimer()!!!!");
//                progressView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        timeTask.setReconnectListener(null);//只是重试一次
//                        startUpgradeTask(starter, timeTask);
//                    }
//                }, 0);//延时1s重启任务
//            }
//        });
//        startUpgradeTask(starter, timeTask);
//    }
//
//    /**
//     * 开启下载任务
//     *
//     * @param starter
//     * @param firstTask
//     */
//    public void startUpgradeTask(final DfuServiceInitiator starter, final DfuBaseTimerTask firstTask) {
//        dfuPool = new ScheduledThreadPoolExecutor(4);
//        XmBluetoothManager.getInstance().startScan(30 * 1000, SCAN_BLE, new XmBluetoothManager.BluetoothSearchResponse() {
//            @Override
//            public void onSearchStarted() {
//            }
//
//            @Override
//            public void onDeviceFounded(XmBluetoothDevice xmBluetoothDevice) {
//                L.e("DFU Scan DeviceFounded:" + xmBluetoothDevice.getAddress().toUpperCase());
//                if (getIncrementedAddress(MAC).toUpperCase().equals(xmBluetoothDevice.getAddress().toUpperCase())) {
//                    L.e("DFU Scan Find DFU MAC:" + xmBluetoothDevice.getAddress().toUpperCase());
//                    XmBluetoothManager.getInstance().stopScan();
//                    hasScanFound = true;
//                    starter.start(mContext, dfuPool, firstTask);
//                }
//            }
//
//            @Override
//            public void onSearchStopped() {
//                L.e("DFU Scan onSearchStopped");
//                if (!hasScanFound) {
//                    starter.start(mContext, dfuPool, firstTask);
//                }
//                hasScanFound = false;
//            }
//
//            @Override
//            public void onSearchCanceled() {
//                hasScanFound = false;
//            }
//        });
//    }
//
//
//    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
//        int delayPercent = 86; //
//        int currentPercent = 0;
//
//        @Override
//        public void onDeviceConnecting(final String deviceAddress) {
//            L.e("onDeviceConnecting ");
//            if (currentPercent > 0 && currentPercent < 100) {
//                L.e("DfuProgressListener onDeviceDisconnecting percent > 0 && <100  ===> PAGE_UPGRADE_FAILED ");
//                showFailAct();
//            }
//        }
//
//
//        @Override
//        public void onDfuCompleted(final String deviceAddress) {
//            L.e("DfuProgressListener onDfuCompleted:" + deviceAddress);
//            SPManager.put(mContext, SP_ARG_FIRMWARE_VERSION, mLastV);
//            SPManager.put(mContext, SP_ARG_DFU_MODE, false);
//        }
//
//        @Override
//        public void onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) {
//            L.e("DfuProgressListener onProgressChanged:" + percent);
//            currentPercent = percent;
//            nowAtomicPercent.set(percent);
//            if (percent > 1 && percent < 100) {
//                showUpgradeProgress((int) (percent * 0.85));
//            } else if (percent == 100) {
//                mCurrentV = mLastV;
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (delayPercent == 100) {
//                            L.e("onDfuCompleted PAGE_UPGRADE_SUCCESS");
//                            showSuccessAct();
//                        } else {
//                            L.e("onDfuCompleted delayPercent = " + delayPercent);
//                            delayPercent++;
//                            showUpgradeProgress(delayPercent);
//                            mHandler.postDelayed(this, 800);
//                        }
//                    }
//                }, 800);
//            }
//        }
//
//
//        @Override
//        public void onError(final String deviceAddress, final int error, final int errorType, final String message) {
//            L.e("DfuProgressListener onError:" + message);
//            showFailAct();
//        }
//
//        @Override
//        public void onDeviceDisconnected(String deviceAddress) {
//            super.onDeviceDisconnected(deviceAddress);
//            L.e("DfuProgressListener onDeviceDisconnected:" + deviceAddress);
//            if (currentPercent < 100) {
//                showFailAct();
//            }
//        }
//    };
//
//    private void showSuccessAct() {
//        if (!hasSwitch.get()) {
//            Intent intent = new Intent();
//            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//            startActivity(intent, DfuSuccessAct.class.getName());
//            ProcessingAct.this.finish();
//            releaseService();
//            hasSwitch.set(true);
//        }
//    }
//
//    private void showUpgradeProgress(int percent) {
//        progressView.setProgressNotInUiThread(percent);
//    }
//
//    private void showFailAct() {
//        if (!hasSwitch.get()) {
//            Intent intent = new Intent();
//            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//            startActivity(intent, DfuFailAct.class.getName());
//            ProcessingAct.this.finish();
//            releaseService();
//            hasSwitch.set(true);
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//    }
//}
