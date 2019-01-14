package com.inshow.watch.android.sync.http.tasks;

import android.content.Context;
import com.google.gson.JsonSyntaxException;
import com.inshow.watch.android.act.mainpagelogic.LowPowerManager;
import com.inshow.watch.android.dao.VibrationDao;
import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.provider.DBHelper;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.sync.http.bean.HttpVibrate;
import com.inshow.watch.android.tools.Constants;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.TimeUtil;
import com.xiaomi.smarthome.device.api.Callback;
import org.json.JSONArray;
import java.util.Locale;
import static com.inshow.watch.android.sync.http.HttpSyncHelper.INSHOW_HTTP_START_TIME;
import static com.inshow.watch.android.tools.Constants.HttpConstant.TYPE_USER_INFO;
import static com.inshow.watch.android.tools.Constants.TimeStamp.VIBRATE_SETTING_KEY;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/10/31
 * @ 描述:
 */
public class VibrateTask extends BaseTask {


    public VibrateTask(Context context, DBHelper dbHelper) {
        super(context, dbHelper);
        TAG = "VibrateTask";
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
        return VIBRATE_SETTING_KEY;
    }

    @Override
    protected boolean saveToLocal(String jsonValue) {
        try {
            HttpVibrate info = AppController.getGson().fromJson(jsonValue, HttpVibrate.class);
            mDBHelper.updateVibration(new VibrationDao(info.isdoubletime == 1, info.isnodisturb == 1, getTime(info.starttime), getTime(info.endtime)));
            LowPowerManager.getInstance().setAutoSwitchState(context,info.autoclosevibrate == 1);
            L.e(TAG + "=>saveTolocal Success");
            return true;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            L.e(TAG + "=>saveTolocal Error:" + e.getMessage());
            return false;
        }
    }

    @Override
    protected boolean uploadToMijia() {
        VibrationDao dao = mDBHelper.getVibrationInfo();
        HttpVibrate bean = new HttpVibrate(dao.stronger ? 1 : 0, dao.notdisturb ? 1 : 0, getTime(dao.startTime), getTime(dao.endTime), LowPowerManager.getInstance().getAutoSwitchState(context)?1:0);
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
                        L.e(TAG + "=>pushVibrationToMijia Success:" + jsonArray.toString());
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        L.e(TAG + "=>pushVibrationToMijia Error:" + s);
                    }
                });
        return true;
    }

    @Override
    protected int getLimit() {
        return 1;
    }

    private int getTime(String serverTime) {
        String[] source = serverTime.split(":");
        return Integer.parseInt(source[0]) * 60 + Integer.parseInt(source[1]);
    }

    private String getTime(int localTime) {
        return String.format(Locale.getDefault(), "%02d:%02d", localTime / 60, localTime / 60);
    }


}
