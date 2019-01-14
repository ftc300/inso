package com.inshow.watch.android.act.user;

import android.view.View;

import com.inshow.watch.android.R;
import com.inshow.watch.android.act.mainpagelogic.MainAct;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.dao.WatchUserDao;
import com.inshow.watch.android.sync.http.bean.HttpRegister;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.bean.HttpUserInfo;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.manager.SPManager;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.TimeUtil;
import com.inshow.watch.android.view.LineOnePicker;
import com.xiaomi.smarthome.device.api.Callback;
import org.json.JSONArray;
import static com.inshow.watch.android.tools.Constants.HttpConstant.TYPE_USER_INFO;
import static com.inshow.watch.android.tools.Constants.SettingHelper.BIRTH_DEFAULT;
import static com.inshow.watch.android.tools.Constants.SettingHelper.GENDER_DEFAULT;
import static com.inshow.watch.android.tools.Constants.SettingHelper.HEIGHT_DEFAULT;
import static com.inshow.watch.android.tools.Constants.SettingHelper.WEIGHT_DEFAULT;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_BIRTH;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_GENDER;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_HEIGHT;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_WEIGHT;
import static com.inshow.watch.android.tools.Constants.TimeStamp.USER_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.USER_REGISTER_KEY;

/**
 * Created by chendong on 2017/4/17.
 */

public class WeightAct extends BasicAct {

    private LineOnePicker weight;

    @Override
    protected int getContentRes() {
        return R.layout.watch_act_weight;
    }

    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    @Override
    protected void initViewOrData() {
        mHostActivity.enableBlackTranslucentStatus();
        weight = (LineOnePicker) findViewById(R.id.weight);
        weight.setMaxValue(250);
        weight.setMinValue(3);
        weight.setValue(70);
        findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               switchTo(HeightAct.class);
                finish();
            }
        });
        findViewById(R.id.right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SPManager.put(mContext,SP_ARG_WEIGHT,weight.getValue());
                SPManager.put(mContext,USER_REGISTER_KEY,TimeUtil.getNowTimeSeconds());
                mDBHelper.updateTimeStamp(USER_REGISTER_KEY,TimeUtil.getNowTimeSeconds());
                mDBHelper.updateTimeStamp(USER_KEY,TimeUtil.getNowTimeSeconds());
                mDBHelper.updateUser(new WatchUserDao(
                        (int)SPManager.get(mContext,SP_ARG_HEIGHT,HEIGHT_DEFAULT),
                        (int)SPManager.get(mContext,SP_ARG_WEIGHT,WEIGHT_DEFAULT),
                        (String) SPManager.get(mContext,SP_ARG_GENDER,GENDER_DEFAULT),
                        (String)SPManager.get(mContext,SP_ARG_BIRTH, BIRTH_DEFAULT)
                ));
                pushBodyInfoToMijia();
                pushRegisterInfoToMijia();
                switchTo(MainAct.class);
                finish();
            }
        });
    }




    /**
     * 上传身体信息
     */
    private void pushBodyInfoToMijia() {
        HttpUserInfo bean = new HttpUserInfo(
                (int)SPManager.get(mContext,SP_ARG_WEIGHT,WEIGHT_DEFAULT),
                (int)SPManager.get(mContext,SP_ARG_HEIGHT,HEIGHT_DEFAULT),
                (String) SPManager.get(mContext,SP_ARG_GENDER,GENDER_DEFAULT),
                (String)SPManager.get(mContext,SP_ARG_BIRTH, BIRTH_DEFAULT)
        );
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
                        L.e("pushBodyInfoToMijia:"+jsonArray.toString());
                    }
                    @Override
                    public void onFailure(int i, String s) {
                        L.e("pushBodyInfoToMijiaError:"+s);
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
                        L.e("pushRegisterInfoToMijia:"+jsonArray.toString());
                    }
                    @Override
                    public void onFailure(int i, String s) {
                        L.e("pushRegisterInfoToMijiaError:"+s);
                    }
                });
    }
}
