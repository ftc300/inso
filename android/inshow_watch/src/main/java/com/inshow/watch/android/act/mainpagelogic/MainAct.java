package com.inshow.watch.android.act.mainpagelogic;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.inshow.watch.android.R;
import com.inshow.watch.android.act.debug.DebugAct;
import com.inshow.watch.android.act.setting.AdjustMainAct;
import com.inshow.watch.android.act.setting.BodyInfoAct;
import com.inshow.watch.android.act.setting.DeviceInfoAct;
import com.inshow.watch.android.act.setting.InstructionAct;
import com.inshow.watch.android.act.setting.VibrationSettingAct;
import com.inshow.watch.android.act.user.RecoveryAct;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.basic.onDeviceNameChangeListener;
import com.inshow.watch.android.event.ChangeUI;
import com.inshow.watch.android.event.HomePageBus;
import com.inshow.watch.android.fragment.FragmentBottom;
import com.inshow.watch.android.fragment.FragmentTop;
import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.manager.BleManager;
import com.inshow.watch.android.manager.SPManager;
import com.inshow.watch.android.provider.DBHelper;
import com.inshow.watch.android.sync.AsyncHttpManager;
import com.inshow.watch.android.sync.ITerminatedListener;
import com.inshow.watch.android.sync.IUtcTimeLoadComplete;
import com.inshow.watch.android.sync.SyncDeviceHelper;
import com.inshow.watch.android.sync.device.DeviceTask;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.sync.http.ResponseBase;
import com.inshow.watch.android.sync.http.bean.HttpBatteryLevel;
import com.inshow.watch.android.sync.http.bean.HttpMac;
import com.inshow.watch.android.sync.http.bean.HttpRegister;
import com.inshow.watch.android.sync.http.bean.HttpWatchState;
import com.inshow.watch.android.tools.Configuration;
import com.inshow.watch.android.tools.Constants;
import com.inshow.watch.android.tools.DataCleanManager;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.MessUtil;
import com.inshow.watch.android.tools.NetWorkUtils;
import com.inshow.watch.android.tools.TimeUtil;
import com.inshow.watch.android.tools.ToastUtil;
import com.inshow.watch.android.view.InShowProgressDialog;
import com.inshow.watch.android.view.MainDragLayout;
import com.xiaomi.smarthome.bluetooth.BleUpgrader;
import com.xiaomi.smarthome.bluetooth.Response;
import com.xiaomi.smarthome.bluetooth.XmBluetoothDevice;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;
import com.xiaomi.smarthome.device.api.BaseFragment;
import com.xiaomi.smarthome.device.api.BtFirmwareUpdateInfo;
import com.xiaomi.smarthome.device.api.Callback;
import com.xiaomi.smarthome.device.api.DeviceStat;
import com.xiaomi.smarthome.device.api.XmPluginHostApi;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import static com.inshow.watch.android.MessageReceiver.hasOpenAsync;
import static com.inshow.watch.android.act.mainpagelogic.LowPowerManager.SP_DB_IGNORE_LOW_POWER_TIP;
import static com.inshow.watch.android.act.mainpagelogic.MainHelper.pushBatteryLevelInfo;
import static com.inshow.watch.android.act.mainpagelogic.MainHelper.pushDfuInfo;
import static com.inshow.watch.android.act.mainpagelogic.MainHelper.pushTimeStampInfo;
import static com.inshow.watch.android.act.mainpagelogic.MainHelper.pushWatchState;
import static com.inshow.watch.android.event.ChangeUI.CONNECT_AGAIN;
import static com.inshow.watch.android.event.ChangeUI.RENDER_AGAIN;
import static com.inshow.watch.android.manager.BleManager.B2I_getBatteryLevel1;
import static com.inshow.watch.android.manager.BleManager.B2I_getBatteryLevel2;
import static com.inshow.watch.android.sync.http.HttpSyncHelper.INSHOW_HTTP_START_TIME;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.BATTERY_LEVEL;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_SYNC_CURRENT_TIME;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;
import static com.inshow.watch.android.tools.Constants.OFF;
import static com.inshow.watch.android.tools.Constants.ON;
import static com.inshow.watch.android.tools.Constants.SystemConstant.EXTRAS_DEVICE_STATE;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_BLUETOOTH_CONNECTED;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_DEVICE_NAME;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_DFU_MODE;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_DID;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_FIRMWARE_VERSION;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_MAC;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_MODEL;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_USERID;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_DB_VERSION;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_DEBUG_DFU;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_IS_FIRST_OPEN;
import static com.inshow.watch.android.tools.Constants.TimeStamp.HTTP_MAC;
import static com.inshow.watch.android.tools.Constants.TimeStamp.HTTP_SYNC_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.INTERVAL_ALARM_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.NORMAL_ALARM_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.USER_REGISTER_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.VIP_KEY;
import static com.inshow.watch.android.tools.MessUtil.MENU_CODE;
import static com.inshow.watch.android.tools.MessUtil.getIncrementedAddress;
import static com.inshow.watch.android.tools.MessUtil.setFlickerAnimation;
import static com.inshow.watch.android.tools.TimeUtil.getUsedTime;
import static com.xiaomi.smarthome.bluetooth.XmBluetoothManager.Code.CONNECTION_NOT_READY;
import static com.xiaomi.smarthome.bluetooth.XmBluetoothManager.Code.REQUEST_TIMEDOUT;
import static com.xiaomi.smarthome.bluetooth.XmBluetoothManager.Code.TOKEN_NOT_MATCHED;
import static com.xiaomi.smarthome.bluetooth.XmBluetoothManager.SCAN_BLE;
import static java.lang.System.currentTimeMillis;

/**
 * Created by chendong on 2017/2/17.
 * 首页Act
 *
 * @author chendong
 */
public class MainAct extends BasicAct {
    private BaseFragment firstF, secondF;
    private TextView title, subTitle;
    private ImageView barReturn, barMore;
    private AsyncHttpManager asyncHttpManager;
    private int[] mPowerConsumption = new int[6];
    private int mBatteryLevel, mHaveUsedTime;
    private String[] menus;
    private String mCurrentV;
    private BtFirmwareUpdateInfo mUpdateInfo;
    private ImageView imgRedPoint;
    private boolean hasScanFound = false;
    private int openScanTryCount;
    private final int TRY_LIMIT = 2;
    private final int BATTERY_LIMIT = 30;
    private MainDragLayout dragLayout;
    private ScheduledExecutorService dfuPool;
    private String mDownLoadFilePath = "";
    private boolean batteryHasReaded = false;
    private boolean resumeFromOtherPage = false;
    private int SCAN_INTERVAL = 5 * 1000;
    private boolean isScanning = true; //正在扫描设备中
    private Timer timer;
    private boolean neverResponse = true;
    private final String DFU_ERROR_VERSION = "1.0.5_21";
    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private AtomicInteger nowAtomicPercent = new AtomicInteger(0);
    private AtomicInteger lastAtomicPercent = new AtomicInteger(0);
    private int index = 0;

    @Subscribe
    public void onEventMainThread(HomePageBus event) {
        if (event.clickTryAgain) {
            L.e("onEventMainThread =>clickTryAgain 重新连接");
            checkBleAndConn();
        }
        if (event.forceUpgrade) {
            scanAndConnect();
        }
    }

    private void startDfuDaemon() {
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (nowAtomicPercent.get() > lastAtomicPercent.get()) {
                    L.d("固件升级中，一切正常！！");
                    lastAtomicPercent.set(nowAtomicPercent.get());
                } else if (lastAtomicPercent.get() == 0 && index > 12) {//两分钟后还是在连接状态 或者 进度没有更新
                    L.d("固件升级停止，未连接上！！");
                    showFail();
                } else if (lastAtomicPercent.get() > 0
                        && nowAtomicPercent.get() == lastAtomicPercent.get()
                        && nowAtomicPercent.get() != 100) {
                    L.d("固件升级中间出错了！！nowAtomicPercent:" + nowAtomicPercent.get());
                    showFail();
                }
                index++;
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void releaseService() {
        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdownNow();
        }
        scheduledExecutorService = null;
        if (timeTask != null) {
            timeTask.cancel();
            timer = null;
        }

    }


    private void showFail() {
        releaseService();
        startUpgrade.showPage(XmBluetoothManager.PAGE_UPGRADE_FAILED, null);
    }

    private void showSuccess() {
        releaseService();
        startUpgrade.showPage(XmBluetoothManager.PAGE_UPGRADE_SUCCESS, null);
    }

    @Override
    protected void preInitViewData() {
        mDBHelper.saveCache(SP_ARG_DEVICE_NAME, (String) SPManager.get(mContext, SP_ARG_DEVICE_NAME, ""));
        mDBHelper.saveCache(SP_ARG_MAC, (String) SPManager.get(mContext, SP_ARG_MAC, ""));
        mDBHelper.saveCache(SP_ARG_MODEL, (String) SPManager.get(mContext, SP_ARG_MODEL, ""));
        mDBHelper.saveCache(SP_ARG_USERID, (String) SPManager.get(mContext, SP_ARG_USERID, ""));
        mDBHelper.saveCache(SP_ARG_DID, (String) SPManager.get(mContext, SP_ARG_DID, ""));
    }

    @Override
    protected int getTitleRes() {
        return R.layout.watch_title_bar_transparent_black;
    }

    @Override
    protected int getContentRes() {
        return R.layout.watch_act_main_new;
    }

    private void pushMacInfo(String mac) {
        HttpMac bean = new HttpMac(mac);
        HttpSyncHelper.pushData(new RequestParams(
                MODEL,
                UID,
                DID,
                Constants.HttpConstant.TYPE_USER_INFO,
                HTTP_MAC,
                AppController.getGson().toJson(bean),
                TimeUtil.getNowTimeSeconds()
        ), new Callback<JSONArray>() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                L.e("pushMacInfo onSuccess:" + jsonArray.toString());
            }

            @Override
            public void onFailure(int i, String s) {
                L.e("pushMacInfo onFailure:" + s);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MENU_CODE:
                if (data != null) {
                    String name = data.getStringExtra("menu");
                    if (!TextUtils.isEmpty(name)) {
                        if (TextUtils.equals(name, menus[0])) {
                            switchTo(BodyInfoAct.class);
                        } else if (TextUtils.equals(name, menus[1])) {
                            switchTo(DeviceInfoAct.class);
                        } else if (TextUtils.equals(name, menus[2])) {
                            switchToWithEventBus(AdjustMainAct.class);
                        } else if (TextUtils.equals(name, menus[3])) {
                            switchTo(VibrationSettingAct.class);
                        } else if (TextUtils.equals(name, menus[4])) {
                            switchTo(InstructionAct.class);
                        } else if (TextUtils.equals(name, menus[5])) {
//                            switchTo(WatchLogAct.class);
                        } else if (TextUtils.equals(name, menus[6])) {
                            switchTo(DebugAct.class);
                        }
                    }
                    String removedLicense = data.getStringExtra("result_data");
                    if (removedLicense != null && removedLicense.equals("removedLicense")) {
                        SPManager.put(mContext, SP_IS_FIRST_OPEN, true);
                        SPManager.put(mContext, USER_REGISTER_KEY, 0);
                        L.e("removedLicense");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                DataCleanManager.cleanInshowData(mContext);
                            }
                        }).start();
                        //假关机
//                        XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CONTROL), I2B_Control(new int[]{4,0,0,0}), new Response.BleWriteResponse() {
//                            @Override
//                            public void onResponse(int code, Void data) {
//                                if(code == XmBluetoothManager.Code.REQUEST_SUCCESS){
//                                    L.e("恢复假关机成功");
//                                }else {
//                                    L.e("恢复假关机失败");
//                                }
//                            }
//                        });
                        finish();
                        XmBluetoothManager.getInstance().disconnect(MAC, 1000);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void storeDeviceStat() {
        int register = (Integer) SPManager.get(mContext, USER_REGISTER_KEY, 0);
        mDBHelper.updateTimeStamp(USER_REGISTER_KEY, register);
        DeviceStat deviceStat = getIntent().getParcelableExtra(EXTRAS_DEVICE_STATE);
        if (null != deviceStat) {
            mDBHelper.saveCache(SP_DB_VERSION, DBHelper.DB_VERSION);
            mDBHelper.saveCache(SP_ARG_DEVICE_NAME, deviceStat.name);
            mDBHelper.saveCache(SP_ARG_MAC, deviceStat.mac);
            mDBHelper.saveCache(SP_ARG_MODEL, deviceStat.model);
            mDBHelper.saveCache(SP_ARG_USERID, deviceStat.userId);
            mDBHelper.saveCache(SP_ARG_DID, deviceStat.did);
        }
    }

    @Override
    protected void initViewOrData() {
        //性能问题 主线程中网络请求并且下载文件
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        timer = new Timer();
        barReturn = (ImageView) findViewById(R.id.title_bar_return);
        barReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackFlag = true;
                handleBack();
            }
        });
        barMore = (ImageView) findViewById(R.id.title_bar_more);
        menus = getResources().getStringArray(R.array.menu_normal_array);
        barMore.setEnabled(false);
        title = (TextView) findViewById(R.id.title_bar_title);
        subTitle = (TextView) findViewById(R.id.sub_title_bar_title);
        dragLayout = (MainDragLayout) findViewById(R.id.dragLayout);
        imgRedPoint = (ImageView) findViewById(R.id.title_bar_redpoint);
        setTitleText(mDBHelper.getCacheWithDefault(SP_ARG_DEVICE_NAME, getString(R.string.A01)));
        setPageStyle();
        barMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                L.e("barMore set onClick listener.");
                try {
                    if (XmPluginHostApi.instance().getApiLevel() < 52) {
                        MessUtil.openMenuLess52(mContext, mHostActivity, menus, startUpgrade);
                    } else {
                        MessUtil.openMenu(mContext, mHostActivity, menus, startUpgrade, UID);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        firstF = FragmentTop.getInstance();
        secondF = FragmentBottom.getInstance(mPluginPackage.packageVersion);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.firstF, firstF)
                .add(R.id.secondF, secondF)
                .commitAllowingStateLoss();
        asyncHttpManager = new AsyncHttpManager(mContext, mDBHelper);
        // first http then device
        NetWorkUtils.handleByNetState(mContext, mDBHelper.getCache(SP_ARG_MODEL), new NetWorkUtils.INetState() {
            @Override
            public void onSuccess() {
                if (mReceiver == null || asyncHttpManager == null || mDBHelper == null) {
                    return;
                }
                Configuration.getInstance().ServerHandle(new Configuration.ServerHandler2() {
                    @Override
                    public void defaultServer() {
                        checkNotChineseRegistered();
                    }

                    @Override
                    public void cnServer() {
//                        new MACAction(mContext, MAC, new IActionByState() {
//                            @Override
//                            public void onPositive() {
//
//                            }
//
//                            @Override
//                            public void onNegative() {
//                                pushMacInfo(MAC);
//                                VariableStateMgr.getInstance().setMacUploaded(mContext,MAC);
//                            }
//                        });
                        pushMacInfo(MAC);
                        if (TextUtils.equals("1263338353", UID) || TextUtils.equals("78377019", UID)) {
                            checkNotChineseRegistered();
                        } else {
                            startNormalEvent();
                        }
                    }
                });
            }

            @Override
            public void onFailure() {
                if (dragLayout == null || firstF == null || secondF == null) {
                    return;
                }
                dragLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (firstF.isAdded()) {
                            ((FragmentTop) firstF).netLost();
                        }
                        if (secondF.isAdded()) {
                            ((FragmentBottom) secondF).netLost();
                        }

                    }
                }, 1000);

            }
        });
    }

    private void checkNotChineseRegistered() {
        HttpSyncHelper.fetchArrayData(new RequestParams(
                MODEL,
                UID,
                DID,
                Constants.HttpConstant.TYPE_USER_INFO,
                USER_REGISTER_KEY,
                TimeUtil.getNowTimeSeconds(),
                1,
                INSHOW_HTTP_START_TIME,
                TimeUtil.getNowTimeSeconds()
        ), new Callback<JSONArray>() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                L.e("getUserRegisterData Success:" + jsonArray.toString());
                try {
                    JsonParser parser = new JsonParser();
                    JsonArray array = parser.parse(jsonArray.toString()).getAsJsonArray();
                    List<ResponseBase> dst = new ArrayList<>();
                    for (JsonElement obj : array) {
                        ResponseBase responseBase = AppController.getGson().fromJson(obj, ResponseBase.class);
                        dst.add(responseBase);
                    }
                    if (dst.size() > 0) {
                        ResponseBase responseBase = dst.get(0);
                        if (!TextUtils.isEmpty(responseBase.value)) {
                            HttpRegister register = AppController.getGson().fromJson(responseBase.value, HttpRegister.class);
                            if (register.time > INSHOW_HTTP_START_TIME && (Integer) SPManager.get(mContext, USER_REGISTER_KEY, 0) == register.time) {
                                L.e("getUserRegisterData Success:1");
                                startNormalEvent();
                            } else {
                                exitPlugin();
                            }
                        } else {
                            exitPlugin();
                        }
                    } else {
                        exitPlugin();
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    exitPlugin();
                }
            }

            @Override
            public void onFailure(int i, String s) {
                L.e("getUserRegisterData Error:" + s);
            }
        });
    }

    private void exitPlugin() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataCleanManager.cleanInshowData(mContext);
            }
        }).start();
        ToastUtil.showToast(mContext, getString(R.string.A10));
        SPManager.put(mContext, USER_REGISTER_KEY, 0);
        finish();
    }

    private void startNormalEvent() {
        asyncHttpManager.startHttpSync(new ITerminatedListener() {
            @Override
            public void onAsyncFinished() {
                L.e(" ========= HttpTasks Complete！！ =========");
                mDBHelper.updateTimeStamp(HTTP_SYNC_KEY, TimeUtil.getNowTimeSeconds());
            }
        });
        checkBleAndConn();
        mReceiver.setNameChangedListener(new onDeviceNameChangeListener() {
            @Override
            public void onChanged(String name) {
                setTitleText(name);
            }
        });
    }

    /**
     * 返回处理
     */
    private void handleBack() {
        if (isScanning) {
            XmBluetoothManager.getInstance().stopScan();
        }
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        XmBluetoothManager.getInstance().disconnect(MAC, 0);
        if ((isScanning || neverResponse) && !isDeviceConnected()) {//非连接状态 扫描或者连接中
            if (isScanning) isScanning = false;
            final InShowProgressDialog dialogInstance = new InShowProgressDialog(mContext);
            dialogInstance.setCancelable(false);
            dialogInstance.setCanceledOnTouchOutside(false);
            dialogInstance.setIndeterminate(false);
            dialogInstance.setMessage(getString(R.string.exit_wait));
            dialogInstance.setMax(3000);
            dialogInstance.show();
            final int nowStart = (int) currentTimeMillis();
            if (null != timer) {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int delta = (int) (currentTimeMillis() - nowStart);
                                if (delta <= 3000) {
                                    dialogInstance.setProgress(delta);
                                } else {
                                    finish();
                                }
                            }
                        });
                    }
                }, 100, 100);
            }
        } else {
            finish();
        }
    }

    /**
     * 检查蓝牙是否打开，并连接蓝牙
     */
    private void checkBleAndConn() {
        if (XmBluetoothManager.getInstance().isBluetoothOpen()) {
            scanAndConnect();
        } else {
            try {
                new MLAlertDialog.Builder(mContext)
                        .setMessage(getString(R.string.open_ble_tip))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.allow), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                XmBluetoothManager.getInstance().openBluetoothSilently();
                                openBleAndScanCon();
                            }
                        })
                        .setNegativeButton(getString(R.string.reject), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                connectFailCallback();
                            }
                        })
                        .show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void openBleAndScanCon() {
        title.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (XmBluetoothManager.getInstance().isBluetoothOpen()) {
                    scanAndConnect();
                    openScanTryCount++;
                } else if (openScanTryCount <= TRY_LIMIT) {
                    openBleAndScanCon();
                } else {
                    openScanTryCount = 0;
                }
            }
        }, 2000);
    }

    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() {
        int delayPercent = 86; //
        int currentPercent = 0;

        @Override
        public void onDeviceDisconnecting(String deviceAddress) {
            super.onDeviceDisconnecting(deviceAddress);
            L.e("DfuProgressListener onDeviceDisconnecting:" + deviceAddress);
            if (currentPercent > 0 && currentPercent < 100) {
                L.e("DfuProgressListener onDeviceDisconnecting percent > 0 && <100  ===> PAGE_UPGRADE_FAILED ");
                showFail();
            }

        }

        @Override
        public void onDeviceConnecting(final String deviceAddress) {
            L.e("DfuProgressListener onDeviceConnecting:" + deviceAddress);
            setOnClickListenersNull();
        }

        @Override
        public void onDfuCompleted(final String deviceAddress) {
            L.e("DfuProgressListener onDfuCompleted:" + deviceAddress);
            SPManager.put(mContext, SP_ARG_FIRMWARE_VERSION, mUpdateInfo.version);
            SPManager.put(mContext, SP_ARG_DFU_MODE, false);
        }

        @Override
        public void onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) {
            L.e("DfuProgressListener onProgressChanged:" + percent);
            currentPercent = percent;
            if (percent > 1 && percent < 100) {
                showUpgradeProgress((int) (percent * 0.85));
            } else if (percent == 100) {
                mCurrentV = mUpdateInfo.version;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (delayPercent == 100) {
                            L.e("onDfuCompleted PAGE_UPGRADE_SUCCESS");
                            try {
                                new MLAlertDialog.Builder(mContext)
                                        .setCancelable(false)
                                        .setMessage(getString(R.string.dfu_success_exit))
                                        .setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                                AppController.getInstance().exit();
                                            }
                                        })
                                        .show();
                                showSuccess();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            L.e("onDfuCompleted delayPercent = " + delayPercent);
                            delayPercent++;
                            showUpgradeProgress(delayPercent);
                            mHandler.postDelayed(this, 800);
                        }
                    }
                }, 800);
            }
        }

        @Override
        public void onError(final String deviceAddress, final int error, final int errorType, final String message) {
            L.e("DfuProgressListener onError:" + message);
            showFail();
        }

        @Override
        public void onDeviceDisconnected(String deviceAddress) {
            super.onDeviceDisconnected(deviceAddress);
            L.e("DfuProgressListener onDeviceDisconnected:" + deviceAddress);
            if (currentPercent < 100) {
                showFail();
            }
        }

    };

    /**
     * 固件版本是否异常
     *
     * @return
     */
    private boolean forceUpgrade() {
        return TextUtils.equals(mCurrentV, DFU_ERROR_VERSION);
    }


    /**
     * 由于刚开始用户可能处于蓝牙关闭状态
     * 扫描并连接设备
     */
    private void scanAndConnect() {
        XmBluetoothManager.getInstance().startScan(SCAN_INTERVAL, SCAN_BLE, new XmBluetoothManager.BluetoothSearchResponse() {
            @Override
            public void onSearchStarted() {
                L.e(" onSearchStarted() For: " + MAC.toUpperCase());
                isScanning = true;
                hasScanFound = false;
            }

            @Override
            public void onDeviceFounded(XmBluetoothDevice xmBluetoothDevice) {
                L.e(" onDeviceFounded() " + xmBluetoothDevice.getAddress());
                if (MAC.toUpperCase().equals(xmBluetoothDevice.getAddress().toUpperCase()) && !hasScanFound) {
                    L.e("onDeviceFounded() equals MAC " + xmBluetoothDevice.getAddress());
                    XmBluetoothManager.getInstance().stopScan();//扫描到就立刻停止扫描
                    isScanning = false;
                    if (!hasScanFound) {
                        connectDevice();
                    }
                    hasScanFound = true;
                }
            }

            @Override
            public void onSearchStopped() {//搜索结束调用
                L.e(" onSearchStopped() ");
                if (!hasScanFound) {
                    connectDevice();
                }
                isScanning = false;
                hasScanFound = false;
            }

            @Override
            public void onSearchCanceled() {//搜索任务被中断
                L.e(" onSearchCanceled() ");
                if (!hasScanFound) {
                    connectDevice();
                }
                hasScanFound = false;
            }
        });
    }

    /**
     * 连接蓝牙设备
     */
    private void connectDevice() {
        XmBluetoothManager.getInstance().secureConnect(MAC, new Response.BleConnectResponse() {
            @Override
            public void onResponse(int code, Bundle data) {
                neverResponse = false;//连接有响应
                L.e("connectDevice return code:" + code);
                if (code == XmBluetoothManager.Code.REQUEST_SUCCESS) {
                    L.e("connectDevice Success");
                    connectSuccessCallback();
                } else if (code == TOKEN_NOT_MATCHED) {
                    L.e("connectDevice removeToken");
                    XmBluetoothManager.getInstance().removeToken(MAC);
                    connectDevice();
                } else if (code == REQUEST_TIMEDOUT || code == CONNECTION_NOT_READY) {
                    showTimeoutDlg();
                } else {
                    connectFailCallback();
                }
            }
        });
    }

    private void showTimeoutDlg() {
        try {
            final MLAlertDialog.Builder builder = new MLAlertDialog.Builder(mContext);
            builder.setCancelable(false)
                    .setTitle(getString(R.string.connect_no_sure))
                    .setMessageGravity(Gravity.LEFT)
                    .setMessage(getString(R.string.connect_timeout_msg))
                    .setPositiveButton(getString(R.string.connect_know), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            builder.setMessageGravity(Gravity.CENTER);
                            EventBus.getDefault().post(new ChangeUI(CONNECT_AGAIN));
                            scanAndConnect();
                        }
                    })
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接成功回调
     */
    private void connectSuccessCallback() {
        dragLayout.setAllowMove(true);
        barMore.setEnabled(true);
        XmBluetoothManager.getInstance().read(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_SYNC_CURRENT_TIME), new Response.BleReadResponse() {
            @Override
            public void onResponse(int code, byte[] bytes) {
                if (code == XmBluetoothManager.Code.REQUEST_SUCCESS) {
                    pushTimeStampInfo(mContext, TimeUtil.getNowTimeSeconds(mDBHelper.getSettingZone()), BleManager.B2I_getTime(bytes) + TimeUtil.getWatchSysStartTimeSecs());
                    readFirmwareVersion(upgradeListener);
                } else {
                    L.e("CHARACTERISTIC_SYNC_CURRENT_TIME  Read Error.");
                    connectFailCallback();
                }
            }
        });
    }

    /**
     * 连接失败
     */
    private void connectFailCallback() {
        SPManager.put(mContext, SP_ARG_BLUETOOTH_CONNECTED, false);
        ((FragmentTop) firstF).renderFail();
        ((FragmentBottom) secondF).renderFail();
    }

    private IUpgrade upgradeListener = new IUpgrade() {
        @Override
        public void ignoreUpgrade() {
            if (hasOpenAsync) {
                executeDeviceSync();
            }
            XmBluetoothManager.getInstance().read(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(BATTERY_LEVEL), new Response.BleReadResponse() {
                @Override
                public void onResponse(int code, byte[] bytes) {
                    if (code == XmBluetoothManager.Code.REQUEST_SUCCESS) {
                        if (null != bytes) {
                            HttpBatteryLevel bean = new HttpBatteryLevel();
                            if (bytes.length == 2) {
                                bean.level = B2I_getBatteryLevel1(bytes);
                                bean.peak_level = 0;
                                bean.valley_level = 0;
                            } else if (bytes.length == 4) {
                                bean.level = 0;
                                bean.peak_level = B2I_getBatteryLevel2(bytes)[0];
                                bean.valley_level = B2I_getBatteryLevel2(bytes)[1];
                            }
                            pushBatteryLevelInfo(mContext, bean);
                        }
                    } else {
                        L.e("BATTERY_LEVEL Read Error.");
                    }
                }
            });
        }

        @Override
        public void upgradeImmediately() {
            Map<String, Object> extras = new HashMap<>();
            extras.put("cv", mCurrentV);//当前固件版本
            extras.put("lv", mUpdateInfo.version);//最新固件版本
            extras.put("url", mUpdateInfo.url);//最新固件版本
            switchTo(ProcessingAct.class, extras);
        }
    };

    private void readFirmwareVersion(final IUpgrade listener) {
        //      读取设备固件版本
        XmBluetoothManager.getInstance().getBluetoothFirmwareVersion(MAC, new Response.BleReadFirmwareVersionResponse() {
            @Override
            public void onResponse(int code, String version) {
                // version类似1.0.3_2001
                if (!TextUtils.isEmpty(version)) {
                    mCurrentV = version;
                    if (!TextUtils.equals((String) SPManager.get(mContext, SP_DEBUG_DFU, null), version)) {
                        pushDfuInfo(mContext, mCurrentV);
                    }
                    SPManager.put(mContext, SP_ARG_FIRMWARE_VERSION, mCurrentV);
                    mDBHelper.saveCache(SP_ARG_FIRMWARE_VERSION, mCurrentV);
                    L.e("固件当前版本:" + mCurrentV);
                }
                XmPluginHostApi.instance().getBluetoothFirmwareUpdateInfo("inshow.watch.w1", new Callback<BtFirmwareUpdateInfo>() {
                    @Override
                    public void onSuccess(BtFirmwareUpdateInfo btFirmwareUpdateInfo) {
                        if (btFirmwareUpdateInfo != null && !TextUtils.isEmpty(mCurrentV)) {
                            mUpdateInfo = btFirmwareUpdateInfo;
                            imgRedPoint.setVisibility(mCurrentV.equals(mUpdateInfo.version) ? View.GONE : View.VISIBLE);
                            L.e("固件最新版本:" + mUpdateInfo.version + ",描述:" + mUpdateInfo.changeLog + ",下载地址:" + mUpdateInfo.url);
                            if (forceUpgrade()) {
                                L.e("forceUpgrade");
                                listener.upgradeImmediately();
                            } else {
                                L.e("need not forceUpgrade");
                                listener.ignoreUpgrade();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int error, String msg) {
                        L.e("getBluetoothFirmwareUpdateInfo error=" + error + ",error msg=" + msg);
                        imgRedPoint.setVisibility(View.GONE);
                        mUpdateInfo = new BtFirmwareUpdateInfo();
                    }
                });
            }
        });
    }


    /**
     * 执行设备同步
     * 必须先要进行绑定标志位等同步 然后判断同步间隔 大于2h就更新
     */
    private void executeDeviceSync() {
        L.e("executeDeviceSync Start");
        ScheduledExecutorService deviceSyncPool = new ScheduledThreadPoolExecutor(2);
        deviceSyncPool.schedule(new DeviceTask(mContext, mDBHelper, new DeviceTask.IRecoveryListener() {
            @Override
            public void switchAct(int[] i) {
                Map<String, Object> map = new HashMap<>();
                map.put(RecoveryAct.INT0, i[0]);
                map.put(RecoveryAct.INT1, i[1]);
                map.put(RecoveryAct.INT2, i[2]);
                map.put(RecoveryAct.INT3, i[3]);
                switchTo(RecoveryAct.class, map);
            }
        }, new IUtcTimeLoadComplete() {
            @Override
            public void onFinish() {
                SyncDeviceHelper.syncDeviceBattery(MAC, new SyncDeviceHelper.BtCallback() {
                    @Override
                    public void onBtResponse(byte[] bytes) {
                        if (firstF.isAdded()) {
                            ((FragmentTop) firstF).renderSuccess();
                        }
                        if (secondF.isAdded()) {
                            ((FragmentBottom) secondF).renderSuccess();
                        }
                        //非大陆地区 隐私
                        Configuration.getInstance().ServerHandle(new Configuration.ServerHandler2() {
                            @Override
                            public void defaultServer() {
                                L.e("checkIsFirstOpen:");
                                MessUtil.checkIsFirstOpen(mContext, mHostActivity);
                            }

                            @Override
                            public void cnServer() {
                                if (TextUtils.equals("1263338353", UID) || TextUtils.equals("78377019", UID)) {
                                    MessUtil.checkIsFirstOpen(mContext, mHostActivity);
                                }
                            }
                        });
                        mReceiver.setStepChangeListener((FragmentBottom) secondF);
                        mPowerConsumption = BleManager.getPowerConsumption(bytes);
                        mBatteryLevel = mPowerConsumption[0];
                        mHaveUsedTime = mPowerConsumption[1];
                        setSubTitleText(mBatteryLevel > BATTERY_LIMIT ? getString(R.string.power_enough) : getString(R.string.power_lower_tip));
                        pushWatchState(mContext, new HttpWatchState(mPowerConsumption[2], mPowerConsumption[3]));
                        if (mBatteryLevel < BATTERY_LIMIT) {
                            setFlickerAnimation(subTitle);
                            batteryHasReaded = true;
                            LowPowerManager.getInstance().setLowPower(true);
//                            showLowPowerDialog();
                            LowPowerManager.getInstance().startFlowControl(mContext, mDBHelper.isNeedCloseVibration(), new LowPowerManager.IFlowControl() {
                                @Override
                                public void clearAllTips() {
                                    if (mDBHelper.closeAllVibration()) {//关闭所有振动设置
                                        L.e(" clear vibrations start");
                                        mDBHelper.updateTimeStamp(VIP_KEY, TimeUtil.getNowTimeSeconds());
                                        mDBHelper.updateTimeStamp(NORMAL_ALARM_KEY, TimeUtil.getNowTimeSeconds());
                                        mDBHelper.updateTimeStamp(INTERVAL_ALARM_KEY, TimeUtil.getNowTimeSeconds());
                                        asyncHttpManager = new AsyncHttpManager(mContext, mDBHelper);
                                        asyncHttpManager.startHttpSync(new ITerminatedListener() {
                                            @Override
                                            public void onAsyncFinished() {
                                                L.e(" ========= HttpTasks Complete！！ =========");
                                                mDBHelper.updateTimeStamp(HTTP_SYNC_KEY, TimeUtil.getNowTimeSeconds());
                                            }
                                        });

                                        //同步设备端
                                        SyncDeviceHelper.changeInComingAlertState(MAC,false);
                                        SyncDeviceHelper.syncClearInterval(MAC);
                                        SyncDeviceHelper.syncClearDeviceAlarm(MAC);

                                        MessUtil.bindOrNot(mContext, MAC, false);
                                        EventBus.getDefault().post(new ChangeUI(RENDER_AGAIN));//刷新frg
                                        L.e(" clear  vibrations end");

                                    } else {
                                        L.e(" clear  vibrations ：error from db");
                                    }
                                }

                                @Override
                                public void startSync() {
                                    //do nothing because sync completely
                                }
                            });
                        } else {
                            LowPowerManager.getInstance().setLowPower(false);
                        }
                        SPManager.put(mContext, SP_ARG_BLUETOOTH_CONNECTED, true);
                        subTitle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setSubTitleText(getUsedTime(mContext, mHaveUsedTime));
                                subTitle.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setSubTitleText(mBatteryLevel > BATTERY_LIMIT ? getString(R.string.power_enough) : getString(R.string.power_lower_tip));
                                    }
                                }, 3000);
                            }
                        });
                    }
                });
            }
        }), 0, TimeUnit.MILLISECONDS);
        deviceSyncPool.shutdown();
    }

    private void showLowPowerDialog() {
        if (TextUtils.equals((String) SPManager.get(mContext, SP_DB_IGNORE_LOW_POWER_TIP, OFF), OFF) && mDBHelper.isNeedCloseVibration()) {//
            try {
                new MLAlertDialog.Builder(mContext)
                        .setCancelable(false)
                        .setTitle(getString(R.string.low_power_title))
                        .setMessage(getString(R.string.low_power_msg))
                        .setPositiveButton(getString(R.string.close_vibration), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SPManager.put(mContext, SP_DB_IGNORE_LOW_POWER_TIP, OFF);
                                if (mDBHelper.closeAllVibration()) {//关闭所有振动设置
                                    mDBHelper.updateTimeStamp(VIP_KEY, TimeUtil.getNowTimeSeconds());
                                    mDBHelper.updateTimeStamp(NORMAL_ALARM_KEY, TimeUtil.getNowTimeSeconds());
                                    mDBHelper.updateTimeStamp(INTERVAL_ALARM_KEY, TimeUtil.getNowTimeSeconds());
                                    asyncHttpManager = new AsyncHttpManager(mContext, mDBHelper);
                                    asyncHttpManager.startHttpSync(new ITerminatedListener() {
                                        @Override
                                        public void onAsyncFinished() {
                                            L.e(" ========= HttpTasks Complete！！ =========");
                                            mDBHelper.updateTimeStamp(HTTP_SYNC_KEY, TimeUtil.getNowTimeSeconds());
                                        }
                                    });//同步网络端


                                    //同步设备端
                                    SyncDeviceHelper.syncClearDeviceVip(MAC);
                                    SyncDeviceHelper.syncClearInterval(MAC);
                                    SyncDeviceHelper.syncClearDeviceAlarm(MAC);

                                    MessUtil.bindOrNot(mContext, MAC, false);
                                    EventBus.getDefault().post(new ChangeUI(RENDER_AGAIN));//刷新frg
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.ignore), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SPManager.put(mContext, SP_DB_IGNORE_LOW_POWER_TIP, ON);
                            }
                        })
                        .show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (resumeFromOtherPage && batteryHasReaded && mBatteryLevel < BATTERY_LIMIT) {
            showLowPowerDialog();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        resumeFromOtherPage = true;
    }

    /**
     * dfu set null
     */
    private void setOnClickListenersNull() {
        findViewById(R.id.sub_title_bar_title).setOnClickListener(null);
        findViewById(R.id.title_bar_more).setOnClickListener(null);
    }


    private void setPageStyle() {
        title.setTextColor(ContextCompat.getColor(mContext, R.color.white_90_transparent));
        subTitle.setTextColor(ContextCompat.getColor(mContext, R.color.white_50_transparent));
        barReturn.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.std_tittlebar_main_device_back_white));
        barMore.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.std_tittlebar_main_device_more_unable));
        llBasic.setBackgroundColor(ContextCompat.getColor(mContext, R.color.main_bg));
        flContent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.main_bg));
    }

    @Override
    public void onDestroy() {
        if (isScanning) {
            XmBluetoothManager.getInstance().stopScan();
            isScanning = false;
        }
        if (isDeviceConnected()) {
            XmBluetoothManager.getInstance().disconnect(MAC);
        }
        if (null != asyncHttpManager) {
            asyncHttpManager.releaseHttpAsyncManager();
        }
        if (null != timer) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        super.onDestroy();
    }

    /**
     * 设备是否连接
     *
     * @return
     */
    private boolean isDeviceConnected() {
        return XmBluetoothManager.getInstance().getConnectStatus(MAC) == XmBluetoothManager.STATE_CONNECTED;
    }


    private final BleUpgrader startUpgrade = new BleUpgrader() {
        private boolean hasTip = false;// prevent tip repeat

        @Override
        public String getCurrentVersion() throws RemoteException {
            return mCurrentV;
        }

        @Override
        public String getLatestVersion() throws RemoteException {
            return mUpdateInfo.version;
        }

        @Override
        public String getUpgradeDescription() throws RemoteException {
            return mUpdateInfo.changeLog;
        }

        @Override
        public void startUpgrade() throws RemoteException {
//            if(true) {
//                Toast.makeText(mContext, "sss", Toast.LENGTH_LONG).show();
//                return;
//            }
            if (!NetWorkUtils.isNetworkConnected(mContext)) {
                try {
                    if (!hasTip) {
                        new MLAlertDialog.Builder(mContext)
                                .setCancelable(false)
                                .setMessage(getString(R.string.dfu_file_download_fail))
                                .setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                                .show();
                        hasTip = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            //低电不升级去掉
//            L.e("mBatteryLevel:" + mBatteryLevel);
//            if (mBatteryLevel < BATTERY_LIMIT) {
//                L.e("mBatteryLevel < BATTERY_LIMIT");
//                ToastUtil.showToastNoRepeat(mContext, getString(R.string.low_power_reject_upgrade));
//                return;
//            }
            showUpgradeProgress(0);
            XmPluginHostApi.instance().downloadBleFirmware(mUpdateInfo.url, new Response.BleUpgradeResponse() {
                @Override
                public void onProgress(int progress) {
                }

                @Override
                public void onResponse(int code, final String filePath) {
                    L.e("downloadBleFirmware filePath:" + filePath);
                    L.e("startUpgrade onResponse code:" + code);
                    if (XmBluetoothManager.Code.REQUEST_SUCCESS == code) {
                        mDownLoadFilePath = filePath;
                        dragLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showUpgradeProgress(1);
                            }
                        }, 1000);
                        intoDfuModeUpgrade();
                    }
                }
            });

        }

        @Override
        public void onActivityCreated(Bundle bundle) throws RemoteException {
            if (!TextUtils.equals(getCurrentVersion(), getLatestVersion())) {
                startUpgrade.showPage(XmBluetoothManager.PAGE_CURRENT_DEPRECATED, null);
            } else {
                startUpgrade.showPage(XmBluetoothManager.PAGE_CURRENT_LATEST, null);
            }
        }

        @Override
        public boolean onPreEnterActivity(Bundle bundle) throws RemoteException {
            return false;
        }
    };


    /**
     * 写入05进入dfu
     */
    public void intoDfuModeUpgrade() {
        L.e("intoDfuModeUpgrade");
        SyncDeviceHelper.syncSetControlFlag(MAC, new SyncDeviceHelper.BtCallback() {
            @Override
            public void onBtResponse(byte[] bytes) {
                startOtaInDfuMode();
            }
        }, new int[]{5, 0, 0, 0});
    }

    public void startOtaInDfuMode() {
        startDfuDaemon();
        XmBluetoothManager.getInstance().disconnect(MAC);
        SPManager.put(mContext, SP_ARG_DFU_MODE, true);
        SPManager.put(mContext, SP_ARG_BLUETOOTH_CONNECTED, false);
        L.e("getIncrementedAddress(MAC)：" + getIncrementedAddress(MAC));
        final DfuServiceInitiator starter = new DfuServiceInitiator(getIncrementedAddress(MAC))
                .setDisableNotification(true)//need't Notification Act
                .setKeepBond(false)
                .setDeviceName("DFU")
                .setZip(mDownLoadFilePath);
        timeTask = new DfuBaseTimerTask();
        timeTask.context = mContext;
        timeTask.setInshowDfuProgressListner(mDfuProgressListener);
        timeTask.setReconnectListener(new IReconnect() {
            @Override
            public void restartTimer() {
                L.e("Connect Failed ,So restartTimer()!!!!");
                dragLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        timeTask.setReconnectListener(null);//只是重试一次
                        startUpgradeTask(starter, timeTask);
                    }
                }, 0);//延时1s重启任务
            }
        });
        startUpgradeTask(starter, timeTask);
    }


    /**
     * 开启下载任务
     *
     * @param starter
     * @param firstTask
     */
    public void startUpgradeTask(final DfuServiceInitiator starter, final DfuBaseTimerTask firstTask) {
        dfuPool = new ScheduledThreadPoolExecutor(4);
        XmBluetoothManager.getInstance().startScan(60 * 1000, SCAN_BLE, new XmBluetoothManager.BluetoothSearchResponse() {
            @Override
            public void onSearchStarted() {
            }

            @Override
            public void onDeviceFounded(XmBluetoothDevice xmBluetoothDevice) {
                L.e("DFU Scan DeviceFounded:" + xmBluetoothDevice.getAddress().toUpperCase());
                if (getIncrementedAddress(MAC).toUpperCase().equals(xmBluetoothDevice.getAddress().toUpperCase())) {
                    L.e("DFU Scan Find DFU MAC:" + xmBluetoothDevice.getAddress().toUpperCase());
                    XmBluetoothManager.getInstance().stopScan();
                    hasScanFound = true;
                    starter.start(mContext, dfuPool, firstTask);
                }
            }

            @Override
            public void onSearchStopped() {
                L.e("DFU Scan onSearchStopped");
                if (!hasScanFound) {
                    starter.start(mContext, dfuPool, firstTask);
                }
                hasScanFound = false;
            }

            @Override
            public void onSearchCanceled() {
                hasScanFound = false;
            }
        });
    }

    /**
     * 显示更新进度
     *
     * @param percent
     */
    private void showUpgradeProgress(int percent) {
        Bundle data = new Bundle();
        data.putInt(XmBluetoothManager.EXTRA_UPGRADE_PROCESS, percent);
        startUpgrade.showPage(XmBluetoothManager.PAGE_UPGRADING, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handleBack();
    }
}
