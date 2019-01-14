package com.inshow.watch.android.act.debug;

import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.sync.AsyncHttpManager;
import com.inshow.watch.android.sync.ITerminatedListener;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.TimeUtil;
import com.inshow.watch.android.tools.ToastUtil;
import com.xiaomi.smarthome.device.api.Callback;

import org.json.JSONArray;

import static com.inshow.watch.android.tools.Constants.HttpConstant.TYPE_USER_INFO;
import static com.inshow.watch.android.tools.Constants.TimeStamp.HTTP_SYNC_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.INTERVAL_ALARM_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.NORMAL_ALARM_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.USER_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.USER_REGISTER_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.VIBRATE_SETTING_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.WORLD_CITY_KEY;

/**
 * Created by chendong on 2017/7/10.
 */

public class ClearAct extends BasicAct {
    AsyncHttpManager asyncHttpManager;

    @Override
    protected void initViewOrData() {
        super.initViewOrData();
        asyncHttpManager = new AsyncHttpManager(mContext, mDBHelper);
        mDBHelper.clearLocalData();
        mDBHelper.updateTimeStamp(INTERVAL_ALARM_KEY, TimeUtil.getNowTimeSeconds());
        mDBHelper.updateTimeStamp(NORMAL_ALARM_KEY, TimeUtil.getNowTimeSeconds());
        mDBHelper.updateTimeStamp(WORLD_CITY_KEY, TimeUtil.getNowTimeSeconds());
        mDBHelper.updateTimeStamp(VIBRATE_SETTING_KEY, TimeUtil.getNowTimeSeconds());
        mDBHelper.updateTimeStamp(USER_REGISTER_KEY, 0);
        asyncHttpManager.startHttpSync(new ITerminatedListener() {
            @Override
            public void onAsyncFinished() {
                L.e(" ========= HttpTasks Complete！！ =========");
                mDBHelper.updateTimeStamp(HTTP_SYNC_KEY, TimeUtil.getNowTimeSeconds());
            }
        });
        HttpSyncHelper.pushData(
                new RequestParams(
                        MODEL,
                        UID,
                        DID,
                        TYPE_USER_INFO,
                        USER_KEY,
                        "",
                        mDBHelper.getKeyTimeStamp(USER_KEY)), new Callback<JSONArray>() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) {
                        L.e("pushBodyInfoToMijia:" + jsonArray.toString());
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        L.e("pushBodyInfoToMijiaError:" + s);
                    }
                });
        HttpSyncHelper.pushData(
                new RequestParams(
                        MODEL,
                        UID,
                        DID,
                        TYPE_USER_INFO,
                        USER_REGISTER_KEY,
                        "",
                        TimeUtil.getNowTimeSeconds()), new Callback<JSONArray>() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) {
                        L.e("pushRegisterInfoToMijia:" + jsonArray.toString());
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        L.e("pushRegisterInfoToMijiaError:" + s);
                    }
                });
        finish();
    }

    @Override
    public void onDestroy() {
        asyncHttpManager.releaseHttpAsyncManager();
        ToastUtil.showToastNoRepeat(mContext, "清除成功，请重新进入插件！");
        super.onDestroy();
    }
}
