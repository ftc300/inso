package com.inshow.watch.android.act.mainpagelogic;

import android.content.Context;

import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.manager.SPManager;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.bean.HttpTime;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.sync.http.bean.HttpBatteryLevel;
import com.inshow.watch.android.sync.http.bean.HttpDfu;
import com.inshow.watch.android.sync.http.bean.HttpWatchState;
import com.inshow.watch.android.tools.Constants;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.TimeUtil;
import com.xiaomi.smarthome.device.api.Callback;

import org.json.JSONArray;

import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_DID;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_MODEL;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_USERID;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_DEBUG_DFU;
import static com.inshow.watch.android.tools.Constants.TimeStamp.BATTERY_LEVEL_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.DFU_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.REPORT_STATUS_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.TIMESTAMP_INFO;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2018/5/2
 * @ 描述:
 */


public class MainHelper {
    /**
     * 电池电压
     */
    public static  void pushBatteryLevelInfo(Context mContext,HttpBatteryLevel bean) {
        HttpSyncHelper.pushData(new RequestParams(
                (String) SPManager.get(mContext, SP_ARG_MODEL, ""),
                (String) SPManager.get(mContext, SP_ARG_USERID, ""),
                (String) SPManager.get(mContext, SP_ARG_DID, ""),
                Constants.HttpConstant.TYPE_USER_INFO,
                BATTERY_LEVEL_KEY,
                AppController.getGson().toJson(bean),
                TimeUtil.getNowTimeSeconds()
        ), new Callback<JSONArray>() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                L.e("pushBatteryLevelInfo onSuccess:" + jsonArray.toString());
            }

            @Override
            public void onFailure(int i, String s) {
                L.e("pushBatteryLevelInfo onFailure:" + s);
            }
        });
    }
    /**
     * 电池电压
     */
    public static  void pushWatchState(Context mContext,HttpWatchState bean) {
        HttpSyncHelper.pushData(new RequestParams(
                (String) SPManager.get(mContext, SP_ARG_MODEL, ""),
                (String) SPManager.get(mContext, SP_ARG_USERID, ""),
                (String) SPManager.get(mContext, SP_ARG_DID, ""),
                Constants.HttpConstant.TYPE_PROP,
                REPORT_STATUS_KEY,
                AppController.getGson().toJson(bean),
                TimeUtil.getNowTimeSeconds()
        ), new Callback<JSONArray>() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                L.e("pushWatchState onSuccess:" + jsonArray.toString());
            }

            @Override
            public void onFailure(int i, String s) {
                L.e("pushWatchState onFailure:" + s);
            }
        });
    }


    public static void pushTimeStampInfo(Context mContext,long serverTime, long localTime) {
        HttpTime bean = new HttpTime(serverTime, localTime);
        HttpSyncHelper.pushData(new RequestParams(
                (String) SPManager.get(mContext, SP_ARG_MODEL, ""),
                (String) SPManager.get(mContext, SP_ARG_USERID, ""),
                (String) SPManager.get(mContext, SP_ARG_DID, ""),
                Constants.HttpConstant.TYPE_USER_INFO,
                TIMESTAMP_INFO,
                AppController.getGson().toJson(bean),
                TimeUtil.getNowTimeSeconds()
        ), new Callback<JSONArray>() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                L.e("pushTimeStampInfo onSuccess:" + jsonArray.toString());
            }

            @Override
            public void onFailure(int i, String s) {
                L.e("pushTimeStampInfo onFailure:" + s);
            }
        });
    }

    /**
     * 固件版本
     */
     public static void pushDfuInfo(final Context mContext, final String mCurrentV) {
        HttpDfu bean = new HttpDfu(mCurrentV);
        HttpSyncHelper.pushData(new RequestParams(
                (String) SPManager.get(mContext, SP_ARG_MODEL, ""),
                (String) SPManager.get(mContext, SP_ARG_USERID, ""),
                (String) SPManager.get(mContext, SP_ARG_DID, ""),
                Constants.HttpConstant.TYPE_USER_INFO,
                DFU_KEY,
                AppController.getGson().toJson(bean),
                TimeUtil.getNowTimeSeconds()
        ), new Callback<JSONArray>() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                L.e("pushDfuInfo:" + jsonArray.toString());
                SPManager.put(mContext, SP_DEBUG_DFU, mCurrentV);
            }

            @Override
            public void onFailure(int i, String s) {
                L.e("pushDfuInfo:" + s);
            }
        });
    }




}
