package com.inshow.watch.android.sync.http.tasks;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.inshow.watch.android.dao.PreferCitiesDao;
import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.provider.DBHelper;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.bean.HttpWorldCity;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.tools.Constants;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.TimeUtil;
import com.xiaomi.smarthome.device.api.Callback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.inshow.watch.android.sync.http.HttpSyncHelper.INSHOW_HTTP_START_TIME;
import static com.inshow.watch.android.tools.Constants.HttpConstant.TYPE_USER_INFO;
import static com.inshow.watch.android.tools.Constants.TimeStamp.WORLD_CITY_KEY;

/**
 * Created by chendong on 2017/5/12.
 */
public class WorldCityTask extends BaseTask {

    public WorldCityTask(Context context, DBHelper dbHelper) {
        super(context, dbHelper);
        TAG = "WorldCityTask";
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
        return WORLD_CITY_KEY;
    }

    @Override
    protected boolean saveToLocal(String jsonValue) {
        L.e(TAG+"====saveToLocal Start");
        try {
            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(jsonValue).getAsJsonArray();
            List<HttpWorldCity> list  = new ArrayList<>();
            for(JsonElement obj : array ){
                HttpWorldCity city = AppController.getGson().fromJson( obj , HttpWorldCity.class);
                list.add(city);
            }
            if(list.size() >=1) {
                L.e("saveToLocal list.size() >=1");
                mDBHelper.syncWorldCity(list);
            }else{
                L.e("saveToLocal list.size() < 1");
                mDBHelper.initPreferCity(mDBHelper.getWritableDatabase());
            }
            L.e(TAG+"====saveToLocal End");
            return  true;
        } catch (Exception e) {
            e.printStackTrace();
            L.e(TAG+"====saveToLocal Exception:"+e.getMessage());
            return  false;
        }
    }

    @Override
    protected boolean uploadToMijia() {
        List<HttpWorldCity> list = new ArrayList<>();
        List<PreferCitiesDao> dbSrc = mDBHelper.getAllPreferCities();
        for(PreferCitiesDao item : dbSrc) {
            list.add(new HttpWorldCity(item.id,item.isSel,item.zh_cn,item.zh_tw,item.zh_hk,item.zone,item.en));
        }
        if(list.size() >=1) {
            HttpSyncHelper.pushData(
                    new RequestParams(
                            MODEL,
                            UID,
                            DID,
                            TYPE_USER_INFO,
                            getKey(),
                            AppController.getGson().toJson(list),
                            getLocalKeyTime()), new Callback<JSONArray>() {
                        @Override

                        public void onSuccess(JSONArray jsonArray) {
                            L.e(TAG + "=>pushWorldCityInfoToMijia Success:" + jsonArray.toString());
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            L.e(TAG + "=>pushWorldCityInfoToMijia Error:" + s);
                        }
                    });
        }
        return true;
    }

    @Override
    protected int getLimit() {
        return 1;
    }

}
