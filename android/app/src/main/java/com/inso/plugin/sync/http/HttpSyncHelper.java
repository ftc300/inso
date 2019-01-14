package com.inso.plugin.sync.http;

import com.inso.plugin.provider.DBHelper;

import static com.inso.plugin.tools.Constants.TimeStamp.INTERVAL_ALARM_KEY;
import static com.inso.plugin.tools.Constants.TimeStamp.NORMAL_ALARM_KEY;
import static com.inso.plugin.tools.Constants.TimeStamp.USER_KEY;
import static com.inso.plugin.tools.Constants.TimeStamp.USER_REGISTER_KEY;
import static com.inso.plugin.tools.Constants.TimeStamp.VIBRATE_SETTING_KEY;
import static com.inso.plugin.tools.Constants.TimeStamp.VIP_KEY;
import static com.inso.plugin.tools.Constants.TimeStamp.WORLD_CITY_KEY;

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


}
