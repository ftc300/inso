package com.inshow.watch.android.act.user;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.dao.AlarmDao;
import com.inshow.watch.android.dao.IntervalDao;
import com.inshow.watch.android.dao.PreferCitiesDao;
import com.inshow.watch.android.dao.VibrationDao;
import com.inshow.watch.android.event.ChangeUI;
import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.model.VipEntity;
import com.inshow.watch.android.sync.SyncDeviceHelper;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.bean.HttpUTCRes;
import com.inshow.watch.android.tools.DateUtil;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.Rom;
import com.inshow.watch.android.tools.TextStyle;
import com.inshow.watch.android.tools.TimeUtil;
import com.xiaomi.smarthome.device.api.Callback;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.TimeZone;

import static com.inshow.watch.android.event.ChangeUI.RENDER_AGAIN;
import static com.inshow.watch.android.tools.Constants.ON;
import static com.inshow.watch.android.tools.Constants.TimeStamp.DEVICE_SYNC_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.INTERVAL_ALARM_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.NORMAL_ALARM_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.VIBRATE_SETTING_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.VIP_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.WORLD_CITY_KEY;

/**
 * Created by chendong on 2017/4/17.
 */
public class RecoveryAct extends BasicAct {

    private LinearLayout container;
    private List<AlarmDao> alarmSrc;
    private List<PreferCitiesDao> preferCitySrc;
    private List<VipEntity> vipSrc ;
    private IntervalDao intervalDao;
    private VibrationDao vibrationDao;
    public static final String INT0 = "INT0";
    public static final String INT1 = "INT1";
    public static final String INT2 = "INT2";
    public static final String INT3 = "INT3";
    private TextStyle mTs;
    private int[] i;

    @Override
    protected int getContentRes() {
        return R.layout.watch_act_recovery;
    }

    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    @Override
    protected void initViewOrData() {
        mHostActivity.enableBlackTranslucentStatus();
        alarmSrc = mDBHelper.getAllAlarm();
        preferCitySrc = mDBHelper.getAllPreferCities();
        intervalDao = mDBHelper.getInterval();
        vibrationDao = mDBHelper.getVibrationInfo();
        vipSrc = mDBHelper.getVipContact();
        container = (LinearLayout) findViewById(R.id.container);
        mTs = new TextStyle(getResources().getColor(R.color.watch_blue), 21);
        i = new int[]{getIntent().getIntExtra(INT0, 0),
                            getIntent().getIntExtra(INT1, 0),
                            getIntent().getIntExtra(INT2, 0),
                            getIntent().getIntExtra(INT3, 0)};
        if(alarmSrc.size()>0) {
            addContainer(alarmSrc.size(), getString(R.string.alarm));
        }
        if(preferCitySrc.size()>0) {
            addContainer(preferCitySrc.size(), getString(R.string.world_time));
        }
        if(vipSrc.size()>0) {
            addContainer(vipSrc.size(), getString(R.string.vip_alert));
        }
        if(ON.equals(intervalDao.status)) {
            addContainer(1, getString(R.string.interval_remind));
        }

        findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SyncDeviceHelper.syncSetControlFlag(MAC, new SyncDeviceHelper.BtCallback() {
                    @Override
                    public void onBtResponse(byte[] bytes) {
                        writeDataToWatch(true);
                    }
                }, new int[]{i[0], i[1], 2, Rom.isMIUI() ? 2 : 3});
                mDBHelper.updateTimeStamp(DEVICE_SYNC_KEY, TimeUtil.getNowTimeSeconds());
                EventBus.getDefault().post(new ChangeUI(RENDER_AGAIN));
                finish();
            }
        });

        findViewById(R.id.right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDBHelper.clearLocalData();
                mDBHelper.updateTimeStamp(INTERVAL_ALARM_KEY, TimeUtil.getNowTimeSeconds());
                mDBHelper.updateTimeStamp(NORMAL_ALARM_KEY, TimeUtil.getNowTimeSeconds());
                mDBHelper.updateTimeStamp(WORLD_CITY_KEY, TimeUtil.getNowTimeSeconds());
                mDBHelper.updateTimeStamp(VIP_KEY, TimeUtil.getNowTimeSeconds());
                mDBHelper.updateTimeStamp(VIBRATE_SETTING_KEY, TimeUtil.getNowTimeSeconds());
                SyncDeviceHelper.syncSetControlFlag(MAC, new SyncDeviceHelper.BtCallback() {
                    @Override
                    public void onBtResponse(byte[] bytes) {
                        writeDataToWatch(false);
                    }
                }, new int[]{i[0], i[1], 2, Rom.isMIUI() ? 2 : 3});
                mDBHelper.updateTimeStamp(DEVICE_SYNC_KEY, TimeUtil.getNowTimeSeconds());
                EventBus.getDefault().post(new ChangeUI(RENDER_AGAIN));
                finish();
            }
        });
    }

    private  void addContainer(int size,String type){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView tv = new TextView(mContext);
        tv.setTextColor(ContextCompat.getColor(mContext,R.color.black_60_transparent));
        tv.setText(mTs.clear().spanSize("一").spanSize("\t\t"+size+getResources().getString(R.string.unit_ge)).spanSize(type).getText());
        container.addView(tv, lp);
}


    /**
     * update device data
     */
    private void writeDataToWatch(final boolean needRecovery) {
        HttpSyncHelper.getUTC(MODEL, new Callback<Object>() {
            @Override
            public void onSuccess(Object result) {
                L.e("getUTC :" + result.toString());
                HttpUTCRes utc = AppController.getGson().fromJson(result.toString(), HttpUTCRes.class);
                int time = (int) (DateUtil.changeTimeZone(DateUtil.getDate(utc.result * 1000), TimeZone.getTimeZone("GMT+8:00"), TimeZone.getTimeZone(mDBHelper.getSettingZone())).getTime() / 1000) - TimeUtil.getWatchSysStartTimeSecs();
                SyncDeviceHelper.syncDevicePreferTime(MAC, time, new SyncDeviceHelper.BtCallback() {      //同步完时间后再做其他事 不然可能会无效
                        @Override
                        public void onBtResponse(byte[] bytes) {
                        if(needRecovery) { //
                            SyncDeviceHelper.syncDeviceInterval(MAC ,intervalDao,mDBHelper);
                            if (alarmSrc.size() > 0) {
                                for (AlarmDao item : alarmSrc) {
                                    SyncDeviceHelper.syncDeviceAlarm(MAC,item);
                                }
                            }
                            if (vipSrc.size() > 0) {
                                for (VipEntity item : vipSrc) {
                                    SyncDeviceHelper.syncDeviceVip(MAC, item);
                                }
                            }
                            SyncDeviceHelper.syncDeviceVibration(MAC,vibrationDao);
                        }else{//clear
                            SyncDeviceHelper.syncClearDeviceAlarm(MAC);
                            SyncDeviceHelper.syncClearDeviceVip(MAC);
                            SyncDeviceHelper.syncClearInterval(MAC);
                            SyncDeviceHelper.syncClearVibration(MAC);
                        }
                    }
                });
            }
            @Override
            public void onFailure(int i, String s) {
                L.e("getUTC error :" + s);
            }
        });

    }
//
//    @Override
//    public void onBackPressed() {
//        SyncDeviceHelper.syncSetControlFlag(MAC, new SyncDeviceHelper.BtCallback() {
//            @Override
//            public void onBtResponse(byte[] bytes) {
//                writeDataToWatch(true);
//            }
//        }, new int[]{i[0], i[1], 2, Rom.isMiui() ? 2 : 3});
//        mDBHelper.updateTimeStamp(DEVICE_SYNC_KEY, TimeUtil.getNowTimeSeconds());
//        EventBus.getDefault().post(new ChangeUI(RENDER_AGAIN));
//        finish();
//    }


    @Override
    protected boolean isNeedBackPress() {
        return false;
    }
}

