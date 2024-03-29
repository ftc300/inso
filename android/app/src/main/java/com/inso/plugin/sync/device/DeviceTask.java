package com.inso.plugin.sync.device;

import android.content.Context;

import com.inso.core.BleMgr;
import com.inso.plugin.act.user.WatchUserInfoHelper;
import com.inso.plugin.dao.AlarmDao;
import com.inso.plugin.dao.IntervalDao;
import com.inso.plugin.dao.PreferCitiesDao;
import com.inso.plugin.dao.StepDao;
import com.inso.plugin.dao.VibrationDao;
import com.inso.plugin.event.ChangeUI;
import com.inso.plugin.manager.BleManager;
import com.inso.plugin.manager.SPManager;
import com.inso.plugin.model.VipEntity;
import com.inso.plugin.provider.DBHelper;
import com.inso.plugin.sync.IUtcTimeLoadComplete;
import com.inso.plugin.sync.SyncDeviceHelper;
import com.inso.plugin.tools.L;
import com.inso.plugin.tools.Rom;
import com.inso.plugin.tools.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.inso.plugin.event.ChangeUI.SYNC_BIND_RENDER;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.CHARACTERISTIC_HISTORY_STEP;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;
import static com.inso.plugin.tools.Constants.ON;
import static com.inso.plugin.tools.Constants.SystemConstant.SP_ARG_MAC;
import static com.inso.plugin.tools.Constants.SystemConstant.SP_ARG_MODEL;
import static com.inso.plugin.tools.Constants.SystemConstant.SP_DEBUG_ARG_LOCAL_TIME;
import static com.inso.plugin.tools.Constants.SystemConstant.SP_INCOMING_SWITCH;
import static com.inso.plugin.tools.Constants.TimeStamp.DEVICE_SYNC_KEY;
import static com.inso.plugin.tools.Constants.TimeStamp.STEPS_KEY;
import static com.inso.plugin.tools.Constants.deltaTimeFromUTC;

/**
 * Created by chendong on 2017/5/16.
 */
public class DeviceTask implements Runnable {
    private Context context;
    private String MAC;
    private String TAG = "DeviceTask";
    private DBHelper mDBHelper;
    private WatchUserInfoHelper userHelper;
    private List<StepDao> stepDataSource = new ArrayList<>();
    private final int DEVICE_SYNC_INTERVAL = 0;
    private IRecoveryListener listener;
    private List<AlarmDao> alarmSrc;
    private List<PreferCitiesDao> preferCitySrc;
    private IntervalDao intervalDao;
    private VibrationDao vibrationDao;
    private List<VipEntity> vipSrc;
    private String MODEL;
    private int count;
    private int start;
    private int end;
    private IUtcTimeLoadComplete utcListener;
    private int DELTA_ADD_TIME = 2;//写给手表的时间可能会耗时2s
    private final int BATTERY_LIMIT = 30;

    public SyncDeviceHelper.BtCallback callback = new SyncDeviceHelper.BtCallback() {

        @Override
        public void onBtResponse(byte[] bytes) {
            int[] i = BleManager.B2I_getHistoryStep(bytes);
            start = i[1];
            end = i[2];
            count = i[3];
            if (i[0] != -1 && start > getStepStamp()) {
                stepDataSource.add(new StepDao(start, end, count, end - start,
                        userHelper.getDistance(count),
                        userHelper.getKCal(count),
                        TimeUtil.getMD(start),
                        TimeUtil.getWeekBegEnd(start),
                        TimeUtil.getMon(start),
                        TimeUtil.getYear(start)));
                SyncDeviceHelper.syncDeviceStepHistory(MAC, callback);
            } else if (stepDataSource.size() > 0) {
                mDBHelper.addSteps(stepDataSource);
            }
        }
    };

    public DeviceTask(Context context, DBHelper dbHelper, IRecoveryListener listener, IUtcTimeLoadComplete utcListener) {
        this.context = context;
        mDBHelper = dbHelper;
        MODEL = mDBHelper.getCache(SP_ARG_MODEL);
        MAC = mDBHelper.getCache(SP_ARG_MAC);
        userHelper = new WatchUserInfoHelper(context);
        this.listener = listener;
        this.utcListener = utcListener;
    }

    @Override
    public void run() {
        alarmSrc = mDBHelper.getAllAlarm();
        preferCitySrc = mDBHelper.getAllPreferCities();
        intervalDao = mDBHelper.getInterval();
        vipSrc = mDBHelper.getVipContact();
        vibrationDao = mDBHelper.getVibrationInfo();
        try {

            //<editor-fold desc="StepHistory">
            BleMgr.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_HISTORY_STEP), new byte[]{0, 0, 0, 0}, new BleMgr.IWriteResponse() {
                @Override
                public void onSuccess() {
                    L.d("CHARACTERISTIC_HISTORY_STEP:write suc");
                    SyncDeviceHelper.syncDeviceStepHistory(MAC, callback);
                }

                @Override
                public void onFail() {
                    L.d("CHARACTERISTIC_HISTORY_STEP:write fail");
                }
            });
            //</editor-fold>

            //<editor-fold desc="Flags">
            //notify is android device
//            SyncDeviceHelper.syncSetControlFlag(MAC, new SyncDeviceHelper.BtCallback() {
//                @Override
//                public void onBtResponse(byte[] bytes) {
//                    if (null != utcListener) utcListener.onFinish(); //连接成功就更新页面
//                    writeDataToWatch();
//                }
//            }, new int[]{6, 0, 0, Rom.isMIUI() ? 2 : 3});
//            XmBluetoothManager.getInstance().read(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CONTROL), new Response.BleReadResponse() {
//                @Override
//                public void onResponse(int code, byte[] bytes) {
//                    if (code == XmBluetoothManager.Code.REQUEST_SUCCESS) {
//                        if (null != utcListener) utcListener.onFinish(); //连接成功就更新页面
                        //notify is android device
//                        int[] i = XmBluetoothManager.getControl(bytes);
//                        SyncDeviceHelper.syncSetControlFlag(MAC, new SyncDeviceHelper.BtCallback() {
//                            @Override
//                            public void onBtResponse(byte[] bytes) {
//                                writeDataToWatch();
//                            }
//                        }, new int[]{6, 0, 0, Rom.isMiui() ? 2 : 3});
//                        SyncDeviceHelper.syncSetControlFlag(MAC, new SyncDeviceHelper.BtCallback() {
//                            @Override
//                            public void onBtResponse(byte[] bytes) {
//                                writeDataToWatch();
//                            }
//                        }, new int[]{i[0], 2, i[2], Rom.isMiui() ? 2 : 3});
//                    } else {
//                        L.e(TAG + "==>control:error");
//                    }
//                }
//            });
            //</editor-fold>
        } catch (Exception e) {
            L.e(e.getMessage());
            e.printStackTrace();
        } finally {
            L.e("===========DeviceTasks complete!==========");
            mDBHelper.updateTimeStamp(DEVICE_SYNC_KEY, TimeUtil.getNowTimeSeconds());
        }
    }

    /**
     * update device data
     */
    private void writeDataToWatch() {
        if ((Boolean) SPManager.get(context, SP_DEBUG_ARG_LOCAL_TIME, false)) {
            deltaTimeFromUTC = 0;
        }
        startSync();
        EventBus.getDefault().post(new ChangeUI(SYNC_BIND_RENDER));
    }

    private void startSync() {
        int time = TimeUtil.getNowTimeSeconds(mDBHelper.getSettingZone()) - TimeUtil.getWatchSysStartTimeSecs();
        L.e("syncDevicePreferTime:" + (TimeUtil.getNowTimeSeconds(mDBHelper.getSettingZone())));
        SyncDeviceHelper.syncDevicePreferTime(MAC, time + 2, new SyncDeviceHelper.BtCallback() {
            @Override
            public void onBtResponse(byte[] bytes) {
                SyncDeviceHelper.syncDeviceBattery(MAC, new SyncDeviceHelper.BtCallback() {
                    @Override
                    public void onBtResponse(byte[] bytes) {
                        int[] mPowerConsumption = BleManager.getPowerConsumption(bytes);
                        if (mPowerConsumption[0] >= BATTERY_LIMIT) {//电量充足才同步
                            SyncDeviceHelper.syncDeviceInterval(MAC, intervalDao, mDBHelper);
                            //同步完时间后再做其他事 不然可能会无效
                            SyncDeviceHelper.syncClearDeviceAlarm(MAC);
                            if (alarmSrc.size() > 0) {
                                for (AlarmDao item : alarmSrc) {
                                    SyncDeviceHelper.syncDeviceAlarm(MAC, item);
                                }
                            }

                            if (Rom.isMIUI()) {
                                SyncDeviceHelper.changeInComingAlertState(MAC,(Boolean) SPManager.get(context,SP_INCOMING_SWITCH,false));
                            }
                            SyncDeviceHelper.syncDeviceVibration(MAC, vibrationDao);
                        }
                    }
                });
            }
        });
    }

    private  int getStepStamp() {
        return mDBHelper.getKeyTimeStamp(STEPS_KEY);
    }

    private int getDeviceSyncStamp() {
        return mDBHelper.getKeyTimeStamp(DEVICE_SYNC_KEY);
    }

    private boolean hasSettingData() {
        return alarmSrc.size() > 0 || preferCitySrc.size() > 1 || ON.equals(intervalDao.status) || vipSrc.size() > 0;
    }

    public interface IRecoveryListener {
        void switchAct(int[] i);
    }

}

