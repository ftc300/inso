package com.inshow.watch.android.sync.http.tasks;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.manager.SPManager;
import com.inshow.watch.android.model.PickVipEntity;
import com.inshow.watch.android.model.VipEntity;
import com.inshow.watch.android.provider.DBHelper;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.sync.http.bean.HttpVip;
import com.inshow.watch.android.sync.http.bean.HttpVipState;
import com.inshow.watch.android.tools.Constants;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.Rom;
import com.inshow.watch.android.tools.TimeUtil;
import com.xiaomi.smarthome.device.api.Callback;

import org.json.JSONArray;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static com.inshow.watch.android.sync.http.HttpSyncHelper.INSHOW_HTTP_START_TIME;
import static com.inshow.watch.android.tools.Constants.HttpConstant.TYPE_USER_INFO;
import static com.inshow.watch.android.tools.Constants.ON;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_INCOMING_SWITCH;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_VIP_OLD_PLUGIN;
import static com.inshow.watch.android.tools.Constants.TimeStamp.VIP_KEY;
import static com.inshow.watch.android.tools.MessUtil.getSystemContact;
import static com.inshow.watch.android.tools.MessUtil.showVipOta;

/**
 * Created by chendong on 2017/6/5.
 */

public class VipTask extends BaseTask {

    public VipTask(Context context, DBHelper dbHelper) {
        super(context, dbHelper);
        TAG = "VipTask";
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
        return VIP_KEY;
    }

    @Override
    protected boolean isVipOldPlugin() {
        return (Boolean) SPManager.get(context,SP_VIP_OLD_PLUGIN,true);
    }

    @Override
    protected boolean saveToLocal(String jsonValue) {
        L.d("saveToLocal jsonValue：" + jsonValue);
        try {
            boolean isOldPlugin = jsonValue.contains("[");
            if (Rom.isMIUI()) {
                if (isOldPlugin) {
                    L.d("jsonValue.contains(\"[\") isOldPlugin" );
                    SPManager.put(context, SP_INCOMING_SWITCH, false);
                    SPManager.put(context, SP_VIP_OLD_PLUGIN, false);
                    updateKey(TimeUtil.getNowTimeSeconds());
                    uploadToMijia();
                    showVipOta(context);
                } else {
                    SPManager.put(context, SP_VIP_OLD_PLUGIN, false);
                    HttpVipState state = AppController.getGson().fromJson(jsonValue, HttpVipState.class);
                    if (!TextUtils.isEmpty(state.state)) {
                        SPManager.put(context, SP_INCOMING_SWITCH, "on".equals(state.state));
                    }
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            L.e(TAG + "=>saveVipInfoToLocal Exception::" + e.getMessage());
        }
        return true;
//        try {
//            if (Rom.isMIUI()) {
//                L.e(TAG + "=>saveVipInfoToLocal start");
//                JsonParser parser = new JsonParser();
//                JsonArray array = parser.parse(jsonValue).getAsJsonArray();
//                if (array.size() >= 0) {
//                    //老插件服务端有数据
//                    SPManager.put(context, SP_INCOMING_SWITCH, false);
//                    uploadToMijia();
//                    showVipOta(context);
//                    isOldPlugin = true;
//                }
//            }
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            L.e(TAG + "=>saveVipInfoToLocal Exception::" + e.getMessage());
//        }finally {
//            L.d("finally");
//            if (!isOldPlugin) {
//                try {
//                    if (Rom.isMIUI()) {
//                        HttpVipState state = AppController.getGson().fromJson(jsonValue, HttpVipState.class);
//                        if (!TextUtils.isEmpty(state.state)) {
//                            SPManager.put(context, SP_INCOMING_SWITCH, "on".equals(state.state));
//                        }
//                    }
//                    L.e(TAG + "=>saveVipInfoToLocal end");
//                    return true;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    L.e(TAG + "=>saveVipInfoToLocal Exception::" + e.getMessage());
//                    return false;
//                }
//            }
//            return false;
//        }
    }


    @Override
    protected boolean uploadToMijia() {
        L.d("vip start uploadToMijia ");
        boolean b = (Boolean) SPManager.get(context, SP_INCOMING_SWITCH, false);
        HttpVipState vipState = new HttpVipState(b ? "on" : "off");
        HttpSyncHelper.pushData(
                new RequestParams(
                        MODEL,
                        UID,
                        DID,
                        TYPE_USER_INFO,
                        VIP_KEY,
                        AppController.getGson().toJson(vipState),
                        getLocalKeyTime()), new Callback<JSONArray>() {
                    @Override
                    public void onSuccess(JSONArray jsonArray) {
                        L.e("pushVipInfoToMijia:" + jsonArray.toString());
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        L.e("pushVipInfoToMijia onFailure:" + s);
                    }
                });
        return true;
    }

    @Override
    protected int getLimit() {
        return 1;
    }
}
//{"uid":"1263338353","time":1543396921,"type":"prop","value":"{\"state\":\"off\"}","did":"user_info","key":"vip"}
//[{"uid":"1263338353","time":1543399094,"type":"prop","value":"{\"state\":\"off\"}","did":"user_info","key":"vip"}]
//{"id":"9936713","uid":"1263338353","did":"blt.3.qn9kr3h48800","data_key":"vip","type":"user_info","value":"{\"state\":\"on\"}","timeAt":"1543398585","system":"Android","create_time":"2018-11-28 17:49:45"}
