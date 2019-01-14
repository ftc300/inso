package com.inshow.watch.android.fragment;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.act.datasheet.DataSheetAct;
import com.inshow.watch.android.act.alarm.AlarmClockAct;
import com.inshow.watch.android.act.alarm.IOnceAlarm;
import com.inshow.watch.android.act.city.WorldTimeAct;
import com.inshow.watch.android.act.interval.IntervalHelper;
import com.inshow.watch.android.act.interval.IntervalRemindAct;
import com.inshow.watch.android.act.mainpagelogic.LowPowerManager;
import com.inshow.watch.android.act.user.WatchUserInfoHelper;
import com.inshow.watch.android.act.vip.InComingPhoneAlertAct;
import com.inshow.watch.android.basic.BasicFragment;
import com.inshow.watch.android.dao.AlarmDao;
import com.inshow.watch.android.dao.IntervalDao;
import com.inshow.watch.android.dao.PreferCitiesDao;
import com.inshow.watch.android.dao.StepDao;
import com.inshow.watch.android.event.ChangeUI;
import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.manager.SPManager;
import com.inshow.watch.android.provider.DBHelper;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.sync.http.bean.HttpAlarm;
import com.inshow.watch.android.sync.http.bean.HttpCurrentSteps;
import com.inshow.watch.android.sync.http.bean.HttpStepHistory;
import com.inshow.watch.android.tools.Configuration;
import com.inshow.watch.android.tools.Constants;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.MessUtil;
import com.inshow.watch.android.tools.Rom;
import com.inshow.watch.android.tools.TextStyle;
import com.inshow.watch.android.tools.TimeUtil;
import com.inshow.watch.android.tools.ToastUtil;
import com.inshow.watch.android.view.CustomItem;
import com.inshow.watch.android.view.ItemStatus;
import com.inshow.watch.android.view.risenum.RiseNumberTextView;
import com.xiaomi.smarthome.bluetooth.Response;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;
import com.xiaomi.smarthome.device.api.Callback;
import com.xiaomi.smarthome.device.api.Parser;
import com.xiaomi.smarthome.device.api.XmPluginHostApi;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import miui.bluetooth.ble.MiBleDeviceManager;

import static com.inshow.watch.android.event.ChangeUI.CONNECT_DFU;
import static com.inshow.watch.android.event.ChangeUI.RENDER_AGAIN;
import static com.inshow.watch.android.event.ChangeUI.SYNC_BIND_RENDER;
import static com.inshow.watch.android.manager.BleManager.B2I_getStep;
import static com.inshow.watch.android.manager.BleManager.bytesToHexString;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_TODAY_STEP;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;
import static com.inshow.watch.android.tools.Constants.HttpConstant.TYPE_USER_INFO;
import static com.inshow.watch.android.tools.Constants.HttpConstant.URL_SET_USER_DEVICE_DATA;
import static com.inshow.watch.android.tools.Constants.OFF;
import static com.inshow.watch.android.tools.Constants.ON;
import static com.inshow.watch.android.tools.Constants.SystemConstant.EXTRAS_EVENT_BUS;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_DID;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_FIRMWARE_VERSION;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_MODEL;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_USERID;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_INCOMING_SWITCH;
import static com.inshow.watch.android.tools.Constants.TimeStamp.CURRENT_STEPS;
import static com.inshow.watch.android.tools.Constants.TimeStamp.NORMAL_ALARM_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.STEPS_KEY;
import static com.inshow.watch.android.tools.MessUtil.getListType;
import static com.inshow.watch.android.tools.MessUtil.showVipOta;
import static com.inshow.watch.android.tools.TimeUtil.getMTS;

public class FragmentBottom extends BasicFragment implements View.OnClickListener, IStepChangeListener, IRender {
    private RiseNumberTextView stepCounter;
    private CustomItem menuWorld, menuInterval, menuVIP, menuAlarm;
    private TextView titleWorld, titleInterval, titleVIP, titleAlarm;
    private TextView titleWorldInfo, titleIntervalInfo, titleVIPInfo, titleAlarmInfo;
    private DBHelper mDBHelper;
    private List<PreferCitiesDao> mPreCities = new ArrayList<>();
    private static final int MIN_SRV_VERSION = 6;
    private MiBleDeviceManager mManager;
    private TextStyle mTs;
    private int mSize;
    private String onText, closeText;
    private IntervalDao intervalDao;
    private WatchUserInfoHelper userHelper;
    private int mCurrentSteps, duration;
    private TextView version;
    private String selectCity;

    public static FragmentBottom getInstance(int version) {
        FragmentBottom f = new FragmentBottom();
        Bundle arg = new Bundle();
        arg.putBoolean(EXTRAS_EVENT_BUS, true);
        arg.putInt("Version", version);
        f.setArguments(arg);
        return f;
    }

    @Subscribe
    public void onEventMainThread(ChangeUI event) {
        if (isAdded()) {
            if (CONNECT_DFU == event.btCode) setOnClickListenersNull();
            if (!TextUtils.isEmpty(event.action) && RENDER_AGAIN.equals(event.action)) {
                L.e("FragmentBottom onEventMainThread render");
                renderByData();
                MessUtil.bindOrNot(mContext, MAC, getVipState());
            }
            if (SYNC_BIND_RENDER.equals(event.action)) {
                //来电提醒bug
                renderByData();
                MessUtil.bindOrNot(mContext, MAC, false);
                version.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MessUtil.bindOrNot(mContext, MAC, getVipState());
                    }
                }, 2000);
            }
        }
    }

    boolean getVipState() {
        return (Boolean) SPManager.get(mContext, SP_INCOMING_SWITCH, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.watch_frg_second, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initData();
        setOnClickListenersNull();
        MiBleDeviceManager.createManager(mContext, new MiBleDeviceManager.MiBleDeviceManagerListener() {
            @Override
            public void onInit(MiBleDeviceManager manager) {
                mManager = manager;
                if (mManager.getServiceVersion() < MIN_SRV_VERSION) {
                    ToastUtil.showToastNoRepeat(mContext, "版本服务小于6，不支持此功能");
                }
            }

            @Override
            public void onDestroy() {
                mManager = null;
            }
        });
    }

    private void bindView(View view) {
        menuWorld = (CustomItem) view.findViewById(R.id.menu_worldtime);
        menuInterval = (CustomItem) view.findViewById(R.id.menu_intervalremind);
        menuVIP = (CustomItem) view.findViewById(R.id.menu_vipremind);
        menuAlarm = (CustomItem) view.findViewById(R.id.menu_alarmclock);
        stepCounter = ((RiseNumberTextView) view.findViewById(R.id.step_counter));
        titleWorld = menuWorld.getTvLable();
        titleInterval = menuInterval.getTvLable();
        titleVIP = menuVIP.getTvLable();
        titleAlarm = menuAlarm.getTvLable();
        titleWorldInfo = menuWorld.getTvContent();
        titleIntervalInfo = menuInterval.getTvContent();
        titleVIPInfo = menuVIP.getTvContent();
        titleAlarmInfo = menuAlarm.getTvContent();
        version = (TextView) view.findViewById(R.id.tv_version);
    }

    private void initData() {
        onText = getResources().getString(R.string.on);
        closeText = getResources().getString(R.string.off);
        userHelper = new WatchUserInfoHelper(mContext);
        mDBHelper = new DBHelper(getActivity());
        mTs = new TextStyle(ContextCompat.getColor(mContext, R.color.black), 35);
        titleWorld.setText(getString(R.string.menu_worldtime));
        titleInterval.setText(getString(R.string.menu_intervalremind));
        titleVIP.setText(getString(R.string.menu_vipremind));
        titleAlarm.setText(getString(R.string.menu_alarmclock));
        Configuration.getInstance().LocaleHandler(mContext, new Configuration.LocaleHandler() {
            @Override
            public void cnHandle() {
                stepCounter.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/MIUI_EX_Light.ttf"));
            }

            @Override
            public void twHandle() {
                stepCounter.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/MIUI_EX_Light.ttf"));
            }

            @Override
            public void hkHandle() {
                stepCounter.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/MIUI_EX_Light.ttf"));
            }

            @Override
            public void enHandle() {

            }

            @Override
            public void defaultHandle() {

            }
        });
        stepCounter.setAlpha(0.3f);
        stepCounter.setText(mTs.clear().span(getResources().getString(R.string.A05)).spanColorAndSize("\t\t0").spanColor(getResources().getString(R.string.A06)).getText());
        version.setText(TextUtils.concat("V1.2.", String.valueOf(getArguments().getInt("Version") % 100)).toString());
    }

    private void setOnClickListeners() {
        menuWorld.setOnClickListener(this);
        menuInterval.setOnClickListener(this);
        menuVIP.setOnClickListener(this);
        menuAlarm.setOnClickListener(this);
        stepCounter.setOnClickListener(this);
        menuWorld.setCustomItem(ItemStatus.ENABLE);
        menuVIP.setCustomItem(ItemStatus.ENABLE);
        menuInterval.setCustomItem(ItemStatus.ENABLE);
        menuAlarm.setCustomItem(ItemStatus.ENABLE);
        menuWorld.setCustomDrawable(R.drawable.list_ic_worldtime_normal);
        menuVIP.setCustomDrawable(R.drawable.list_ic_call_normal);
        menuInterval.setCustomDrawable(R.drawable.list_ic_timer_normal);
        menuAlarm.setCustomDrawable(R.drawable.list_ic_alarm_normal);
    }

    private void setOnClickListenersNull() {
        menuWorld.setOnClickListener(null);
        menuInterval.setOnClickListener(null);
        menuVIP.setOnClickListener(null);
        menuAlarm.setOnClickListener(null);
        menuWorld.setCustomItem(ItemStatus.UNABLE);
        menuVIP.setCustomItem(ItemStatus.UNABLE);
        menuInterval.setCustomItem(ItemStatus.UNABLE);
        menuAlarm.setCustomItem(ItemStatus.UNABLE);
        menuWorld.setCustomDrawable(R.drawable.list_ic_worldtime_unable);
        menuVIP.setCustomDrawable(R.drawable.list_ic_call_unable);
        menuInterval.setCustomDrawable(R.drawable.list_ic_timer_unable);
        menuAlarm.setCustomDrawable(R.drawable.list_ic_alarm_unable);
    }

    @Override
    public void onResume() {
        super.onResume();
        renderByData();
    }

    private void renderByData() {
        mPreCities = mDBHelper.getAllPreferCities();
        //todo 应该封装在Dao里面的
        for (final PreferCitiesDao item : mPreCities) {
            if (item.isSel) {
                Configuration.getInstance().LocaleHandler(mContext, new Configuration.LocaleHandler() {
                    @Override
                    public void cnHandle() {
                        selectCity = item.zh_cn;
                    }

                    @Override
                    public void twHandle() {
                        selectCity = item.zh_tw;
                    }

                    @Override
                    public void hkHandle() {
                        selectCity = item.zh_hk;
                    }

                    @Override
                    public void enHandle() {
                        selectCity = item.en;
                    }

                    @Override
                    public void defaultHandle() {
                        selectCity = item.zh_cn;
                    }
                });
                titleWorldInfo.setText(selectCity);
                break;
            }
        }
        intervalDao = mDBHelper.getInterval();
        titleIntervalInfo.setText(ON.equals(intervalDao.status) ? mTs.clear()
                .span(IntervalHelper.isIntegral(intervalDao.time) ? getString(R.string.integral) : intervalDao.time / 60 + "")
                .span(IntervalHelper.isIntegral(intervalDao.time) ? "" : getString(intervalDao.time / 60 == 1 ? R.string.unit_min1 : R.string.unit_min2))
                .getText() : closeText);
        titleVIPInfo.setText(getVipState() ? onText : closeText);
        MessUtil.checkOnceAlarm(mDBHelper, new IOnceAlarm() {
            @Override
            public void onStateChanged() {
                pushAlarmInfoToMijia();
            }
        });//更新
        titleAlarmInfo.setText((mSize = mDBHelper.getOpenAlarmCount()) > 0 ? mTs.clear().span(String.valueOf(mSize)).span(getString(R.string.unit_ge)).getText() : closeText);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_worldtime:
                switchTo(WorldTimeAct.class);
                break;
            case R.id.menu_intervalremind:
                LowPowerManager.getInstance().handleBottomLowPower(mContext, new LowPowerManager.ILowPowerHandle() {
                    @Override
                    public void handle() {
                        switchTo(IntervalRemindAct.class);
                    }
                });
                break;
            case R.id.menu_vipremind:
                LowPowerManager.getInstance().handleBottomLowPower(mContext, new LowPowerManager.ILowPowerHandle() {
                    @Override
                    public void handle() {
                        MessUtil.checkAndHandleVipChange((String) SPManager.get(mContext, SP_ARG_FIRMWARE_VERSION, ""), new MessUtil.IVip() {
                            @Override
                            public void lowerThanVipChangVersion() {
                                showVipOta(mContext);
                            }

                            @Override
                            public void largerThan() {
                                switchTo(InComingPhoneAlertAct.class);
                            }
                        });
                    }
                });
                break;
            case R.id.menu_alarmclock:
                LowPowerManager.getInstance().handleBottomLowPower(mContext, new LowPowerManager.ILowPowerHandle() {
                    @Override
                    public void handle() {
                        switchToWithEventBus(AlarmClockAct.class);
                    }
                });
                break;
            case R.id.step_counter:
                modifyCurrentStep();
                switchTo(DataSheetAct.class);
                break;

        }
    }


    /**
     * 根据返回值显示当前步数
     *
     * @param isNotify
     * @param value
     */
    private void displayNowStep(boolean isNotify, byte[] value) {
        L.e("step:" + bytesToHexString(value));
        int[] i = B2I_getStep(value);
//        int start = TimeUtil.getTodayZero();
//        int end = TimeUtil.getTodayZero() + i[1];
        duration = i[1];
        mCurrentSteps = i[0];
        stepCounter.setAlpha(1f);
        if (isNotify) {
            stepCounter.setText(mTs.clear().span(getString(R.string.A05)).spanSize("\t\t").spanColorAndSize(String.valueOf(mCurrentSteps)).spanColor(getString(R.string.A06)).getText());
        } else {
            stepCounter.withString(mCurrentSteps);
            stepCounter.setDuration(1200);
            stepCounter.start();
            pushCurrentSteps();
        }
        modifyCurrentStep();
    }

    private void modifyCurrentStep() {
        int serverZeroStamp = TimeUtil.getTodayZero(mDBHelper.getSettingZone());
        int start = serverZeroStamp;
        int end = serverZeroStamp + duration;
        //时区调整为明天的或者为今天的可以替换今日步数
        TimeUtil.TimeCompare cmp = getMTS(mDBHelper.getSettingZone());
        L.e("mDBHelper.getKeyTimeStamp(STEPS_KEY):" + mDBHelper.getKeyTimeStamp(STEPS_KEY) + ",start:" + start);
        if (cmp == TimeUtil.TimeCompare.LESS) {
            L.e("less");
            return;
        } else if (cmp == TimeUtil.TimeCompare.MORE) {
            //调到后一天就更新时间戳，当前步数已经生成了历史步数
            if (mDBHelper.getKeyTimeStamp(STEPS_KEY) < start) {
                uploadToMijia(start + 24 * 3600);
                mDBHelper.updateTimeStamp(STEPS_KEY, serverZeroStamp);
            }
            TimeUtil.initZone(mDBHelper);
            L.e("MORE TimeUtil.getMD(start):" + TimeUtil.getMD(start) + "start:" + start);
//            start = start + 24 * 3600;
            mDBHelper.addTodayStep(new StepDao(start, start + duration, mCurrentSteps, duration,
                    userHelper.getDistance(mCurrentSteps),
                    userHelper.getKCal(mCurrentSteps),
                    TimeUtil.getMD(start),
                    TimeUtil.getWeekBegEnd(start),
                    TimeUtil.getMon(start),
                    TimeUtil.getYear(start)));
        } else if ((cmp == TimeUtil.TimeCompare.EQUAL) && (mDBHelper.getKeyTimeStamp(STEPS_KEY) < start)) {
            TimeUtil.releaseZone();
            L.e("EQUAL TimeUtil.getMD(start):" + TimeUtil.getMD(start));
            StepDao s = new StepDao(start, end, mCurrentSteps, end - start,
                    userHelper.getDistance(mCurrentSteps),
                    userHelper.getKCal(mCurrentSteps),
                    TimeUtil.getMD(start),
                    TimeUtil.getWeekBegEnd(start),
                    TimeUtil.getMon(start),
                    TimeUtil.getYear(start));
            L.e(s.toString());
            mDBHelper.addTodayStep(s);
        }
    }

    protected boolean uploadToMijia(int to) {
        List<HttpStepHistory> src = mDBHelper.getStepSyncData(mDBHelper.getKeyTimeStamp(STEPS_KEY), to);
        try {
            JSONObject data = new JSONObject();
            for (int i = 0; i < src.size(); i++) {
                String key = String.valueOf(i);
                HttpStepHistory item = src.get(i);
                JSONObject value = new JSONObject();
                value.put("uid", (String) SPManager.get(mContext, SP_ARG_USERID, ""));
                value.put("did", (String) SPManager.get(mContext, SP_ARG_DID, ""));
                value.put("type", TYPE_USER_INFO);
                value.put("time", item.start);
                value.put("key", STEPS_KEY);
                value.put("value", AppController.getGson().toJson(item));
                data.put(key, value);
            }
            L.e("===>uploadToMijia=>ReqParam:" + data.toString());
            XmPluginHostApi.instance().callSmartHomeApi((String) SPManager.get(mContext, SP_ARG_MODEL, ""), URL_SET_USER_DEVICE_DATA, data, new Callback<JSONArray>() {
                @Override
                public void onSuccess(JSONArray jsonArray) {
                    L.e("===>uploadToMijia Success ======");
                }

                @Override
                public void onFailure(int i, String s) {
                    L.e("===>uploadToMijia Fail ======");
                }
            }, new Parser() {
                public JSONArray parse(String result) throws JSONException {
                    JSONObject response = new JSONObject(result);
                    return response.getJSONArray("result");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void pushCurrentSteps() {
        if (mCurrentSteps > 0) {
            HttpCurrentSteps bean = new HttpCurrentSteps(duration, mCurrentSteps);
            HttpSyncHelper.pushData(new RequestParams(
                    (String) SPManager.get(mContext, SP_ARG_MODEL, ""),
                    (String) SPManager.get(mContext, SP_ARG_USERID, ""),
                    (String) SPManager.get(mContext, SP_ARG_DID, ""),
                    Constants.HttpConstant.TYPE_USER_INFO,
                    CURRENT_STEPS,
                    AppController.getGson().toJson(bean),
                    TimeUtil.getNowTimeSeconds()
            ), new Callback<JSONArray>() {
                @Override
                public void onSuccess(JSONArray jsonArray) {
                    L.e("setCurrentSteps:" + jsonArray.toString());
                }

                @Override
                public void onFailure(int i, String s) {
                    L.e("setCurrentSteps:" + s);
                }
            });
        }
    }

    @Override
    public void onChanged(byte[] value) {
        if (isAdded()) //判断当天数据
            displayNowStep(true, value);
    }


    @Override
    public void renderSuccess() {
        setOnClickListeners();
        renderByData();
        //通知监听
        XmBluetoothManager.getInstance().notify(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_TODAY_STEP), new Response.BleNotifyResponse() {
            @Override
            public void onResponse(int code, Void data) {

            }
        });
        L.e("renderSuccess read  CHARACTERISTIC_TODAY_STEP");
        XmBluetoothManager.getInstance().read(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_TODAY_STEP), new Response.BleReadResponse() {
            @Override
            public void onResponse(int i, byte[] bytes) {
                L.e("renderSuccess CHARACTERISTIC_TODAY_STEP:" + bytes);
                displayNowStep(false, bytes);
            }
        });
    }

    @Override
    public void renderFail() {
        if (isAdded()) {
//            stepCounter.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    switchTo(DataSheetAct.class);
//                }
//            });
            stepCounter.setText(mTs.clear().span(getString(R.string.A05)).spanSize("\t\t0").span(getString(R.string.A06)).getText());
        }
    }

    @Override
    public void netLost() {
        renderFail();
    }

    @Override
    public void onDestroy() {
        pushCurrentSteps();
        super.onDestroy();
    }

    /**
     * 上传闹钟信息
     */
    private void pushAlarmInfoToMijia() {
        L.e("FragmentBottom pushAlarmInfoToMijia");
        mDBHelper.updateTimeStamp(NORMAL_ALARM_KEY, TimeUtil.getNowTimeSeconds());
        List<HttpAlarm> list = new ArrayList<>();
        List<AlarmDao> mDbSource = mDBHelper.getAllAlarm();
        for (AlarmDao item : mDbSource) {
            list.add(new HttpAlarm(item.id, item.status ? ON : OFF, item.seconds, item.extend, getListType(item.repeatType), item.desc));
        }
        HttpSyncHelper.pushData(
                new RequestParams(
                        MODEL,
                        UID,
                        DID,
                        TYPE_USER_INFO,
                        NORMAL_ALARM_KEY,
                        AppController.getGson().toJson(list),
                        mDBHelper.getKeyTimeStamp(NORMAL_ALARM_KEY)
                ), new Callback<JSONArray>() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) {
                        L.e("pushAlarmInfoToMijia:" + jsonArray.toString());
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        L.e("pushAlarmInfoToMijiaError:" + s);
                    }
                });
    }
}
