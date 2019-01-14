package com.inshow.watch.android.sync.http;

import com.inshow.watch.android.provider.DBHelper;
import com.inshow.watch.android.tools.L;
import com.xiaomi.smarthome.device.api.Callback;
import com.xiaomi.smarthome.device.api.Parser;
import com.xiaomi.smarthome.device.api.XmPluginHostApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.inshow.watch.android.tools.Constants.HttpConstant.URL_GET_APP_CONFIG;
import static com.inshow.watch.android.tools.Constants.HttpConstant.URL_GET_USER_DEVICE_DATA;
import static com.inshow.watch.android.tools.Constants.HttpConstant.URL_GET_UTC;
import static com.inshow.watch.android.tools.Constants.HttpConstant.URL_SET_USER_DEVICE_DATA;
import static com.inshow.watch.android.tools.Constants.TimeStamp.INTERVAL_ALARM_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.NORMAL_ALARM_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.USER_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.USER_REGISTER_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.VIBRATE_SETTING_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.VIP_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.WORLD_CITY_KEY;

/**
 * Created by chendong on 2017/5/12.
 */

public class HttpSyncHelper {

    private DBHelper mDbHelper;

    public HttpSyncHelper(DBHelper dbHelper) {
        this.mDbHelper = dbHelper;
    }

    public int getLocalRegisterKeyTime(){
        return mDbHelper.getKeyTimeStamp(USER_REGISTER_KEY);
    }

    public int getLocalUserKeyTime(){
        return mDbHelper.getKeyTimeStamp(USER_KEY);
    }

    public int getLocalAlarmKeyTime(){
        return mDbHelper.getKeyTimeStamp(NORMAL_ALARM_KEY);
    }

    public int getLocalWorldCityKeyTime(){
        return mDbHelper.getKeyTimeStamp(WORLD_CITY_KEY);
    }

    public int getLocalIntervalKeyTime(){
        return mDbHelper.getKeyTimeStamp(INTERVAL_ALARM_KEY);
    }

    public int getLocalVibrationKeyTime(){
        return mDbHelper.getKeyTimeStamp(VIBRATE_SETTING_KEY);
    }

    public int getLocalVipTime(){
        return mDbHelper.getKeyTimeStamp(VIP_KEY);
    }

    public static final int INSHOW_HTTP_START_TIME = 1493568000;//2017/05/01 00:00:00

    @SuppressWarnings("unchecked")
    public static void pushData(RequestParams params, Callback<JSONArray> callback) {
        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("uid", params.uid);
            dataObj.put("did", params.did);
            dataObj.put("type", params.type);
            dataObj.put("time", params.time);
            dataObj.put("key", params.key);
            dataObj.put("value", params.value);
        } catch (JSONException var12) {
            if (callback != null) {
                callback.onFailure(-1, var12.toString());
                return;
            }
        }
        L.e("setUserDeviceData=>ReqUrl:"+ URL_SET_USER_DEVICE_DATA +",ReqParam:" + dataObj.toString());
        XmPluginHostApi.instance().callSmartHomeApi(params.model, URL_SET_USER_DEVICE_DATA, dataObj, callback, new Parser() {
            public JSONArray parse(String result) throws JSONException {
                JSONObject response = new JSONObject(result);
                return response.getJSONArray("result");
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static void fetchArrayData(RequestParams params,Callback<JSONArray> callback){
        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("uid", params.uid);
            dataObj.put("did", params.did);
            dataObj.put("type", params.type);
            dataObj.put("key",  params.key);
            dataObj.put("limit", params.limit);
            dataObj.put("time", params.time);
            dataObj.put("time_start", params.timeStart);
            dataObj.put("time_end", params.timeEnd);
        } catch (JSONException var12) {
            if(callback != null) {
                callback.onFailure(-1, var12.toString());
                return;
            }
        }
        L.e("getUserDeviceData=>ReqUrl:"+ URL_GET_USER_DEVICE_DATA +",ReqParam:"+dataObj.toString());
        XmPluginHostApi.instance().callSmartHomeApi(params.model, URL_GET_USER_DEVICE_DATA, dataObj, callback, new Parser() {
            public JSONArray parse(String result) throws JSONException {
                JSONObject response = new JSONObject(result);
                return response.getJSONArray("result");
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static void getAppConfig(String MODEL,Callback<Object> callback){
        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("name","config_version");
            dataObj.put("version",1);
            dataObj.put("lang", "en");
            dataObj.put("app_id", "10103");
        } catch (JSONException var12) {
            if(callback != null) {
                callback.onFailure(-1, var12.toString());
                return;
            }
        }
        L.e("getAppConfig=>ReqUrl:"+ URL_GET_APP_CONFIG +",ReqParam:"+dataObj.toString());
        XmPluginHostApi.instance().callSmartHomeApi(MODEL, URL_GET_APP_CONFIG, dataObj, callback, new Parser() {
            public Object parse(String result) throws JSONException {
                return result;
            }
        });
    }


    @SuppressWarnings("unchecked")
    public static void getAppConfig( RequestParams params,Callback<Object> callback){
        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("name",params.name);
            dataObj.put("version",params.version);
            dataObj.put("lang", "en");
            dataObj.put("app_id","10103");
        } catch (JSONException var12) {
            if(callback != null) {
                callback.onFailure(-1, var12.toString());
                return;
            }
        }
        L.e("getAppConfig=>ReqUrl:"+ URL_GET_APP_CONFIG +",ReqParam:"+dataObj.toString());
        XmPluginHostApi.instance().callSmartHomeApi(params.model, URL_GET_APP_CONFIG, dataObj, callback, new Parser() {
            public Object parse(String result) throws JSONException {
                return result;
            }
        });
    }
    @SuppressWarnings("unchecked")
    public static void getUTC(String model,Callback<Object> callback){
        XmPluginHostApi.instance().callSmartHomeApi(model, URL_GET_UTC, new JSONObject(), callback, new Parser() {
            public Object parse(String result) throws JSONException {
                return result;
            }
        });
    }



}
