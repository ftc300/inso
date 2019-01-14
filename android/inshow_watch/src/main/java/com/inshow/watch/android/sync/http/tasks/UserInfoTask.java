package com.inshow.watch.android.sync.http.tasks;

import android.content.Context;

import com.google.gson.JsonSyntaxException;
import com.inshow.watch.android.dao.WatchUserDao;
import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.manager.SPManager;
import com.inshow.watch.android.provider.DBHelper;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.bean.HttpUserInfo;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.tools.Constants;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.TimeUtil;
import com.xiaomi.smarthome.device.api.Callback;

import org.json.JSONArray;

import static com.inshow.watch.android.sync.http.HttpSyncHelper.INSHOW_HTTP_START_TIME;
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

/**
 * Created by chendong on 2017/5/12.
 */
public class UserInfoTask extends BaseTask {

    public UserInfoTask(Context context, DBHelper dbHelper) {
        super(context, dbHelper);
        TAG = "UserInfoTask";
    }

    @Override
    protected RequestParams getRequestParams() {
        return new RequestParams(
                MAC,
                UID,
                DID,
                Constants.HttpConstant.TYPE_USER_INFO,
                getKey() ,
                TimeUtil.getNowTimeSeconds(),
                getLimit(),
                INSHOW_HTTP_START_TIME,
                TimeUtil.getNowTimeSeconds()
        );
    }

    @Override
    protected String getKey() {
        return USER_KEY;
    }

    @Override
    protected boolean saveToLocal(String jsonValue) {
        try {
            HttpUserInfo info  = AppController.getGson().fromJson(jsonValue,HttpUserInfo.class);
            if(info!=null){
                L.e(TAG+"=>saveBodyInfoTolocal Success");
            SPManager.put(context,SP_ARG_BIRTH,info.birth);
            SPManager.put(context,SP_ARG_HEIGHT,info.height);
            SPManager.put(context,SP_ARG_WEIGHT,info.weight);
            SPManager.put(context,SP_ARG_GENDER,info.gender);
            mDBHelper.updateUser(new WatchUserDao(info.height,info.weight,info.gender,info.birth));
             L.e(TAG+"=>saveBodyInfoTolocal Success");
            }else{
             L.e(TAG+"=>HttpUserInfo info equals null!");
            }
            return  true;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            L.e(TAG+"=>saveBodyInfoTolocal Error:"+e.getMessage());
            return  false;
        }
    }

    @Override
    protected boolean uploadToMijia() {
        HttpUserInfo bean = new HttpUserInfo(
                (int)SPManager.get(context,SP_ARG_WEIGHT,WEIGHT_DEFAULT),
                (int)SPManager.get(context,SP_ARG_HEIGHT,HEIGHT_DEFAULT),
                (String) SPManager.get(context,SP_ARG_GENDER,GENDER_DEFAULT),
                (String)SPManager.get(context,SP_ARG_BIRTH, BIRTH_DEFAULT)
        );
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
                        L.e(TAG+"=>pushBodyInfoToMijia Success:"+jsonArray.toString());
                    }
                    @Override
                    public void onFailure(int i, String s) {
                        L.e(TAG+"=>pushBodyInfoToMijia Error:"+s);
                    }
                });
        return true;
    }

    @Override
    protected int getLimit() {
        return 1;
    }

}
