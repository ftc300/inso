package com.inshow.watch.android.sync.http.tasks;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.provider.DBHelper;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.sync.http.ResponseBase;
import com.inshow.watch.android.tools.L;
import com.xiaomi.smarthome.device.api.Callback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_DID;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_MAC;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_MODEL;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_USERID;

/**
 * Created by chendong on 2017/5/12.
 */

public abstract class BaseTask implements Runnable {
    protected String MAC;
    protected String MODEL;
    protected String DID;
    protected String UID;
    protected String TAG;
    protected Context context;
    protected HttpSyncHelper syncHelper;
    protected DBHelper mDBHelper;
    protected int mRemoteTime;

    public BaseTask(Context context,DBHelper dbHelper) {
        this.context = context;
        syncHelper = new HttpSyncHelper(dbHelper);
        mDBHelper = dbHelper;
        MAC = mDBHelper.getCache(SP_ARG_MAC);
        MODEL = mDBHelper.getCache(SP_ARG_MODEL);
        UID = mDBHelper.getCache(SP_ARG_USERID);
        DID = mDBHelper.getCache(SP_ARG_DID);
    }

    @Override
    public void run() {
            HttpSyncHelper.fetchArrayData(getRequestParams(), new Callback<JSONArray>() {
                @Override
                public void onSuccess(JSONArray json) {
                    L.e(TAG + "====Fetch From MiJia Success:===" + json.toString());
                            JsonParser parser = new JsonParser();
                            JsonArray array = parser.parse(json.toString()).getAsJsonArray();
                            List<ResponseBase> dst = new ArrayList<>();
                            for (JsonElement obj : array) {
                                ResponseBase responseBase = AppController.getGson().fromJson(obj, ResponseBase.class);
                                dst.add(responseBase);
                            }
                            if (getLimit() == 1) {
                                if (dst.size() > 0) {//remote have data
                                    ResponseBase responseBase = dst.get(0);
                                    mRemoteTime = responseBase.time;
                                    if (mRemoteTime > getLocalKeyTime() || isVipOldPlugin()) {//remote newer than local
                                        if (saveToLocal(responseBase.value)) {//save to local
                                            updateKey(mRemoteTime);//if save success update local timestamp
                                        }
                                    } else if (mRemoteTime < getLocalKeyTime()) {
                                        uploadToMijia();
                                    } else if (mRemoteTime == getLocalKeyTime()) {
                                        L.e(TAG + " ===Good Luck!Needn't UpOrDown!===");
                                    }
                                } else {//remote don't have data
                                    if(getLocalKeyTime()>0) {
                                        uploadToMijia();
                                    }
                                }
                            }
                            L.e(TAG + "====Fetch && Operate Success:===");
                    }

                @Override
                public void onFailure(int i, String s) {
                    L.e(TAG + "=====Fetch From MiJia Error:====" + s);
                }
            });
    }

    /**
     * vip全部提醒 造成的问题 每次都存储吧
     * @return
     */
    protected boolean isVipOldPlugin(){
        return false;
    }

    protected abstract RequestParams getRequestParams();

    protected abstract String getKey();

    protected abstract boolean saveToLocal(String jsonValue);

    //push到米家的时候，注意time为Key本地时间戳，不是当前时间
    protected abstract boolean uploadToMijia();

    protected int getLocalKeyTime() {
        return  mDBHelper.getKeyTimeStamp(getKey());
    }

    protected void updateKey(int time) {
        mDBHelper.updateTimeStamp(getKey(), time);
    }

    protected abstract int getLimit();

}
