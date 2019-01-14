package com.inshow.watch.android.act.user;

import android.view.View;

import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.manager.SPManager;
import com.inshow.watch.android.provider.DBHelper;
import com.inshow.watch.android.tools.Configuration;
import com.inshow.watch.android.view.LineOnePicker;
import com.xiaomi.smarthome.device.api.DeviceStat;
import com.xiaomi.smarthome.device.api.XmPluginHostApi;

import static com.inshow.watch.android.tools.Constants.SettingHelper.BIRTH_DEFAULT;
import static com.inshow.watch.android.tools.Constants.SettingHelper.GENDER_DEFAULT;
import static com.inshow.watch.android.tools.Constants.SettingHelper.HEIGHT_DEFAULT;
import static com.inshow.watch.android.tools.Constants.SettingHelper.WEIGHT_DEFAULT;
import static com.inshow.watch.android.tools.Constants.SystemConstant.EXTRAS_DEVICE_STATE;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_BIRTH;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_DEVICE_NAME;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_DID;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_GENDER;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_HEIGHT;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_MAC;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_MODEL;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_USERID;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_WEIGHT;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_DB_VERSION;
import static com.inshow.watch.android.tools.Constants.TimeStamp.USER_REGISTER_KEY;

/**
 * Created by chendong on 2017/4/17.
 */

public class SexAct extends BasicAct {

    private LineOnePicker sex;

    @Override
    protected int getContentRes() {
        return R.layout.watch_act_sex;
    }

    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    @Override
    protected void storeDeviceStat() {
        int register = (Integer) SPManager.get(mContext, USER_REGISTER_KEY, 0);
        SPManager.put(mContext, SP_ARG_BIRTH, BIRTH_DEFAULT);
        SPManager.put(mContext, SP_ARG_GENDER, GENDER_DEFAULT);
        SPManager.put(mContext, SP_ARG_HEIGHT, HEIGHT_DEFAULT);
        SPManager.put(mContext, SP_ARG_WEIGHT, WEIGHT_DEFAULT);
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
        mHostActivity.enableBlackTranslucentStatus();
        sex = (LineOnePicker) findViewById(R.id.sex);
        sex.setDisplayedValues(new String[]{getString(R.string.nv),getString(R.string.nan)});
        sex.setMinValue(1);
        sex.setMaxValue(2);
        sex.setValue(2);
        findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Configuration.getInstance().ServerHandle(new Configuration.ServerHandler2() {
                    @Override
                    public void defaultServer() {

                    }

                    @Override
                    public void cnServer() {
                        switchTo(BirthdayAct.class);
                    }
                });
                finish();
            }
        });
        findViewById(R.id.right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SPManager.put(mContext,SP_ARG_GENDER, sex.getValue() == 1?"female":"male");
                switchTo(HeightAct.class);
                finish();
            }
        });
    }

}
