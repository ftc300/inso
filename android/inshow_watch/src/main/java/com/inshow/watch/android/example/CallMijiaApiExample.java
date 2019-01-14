package com.inshow.watch.android.example;

import com.google.gson.Gson;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.sync.http.bean.HttpInterval;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.tools.Constants;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.TimeUtil;
import com.xiaomi.smarthome.device.api.Callback;
import com.xiaomi.smarthome.device.api.DeviceStat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chendong on 2017/5/9.
 */
public class CallMijiaApiExample {

    private void test() {
        try {
            JSONObject arry = new JSONObject();
            JSONObject dataObj = new JSONObject();
            JSONObject dataObj1 = new JSONObject();
            dataObj.put("name", "zhangsan");
            dataObj1.put("name", "lisi");
            arry.put("0", dataObj);
            arry.put("1", dataObj1);
            L.e(arry.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void example() {
        DeviceStat deviceStat = new DeviceStat();

        HttpInterval bean =null;//= new HttpInterval(20, "on");
        HttpSyncHelper.pushData(new RequestParams(deviceStat.model,
                deviceStat.userId,
                deviceStat.did,
                Constants.HttpConstant.TYPE_USER_INFO,
                "test2017/05/05",
                new Gson().toJson(bean),
                TimeUtil.getNowTimeSeconds()), new Callback<JSONArray>() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                L.e("setUserDeviceDataSucess:" + jsonArray.toString());
            }

            @Override
            public void onFailure(int i, String s) {
                L.e("setUserDeviceDataError:" + s);
            }
        });
        HttpSyncHelper.pushData(new RequestParams(deviceStat.model,
                deviceStat.userId,
                deviceStat.did,
                Constants.HttpConstant.TYPE_USER_INFO,
                "test2017/05/05",
                TimeUtil.getNowTimeSeconds(),
                10,
                1,
                TimeUtil.getNowTimeSeconds()
        ), new Callback<JSONArray>() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                L.e("getUserDeviceDataSucess:" + jsonArray.toString());
            }

            @Override
            public void onFailure(int i, String s) {
                L.e("getUserDeviceDataError:" + s);
            }
        });
//        HttpSyncHelper.getAppConfig(new RequestParams("test", 1, "en", "10103"), new Callback<Object>() {
//            @Override
//            public void onSuccess(Object result) {
//                L.e("getAppConfigSucess:" + result.toString());
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//                L.e("getAppConfigError:" + s);
//            }
//        });

//获取用户信息
//                L.e("userid:"+deviceStat.userId+",deviceid:"+deviceStat.did);
//                XmPluginHostApi.instance().getUserInfo(deviceStat.userId, new Callback<UserInfo>() {
//                    @Override
//                    public void onSuccess(UserInfo userInfo) {
//
//                    }
//
//                    @Override
//                    public void onFailure(int i, String s) {
//
//                    }
//                });
    }
}
