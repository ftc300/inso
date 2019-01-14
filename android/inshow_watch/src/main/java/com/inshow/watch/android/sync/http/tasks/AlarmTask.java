package com.inshow.watch.android.sync.http.tasks;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.inshow.watch.android.dao.AlarmDao;
import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.provider.DBHelper;
import com.inshow.watch.android.sync.http.bean.HttpAlarm;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.tools.Constants;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.TimeUtil;
import com.xiaomi.smarthome.device.api.Callback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.inshow.watch.android.tools.Constants.HttpConstant.TYPE_USER_INFO;
import static com.inshow.watch.android.tools.Constants.OFF;
import static com.inshow.watch.android.tools.Constants.ON;
import static com.inshow.watch.android.tools.Constants.TimeStamp.NORMAL_ALARM_KEY;

/**
 * Created by chendong on 2017/5/12.
 */
public class AlarmTask extends BaseTask {

    public AlarmTask(Context context, DBHelper dbHelper) {
        super(context, dbHelper);
        TAG = "AlarmTask";
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
                HttpSyncHelper.INSHOW_HTTP_START_TIME,
                TimeUtil.getNowTimeSeconds()
        );
    }

    @Override
    protected String getKey() {
        return NORMAL_ALARM_KEY;
    }

    @Override
    protected boolean saveToLocal(String jsonValue) {
        try {
            L.e(TAG + "=>saveAlarmInfoToLocal start");
            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(jsonValue).getAsJsonArray();
            List<AlarmDao> dst = new ArrayList<>();
            for(JsonElement obj : array ){
                HttpAlarm alarm = AppController.getGson().fromJson( obj , HttpAlarm.class);
                dst.add(new AlarmDao(alarm.id,alarm.time,alarm.nextring,getRepeatType(alarm.type),alarm.status.equals(ON),alarm.label));
            }
            mDBHelper.syncAlarm(dst);
            L.e(TAG + "=>saveAlarmInfoToLocal end");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            L.e(TAG + "=>saveAlarmInfoToLocal Exception::"+ e.getMessage());
            return false;
        }
    }

    @Override
    protected boolean uploadToMijia() {
        List<HttpAlarm> list = new ArrayList<>();
        List<AlarmDao> mDbSource = mDBHelper.getAllAlarm();
        for(AlarmDao item : mDbSource)
        {
            list.add(new HttpAlarm(item.id,item.status ?ON:OFF,item.seconds,item.extend,getListType(item.repeatType),item.desc));
        }
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
                        L.e(TAG + "=>pushAlarmInfoToMijia Success::" + jsonArray.toString());
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        L.e(TAG + "=>pushAlarmInfoToMijia Error::" + s);
                    }
                });
        return true;
    }

    @Override
    protected int getLimit() {
        return 1;
    }

    /**
     * 服务端规则：
     * 0: 响一次；1: 每天；2: 法定工作日；3: 法定节假日；4: 周一；5: 周二；6: 周三；7: 周四；8: 周五；9: 周六；10: 周日
     * 自己以前制定的规则：
     * 0: 响一次；1: 每天；2: 法定工作日；3: 法定节假日；4:周一至周五；5:自定义 eg:（周一|周二）,(5,0,1)
     * @param type
     * @return
     */
    private List<Integer> getListType(String type)
    {
        List<Integer> ret = new ArrayList<>() ;
        String[] src = type.split(",");
        if(src.length==1){
            if(Integer.parseInt(src[0])<4){
                ret.add(Integer.parseInt(src[0]));
                return ret;
            }else{//等于4
                for (int i =4;i<9;i++){
                    ret.add(i);
                }
                return ret;
            }
        }
        for(int i = 1;i<src.length;i++)
        {
            ret.add(Integer.parseInt(src[i])+4);
        }
        return ret;
    }



    private String getRepeatType( List<Integer> src){
        if(src.size()==1&&src.get(0)<4){
            return  String.valueOf(src.get(0));
        }else if(src.size()==5&&(!src.contains(9)&&!src.contains(10))){
            return "4";
        }else {
            String ret  = "5";
            for (Integer i : src)
            {
                ret += ",";
                ret += i - 4;
            }
            return  ret;
        }
    }
}
