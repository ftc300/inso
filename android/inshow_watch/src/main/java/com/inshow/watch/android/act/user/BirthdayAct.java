package com.inshow.watch.android.act.user;

import android.view.View;

import com.inshow.watch.android.R;
import com.inshow.watch.android.act.mainpagelogic.MainAct;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.manager.SPManager;
import com.inshow.watch.android.provider.DBHelper;
import com.inshow.watch.android.sync.http.bean.HttpRegister;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.bean.HttpUserInfo;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.NumUtil;
import com.inshow.watch.android.tools.TimeUtil;
import com.inshow.watch.android.view.LineOnePicker;
import com.xiaomi.smarthome.device.api.Callback;
import com.xiaomi.smarthome.device.api.DeviceStat;

import org.json.JSONArray;

import static com.inshow.watch.android.tools.Constants.HttpConstant.TYPE_USER_INFO;
import static com.inshow.watch.android.tools.Constants.SettingHelper.BIRTH_DEFAULT;
import static com.inshow.watch.android.tools.Constants.SettingHelper.GENDER_DEFAULT;
import static com.inshow.watch.android.tools.Constants.SettingHelper.HEIGHT_DEFAULT;
import static com.inshow.watch.android.tools.Constants.SettingHelper.WEIGHT_DEFAULT;
import static com.inshow.watch.android.tools.Constants.SystemConstant.EXTRAS_DEVICE_STATE;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_BIRTH;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_BLUETOOTH_CONNECTED;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_DEVICE_NAME;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_DID;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_GENDER;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_HEIGHT;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_MAC;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_MODEL;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_USERID;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_WEIGHT;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_DB_VERSION;
import static com.inshow.watch.android.tools.Constants.TimeStamp.USER_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.USER_REGISTER_KEY;
import static com.inshow.watch.android.view.LineOnePicker.TWO_DIGIT_FORMATTER;

/**
 * Created by chendong on 2017/4/17.
 */
public class BirthdayAct extends BasicAct {

    private LineOnePicker year, month;

    @Override
    protected int getContentRes() {
        return R.layout.watch_act_birthday;
    }


    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    @Override
    protected void initViewOrData() {
        mHostActivity.enableBlackTranslucentStatus();
        year = (LineOnePicker) findViewById(R.id.year);
        month = (LineOnePicker) findViewById(R.id.month);
        year.setMaxValue(2017);
        year.setMinValue(1917);
        year.setFormatter(TWO_DIGIT_FORMATTER);
        year.setValue(1990);
        month.setMaxValue(12);
        month.setMinValue(1);
        month.setFormatter(TWO_DIGIT_FORMATTER);
        findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToWithEventBus(MainAct.class);
                saveDefaultBodyInfo();
                SPManager.put(mContext,USER_REGISTER_KEY,TimeUtil.getNowTimeSeconds());
                mDBHelper.updateTimeStamp(USER_REGISTER_KEY, TimeUtil.getNowTimeSeconds());
                mDBHelper.updateTimeStamp(USER_KEY, TimeUtil.getNowTimeSeconds());
                pushDefaultBodyInfoToMijia();
                pushRegisterInfoToMijia();
                finish();
            }
        });
        findViewById(R.id.right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SPManager.put(mContext, SP_ARG_BIRTH, year.getValue() + "-" + NumUtil.formatTwoDigitalNum(month.getValue()));
                switchTo(SexAct.class);
                finish();
            }
        });
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

    /**
     * 保存默认的信息
     */
    private void saveDefaultBodyInfo() {
        SPManager.put(mContext, SP_ARG_BIRTH, BIRTH_DEFAULT);
        SPManager.put(mContext, SP_ARG_GENDER, GENDER_DEFAULT);
        SPManager.put(mContext, SP_ARG_HEIGHT, HEIGHT_DEFAULT);
        SPManager.put(mContext, SP_ARG_WEIGHT, WEIGHT_DEFAULT);
    }

    /**
     * 上传默认身体信息
     */
    private void pushDefaultBodyInfoToMijia() {
        HttpUserInfo bean = new HttpUserInfo(WEIGHT_DEFAULT, HEIGHT_DEFAULT, GENDER_DEFAULT, BIRTH_DEFAULT);
        HttpSyncHelper.pushData(
                new RequestParams(
                        MODEL,
                        UID,
                        DID,
                        TYPE_USER_INFO,
                        USER_KEY,
                        AppController.getGson().toJson(bean),
                        mSyncHelper.getLocalUserKeyTime()), new Callback<JSONArray>() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) {
                        L.e("pushDefaultBodyInfoToMijia:" + jsonArray.toString());
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        L.e("pushDefaultBodyInfoToMijiaError:" + s);
                    }
                });
    }


    /**
     * 上传注册信息
     */
    private void pushRegisterInfoToMijia() {
        HttpRegister bean = new HttpRegister(TimeUtil.getNowTimeSeconds());
        HttpSyncHelper.pushData(
                new RequestParams(
                        MODEL,
                        UID,
                        DID,
                        TYPE_USER_INFO,
                        USER_REGISTER_KEY,
                        AppController.getGson().toJson(bean),
                        mSyncHelper.getLocalRegisterKeyTime()), new Callback<JSONArray>() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) {
                        L.e("pushRegisterInfoToMijia:" + jsonArray.toString());
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        L.e("pushRegisterInfoToMijiaError:" + s);
                    }
                });
    }
}
