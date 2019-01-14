package com.inshow.watch.android.sync.http.tasks;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import com.inshow.watch.android.dao.IntervalDao;
import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.provider.DBHelper;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.sync.http.bean.HttpInterval;
import com.inshow.watch.android.tools.Constants;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.TimeUtil;
import com.xiaomi.smarthome.device.api.Callback;

import org.json.JSONArray;

import static com.inshow.watch.android.sync.http.HttpSyncHelper.INSHOW_HTTP_START_TIME;
import static com.inshow.watch.android.tools.Constants.HttpConstant.TYPE_USER_INFO;
import static com.inshow.watch.android.tools.Constants.OFF;
import static com.inshow.watch.android.tools.Constants.TimeStamp.INTERVAL_ALARM_KEY;

/**
 * Created by chendong on 2017/5/12.
 */
public class IntervalTask extends BaseTask {


    public IntervalTask(Context context, DBHelper dbHelper) {
        super(context, dbHelper);
        TAG = "IntervalTask";
    }

    @Override
    protected RequestParams getRequestParams() {
        return new RequestParams(
                MAC,
                UID,
                DID,
                Constants.HttpConstant.TYPE_USER_INFO,
                getKey(),
                TimeUtil.getNowTimeSeconds(),
                getLimit(),
                INSHOW_HTTP_START_TIME,
                TimeUtil.getNowTimeSeconds()
        );
    }

    @Override
    protected String getKey() {
        return INTERVAL_ALARM_KEY;
    }

    @Override
    protected boolean saveToLocal(String jsonValue) {
        try {
            HttpInterval info = AppController.getGson().fromJson(jsonValue, HttpInterval.class);
            mDBHelper.updateInterval(new IntervalDao(info.interval==0 ? 60:info.interval, info.start,TextUtils.isEmpty(info.status)?OFF:info.status ));
            L.e(TAG+"=>saveTolocal Success");
            return true;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            L.e(TAG+"=>saveTolocal Error:"+e.getMessage());
            return false;
        }
    }

    @Override
    protected boolean uploadToMijia() {
        IntervalDao dao = mDBHelper.getInterval();
        HttpInterval bean = new HttpInterval(dao.time ,dao.start, TextUtils.isEmpty(dao.status)?OFF:dao.status);
        HttpSyncHelper.pushData(
                new RequestParams(
                        MODEL,
                        UID,
                        DID,
                        TYPE_USER_INFO,
                        getKey(),
                        AppController.getGson().toJson(bean),
                        getLocalKeyTime()), new Callback<JSONArray>() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) {
                        L.e(TAG+"=>pushIntervalToMijia Success:" + jsonArray.toString());
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        L.e(TAG+"=>pushIntervalToMijia Error:" + s);
                    }
                });
        return true;
    }

    @Override
    protected int getLimit() {
        return 1;
    }

}
