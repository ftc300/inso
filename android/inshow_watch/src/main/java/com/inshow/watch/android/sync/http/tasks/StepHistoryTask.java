package com.inshow.watch.android.sync.http.tasks;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.inshow.watch.android.act.user.WatchUserInfoHelper;
import com.inshow.watch.android.dao.StepDao;
import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.provider.DBHelper;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.sync.http.ResponseBase;
import com.inshow.watch.android.sync.http.bean.HttpStepHistory;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.TimeUtil;
import com.xiaomi.smarthome.device.api.Callback;
import com.xiaomi.smarthome.device.api.Parser;
import com.xiaomi.smarthome.device.api.XmPluginHostApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.inshow.watch.android.tools.Constants.HttpConstant.TYPE_USER_INFO;
import static com.inshow.watch.android.tools.Constants.HttpConstant.URL_SET_USER_DEVICE_DATA;
import static com.inshow.watch.android.tools.Constants.TimeStamp.STEPS_KEY;

/**
 * Created by chendong on 2017/5/16.
 */
public class StepHistoryTask extends BaseTask {

    private final int NUM_PERPAGE = 10;
    private WatchUserInfoHelper userHelper;
    private JsonParser parser;
    private List<HttpStepHistory> stepHistory;
    private int mTempTime = TimeUtil.getNowTimeSeconds();
    private List<StepDao> source;

    public StepHistoryTask(Context context, DBHelper dbHelper) {
        super(context, dbHelper);
        TAG = "StepHistoryTask";
        userHelper = new WatchUserInfoHelper(context);
    }

    @Override
    protected RequestParams getRequestParams() {
        return new RequestParams(
                MAC,
                UID,
                DID,
                TYPE_USER_INFO,
                getKey(),
                TimeUtil.getNowTimeSeconds(),
                getLimit(),
                0,
                TimeUtil.getNowTimeSeconds()
        );
    }

    @Override
    protected String getKey() {
        return STEPS_KEY;
    }

    @Override
    protected boolean saveToLocal(String jsonValue) {
        source = new ArrayList<>();
        stepHistory = new ArrayList<>();
        parser = new JsonParser();
        fillDataFromRemote();
        return false;//needn't update timestamp
}

    /**
     * 递归分页读取米家数据
     */
    private void fillDataFromRemote() {
        HttpSyncHelper.fetchArrayData(new RequestParams(
                MAC,
                UID,
                DID,
                TYPE_USER_INFO,
                getKey(),
                TimeUtil.getNowTimeSeconds(),
                NUM_PERPAGE,
                0,
                mTempTime - 1
        ), new Callback<JSONArray>() {
            @Override
            public void onSuccess(JSONArray json) {
                JsonArray array = parser.parse(json.toString()).getAsJsonArray();
                L.e("JsonArray:"+json.toString());
                List<ResponseBase> baseArrayList = new ArrayList<>();
                for (JsonElement obj : array) {
                    ResponseBase responseBase = AppController.getGson().fromJson(obj, ResponseBase.class);
                    baseArrayList.add(responseBase);
//                    L.e("baseArrayList.add(responseBase):"+responseBase.value);
                }
                for (ResponseBase responseBase : baseArrayList) {
                    HttpStepHistory item = AppController.getGson().fromJson(responseBase.value, HttpStepHistory.class);
                    if(null!=item) {
                        stepHistory.add(item);
                    }
                }
                if (stepHistory.size() > 0) {
                    mTempTime = stepHistory.get(stepHistory.size() - 1).start;
                    if (mTempTime > getLocalKeyTime() && baseArrayList.size() > 0 && baseArrayList.size() % NUM_PERPAGE == 0) {
                        fillDataFromRemote();
                    } else {//data fill complete
//                        try {
                            for (HttpStepHistory item : stepHistory) {
                                L.e("HttpStepHistory item  start :" + item.start);
                                source.add(new StepDao(item.start, item.end, item.count, item.end - item.start,
                                        userHelper.getDistance(item.count),
                                        userHelper.getKCal(item.count),
                                        TimeUtil.getMD(item.start),
                                        TimeUtil.getWeekBegEnd(item.start),
                                        TimeUtil.getMon(item.start),
                                        TimeUtil.getYear(item.start)));
                            }
//                        } catch (Exception e) {
//                            L.e("HttpStepHistory item00"+e.getMessage());
//                            e.printStackTrace();
//                        }
                        mDBHelper.addSteps(source);
                    }
                }

            }

            @Override
            public void onFailure(int i, String s) {
                L.e(TAG + "=====Fetch From MiJia Error:====" + s);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean uploadToMijia() {
        List<HttpStepHistory> src = mDBHelper.getStepSyncData(mRemoteTime,TimeUtil.getTodayZero(mDBHelper.getSettingZone()));
        try {
            JSONObject data = new JSONObject();
            for (int i = 0; i < src.size(); i++) {
                String key = String.valueOf(i);
                HttpStepHistory item = src.get(i);
                JSONObject value = new JSONObject();
                value.put("uid", UID);
                value.put("did", DID);
                value.put("type", TYPE_USER_INFO);
                value.put("time", item.start);
                value.put("key", getKey());
                value.put("value", AppController.getGson().toJson(item));
                data.put(key, value);
            }
            L.e(TAG + "===>uploadToMijia=>ReqParam:" + data.toString());
            XmPluginHostApi.instance().callSmartHomeApi(MODEL, URL_SET_USER_DEVICE_DATA, data, new Callback<JSONArray>() {
                @Override
                public void onSuccess(JSONArray jsonArray) {
                    L.e(TAG + "===>uploadToMijia Success ======");
                }

                @Override
                public void onFailure(int i, String s) {
                    L.e(TAG + "===>uploadToMijia Fail ======");
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

    @Override
    protected int getLimit() {
        return 1;
    }
}
