package com.inshow.watch.android.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.inshow.watch.android.dao.AlarmDao;
import com.inshow.watch.android.dao.IntervalDao;
import com.inshow.watch.android.dao.PreferCitiesDao;
import com.inshow.watch.android.dao.StepDao;
import com.inshow.watch.android.dao.VibrationDao;
import com.inshow.watch.android.dao.WatchUserDao;
import com.inshow.watch.android.manager.SPManager;
import com.inshow.watch.android.model.VipEntity;
import com.inshow.watch.android.sync.http.bean.HttpCityRes;
import com.inshow.watch.android.sync.http.bean.HttpStepHistory;
import com.inshow.watch.android.sync.http.bean.HttpWorldCity;
import com.inshow.watch.android.tools.Configuration;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.MessUtil;
import com.inshow.watch.android.tools.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import static com.inshow.watch.android.MessageReceiver.deltaTimeFromUTC;
import static com.inshow.watch.android.provider.DBColumn.PreferCityColumns.IS_SEL;
import static com.inshow.watch.android.provider.DBColumn.VIP.CONTACTID;
import static com.inshow.watch.android.tools.Constants.ConfigVersion.FESTIVAL_CHINA;
import static com.inshow.watch.android.tools.Constants.ConfigVersion.VERSION_DEFAULT;
import static com.inshow.watch.android.tools.Constants.ConfigVersion.WORLD_CITY;
import static com.inshow.watch.android.tools.Constants.OFF;
import static com.inshow.watch.android.tools.Constants.ON;
import static com.inshow.watch.android.tools.Constants.SystemConstant.BJID;
import static com.inshow.watch.android.tools.Constants.SystemConstant.HKID;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_HAS_DEFAULT_CITY;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_USERID;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_INCOMING_SWITCH;
import static com.inshow.watch.android.tools.Constants.SystemConstant.TBID;
import static com.inshow.watch.android.tools.Constants.TimeStamp.DEVICE_SYNC_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.HTTP_SYNC_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.INTERVAL_ALARM_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.NORMAL_ALARM_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.STEPS_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.USER_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.USER_REGISTER_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.VIBRATE_SETTING_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.VIP_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.WORLD_CITY_KEY;

/**
 * Created by cd 2017/03/08
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASENAME = "inshow.db";
    public static int DB_VERSION = 6;
    private static volatile DBHelper mInstance;
    private Context context;

    public DBHelper(Context context) {
//        super(isDebug ? new DatabaseContext(context) : context,
//                MessUtil.getDataBaseName((String) SPManager.get(context, SP_ARG_USERID, "")),
//                null,
//                DB_VERSION);
        super(context, MessUtil.getDataBaseName((String) SPManager.get(context, SP_ARG_USERID, "")), null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTablesAndInit(db);
    }

    private void createTablesAndInit(SQLiteDatabase db) {
        //<editor-fold desc="Create Tables">
        final String CREATE_CACHE_TABLE = "CREATE TABLE IF NOT EXISTS [CACHE] (" +
                "  [KEY] TEXT NOT NULL UNIQUE, " +
                "  [VALUE] TEXT );";
        db.execSQL(CREATE_CACHE_TABLE);

        final String CREATE_ALARM_TABLE = "CREATE TABLE IF NOT EXISTS [AlarmClock] (" +
                "  [ID] INTEGER NOT NULL UNIQUE, " +
                "  [TIME] INTEGER, " +
                "  [EXTEND] INTEGER, " +
                "  [REPEATTYPE] NVARCHAR, " +
                "  [DESC] NVARCHAR, " +
                "  [STATUS] Boolean);";
        db.execSQL(CREATE_ALARM_TABLE);

        final String CREATE_SYNC_TABLE = "CREATE TABLE IF NOT EXISTS [Synchronous] (" +
                "  [KEY] NVARCHAR NOT NULL UNIQUE, " +
                "  [TIMESTAMP] INTEGER );";
        db.execSQL(CREATE_SYNC_TABLE);

        final String CREATE_VERSION_TABLE = "CREATE TABLE IF NOT EXISTS [ConfigVersion] (" +
                "  [NAME] NVARCHAR NOT NULL UNIQUE, " +
                "  [VERSION] INTEGER);";
        db.execSQL(CREATE_VERSION_TABLE);

        final String CREATE_VIP_TABLE = "CREATE TABLE IF NOT EXISTS [VIP] (" +
                "  [ID] INTEGER NOT NULL, " +
                "  [CONTACTID] INTEGER NOT NULL , " +
                "  [NUMBER] NVARCHAR, " +
                "  [NAME] NVARCHAR," +
                "  [STATUS] Boolean ," +
                "  CONSTRAINT CONTACT_ID UNIQUE (NUMBER,NAME)  );";
        db.execSQL(CREATE_VIP_TABLE);

        final String CREATE_VIBRATION_TABLE = "CREATE TABLE IF NOT EXISTS [VIBRATION] (" +
                "  [ID] INTEGER NOT NULL UNIQUE, " +
                "  [STRONGER_STATUS] Boolean ," +
                "  [NOTDISTURB_STATUS] Boolean ," +
                "  [START_TIME] INTEGER ," +
                "  [END_TIME] INTEGER );";
        db.execSQL(CREATE_VIBRATION_TABLE);

        final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS [WatchUser] (" +
                "  [ID] INTEGER NOT NULL UNIQUE, " +
                "  [WEIGHT] INTEGER, " +
                "  [BIRTH] NVARCHAR, " +
                "  [GENDER] NVARCHAR, " +
                "  [HEIGHT] INTEGER);";
        db.execSQL(CREATE_USER_TABLE);

        final String CREATE_INTERVAL_TABLE = "CREATE TABLE IF NOT EXISTS [IntervalTime] (" +
                "  [ID] INTEGER NOT NULL UNIQUE, " +
                "  [TIME] INTEGER, " +
                "  [START] INTEGER, " +
                "  [STATUS] NVARCHAR);";
        db.execSQL(CREATE_INTERVAL_TABLE);

        final String CREATE_WORLD_TIME_TABLE = "CREATE TABLE IF NOT EXISTS [PreferCity] (" +
                "  [CITY_ID] INTEGER NOT NULL UNIQUE, " +
                "  [EN] NVARCHAR, " +
                "  [ZH_CN] NVARCHAR, " +
                "  [ZH_TW] NVARCHAR, " +
                "  [ZH_HK] NVARCHAR, " +
                "  [ZONE] NVARCHAR," +
                "  [IS_DEFAULT] BOOLEAN," +
                "  [IS_SEL] BOOLEAN);";
        db.execSQL(CREATE_WORLD_TIME_TABLE);

        final String CREATE_STEP_TABLE = "CREATE TABLE IF NOT EXISTS [Step] (" +
                "  [START] INTEGER NOT NULL UNIQUE, " +
                "  [END] INTEGER, " +
                "  [STEP] INTEGER," +
                "  [DURATION] INTEGER, " +
                "  [DISTANCE] INTEGER, " +
                "  [CONSUME] INTEGER, " +
                "  [DAY] NVARCHAR, " +
                "  [WEEK] NVARCHAR, " +
                "  [MON] NVARCHAR, " +
                "  [YEAR] NVARCHAR);";
        db.execSQL(CREATE_STEP_TABLE);
        //</editor-fold>
        initDb(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    //    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        L.e("onUpgrade:"+oldVersion+","+newVersion);
//           //版本2在版本1基础上新增间隔时间START栏位
//        if(oldVersion == 1){
//            secondVersionUpgrade(db);
//            thirdVersionUpgrade(db);
//            fouthVersionUpgrade(db);
//            initSyncTable(db);
//        }else if(oldVersion == 2){
//            //版本3在版本2基础上新增振动设置
//            thirdVersionUpgrade(db);
//            fouthVersionUpgrade(db);
//            initSyncTable(db);
//        }else if(oldVersion == 3){
//            //版本4在版本3基础上新增ContactID
//            fouthVersionUpgrade(db);
//            initSyncTable(db);
//        }else if(oldVersion == 4){
//            initSyncTable(db);
//        }else if(oldVersion == 5){
//
//        }
//    }


//    //第二个版本的改动
//    private void secondVersionUpgrade(SQLiteDatabase db) {
//        L.e("secondVersionUpgrade");
//        final String ALTERInterval = "ALTER TABLE IntervalTime ADD COLUMN START INTEGER DEFAULT 0;";
//        db.execSQL(ALTERInterval);
//    }
//
//    //第三版本改动
//    private void thirdVersionUpgrade(SQLiteDatabase db) {
//        L.e("thirdVersionUpgrade");
//        final String CREATE_VIBRATION_TABLE = "CREATE TABLE IF NOT EXISTS [VIBRATION] (" +
//                "  [ID] INTEGER NOT NULL UNIQUE, " +
//                "  [STRONGER_STATUS] Boolean ," +
//                "  [NOTDISTURB_STATUS] Boolean ," +
//                "  [START_TIME] INTEGER ," +
//                "  [END_TIME] INTEGER );";
//        db.execSQL(CREATE_VIBRATION_TABLE);
//        initVibrationTable(db);
//    }
//
//    //第四版本改动
//    private void fouthVersionUpgrade(SQLiteDatabase db) {
//        L.e("fouthVersionUpgrade");
//        final String ALTERVip = "ALTER TABLE VIP ADD COLUMN CONTACTID INTEGER ;";
//        db.execSQL(ALTERVip);
//        final String DeleteVip = "DELETE FROM VIP;";//清空原来的数据 ，数据迁移再说吧
//        db.execSQL(DeleteVip);
//    }

    private void initDb(SQLiteDatabase db) {
        initUserTable(db);
        initSyncTable(db);
        initIntervalTable(db);
        initConfigVersion(db);
        initPreferCity(db);
        initVibrationTable(db);
    }


    /**
     * 不恢复数据 则清空下面的
     */
    public void clearLocalData() {
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            wDb.delete(DBColumn.StepColumns.TABLE_NAME, null, null);
            wDb.delete(DBColumn.PreferCityColumns.TABLE_NAME, null, null);
            wDb.delete(DBColumn.IntervalColumns.TABLE_NAME, null, null);
            wDb.delete(DBColumn.AlarmColumns.TABLE_NAME, null, null);
            wDb.delete(DBColumn.VIP.TABLE_NAME, null, null);
            wDb.delete(DBColumn.VibrationSetting.TABLE_NAME, null, null);
            initDb(wDb);
        } catch (Exception ex) {
            L.e("clearLocalData=::" + ex.getMessage());
        }
    }


    //<editor-fold desc="initData">

    /**
     * 初始化间隔时间表
     *
     * @param wDb
     */
    public void initUserTable(SQLiteDatabase wDb) {
        wDb.beginTransaction();
        try {
            wDb.execSQL("INSERT OR IGNORE INTO WatchUser (ID,WEIGHT,HEIGHT,BIRTH,GENDER) VALUES(?,?,?,?,? )",
                    new String[]{DBColumn.WatchUserColumns.ID_DEFAULT + "",
                            DBColumn.WatchUserColumns.WEIGHT_DEFAULT + "",
                            DBColumn.WatchUserColumns.HEIGHT_DEFAULT + "",
                            DBColumn.WatchUserColumns.BIRTH_DEFAULT + "",
                            DBColumn.WatchUserColumns.GENDER_DEFAULT + ""});
            wDb.setTransactionSuccessful();
        } catch (Exception ex) {
            L.e("initUserTable=::" + ex.getMessage());
            ex.printStackTrace();
        } finally {
            wDb.endTransaction();
        }
    }


    public void initConfigVersion(SQLiteDatabase wDb) {
        wDb.beginTransaction();
        try {
            wDb.execSQL("INSERT OR IGNORE INTO ConfigVersion (NAME,VERSION) VALUES(?,?)", new String[]{FESTIVAL_CHINA, VERSION_DEFAULT + ""});
            wDb.execSQL("INSERT OR IGNORE INTO ConfigVersion (NAME,VERSION) VALUES(?,?)", new String[]{WORLD_CITY, VERSION_DEFAULT + ""});
            wDb.setTransactionSuccessful();
        } catch (Exception ex) {
            L.e("initConfigVersion=::" + ex.getMessage());
            ex.printStackTrace();
        } finally {
            wDb.endTransaction();
        }

    }


    /**
     * 新增默认的城市（北京）
     *
     * @param
     */
    public boolean initPreferCity(final SQLiteDatabase wDb) {
        boolean result = false;
        wDb.beginTransaction();

        try {
            Configuration.ServerHandle(new Configuration.ServerHandler() {
                @Override
                public String defaultServer() {
                    wDb.execSQL("INSERT OR IGNORE INTO PreferCity (CITY_ID,ZONE,IS_DEFAULT,IS_SEL,ZH_CN,ZH_HK,ZH_TW,EN) VALUES(?,?,?,?,?,?,?,? )", new String[]{String.valueOf(BJID), "GMT+08:00", "1", "1", "北京", "北京", "北京", "Beijing"});
                    return null;
                }

                @Override
                public String cnServer() {
                    wDb.execSQL("INSERT OR IGNORE INTO PreferCity (CITY_ID,ZONE,IS_DEFAULT,IS_SEL,ZH_CN,ZH_HK,ZH_TW,EN) VALUES(?,?,?,?,?,?,?,? )", new String[]{String.valueOf(BJID), "GMT+08:00", "1", "1", "北京", "北京", "北京", "Beijing"});
                    return null;
                }

                @Override
                public String twServer() {
                    wDb.execSQL("INSERT OR IGNORE INTO PreferCity (CITY_ID,ZONE,IS_DEFAULT,IS_SEL,ZH_CN,ZH_HK,ZH_TW,EN) VALUES(?,?,?,?,?,?,?,? )", new String[]{String.valueOf(TBID), "Asia/Taipei", "1", "1", "台北", "臺北", "臺北", "Taipei"});
                    return null;
                }

                @Override
                public String hkServer() {
                    wDb.execSQL("INSERT OR IGNORE INTO PreferCity (CITY_ID,ZONE,IS_DEFAULT,IS_SEL,ZH_CN,ZH_HK,ZH_TW,EN) VALUES(?,?,?,?,?,?,?,? )", new String[]{String.valueOf(HKID), "Asia/Hong_Kong", "1", "1", "香港", "香港", "香港", "Hong Kong"});
                    return null;
                }
            });
            wDb.setTransactionSuccessful();
            result = true;
        } catch (Exception ex) {
            L.e("addDefaultCityError=::" + ex.getMessage());
        } finally {
            wDb.endTransaction();
        }
        return result;
    }


    /**
     * 同一个服务器造成默认城市不一致
     * 补上默认城市
     *
     * @param
     */
    public boolean initPreferCity2(final SQLiteDatabase wDb) {
        boolean result = false;
        wDb.beginTransaction();

        try {
            Configuration.ServerHandle(new Configuration.ServerHandler() {
                @Override
                public String defaultServer() {
                    wDb.execSQL("INSERT OR IGNORE INTO PreferCity (CITY_ID,ZONE,IS_DEFAULT,IS_SEL,ZH_CN,ZH_HK,ZH_TW,EN) VALUES(?,?,?,?,?,?,?,? )", new String[]{String.valueOf(BJID), "GMT+08:00", "1", "0", "北京", "北京", "北京", "Beijing"});
                    return null;
                }

                @Override
                public String cnServer() {
                    wDb.execSQL("INSERT OR IGNORE INTO PreferCity (CITY_ID,ZONE,IS_DEFAULT,IS_SEL,ZH_CN,ZH_HK,ZH_TW,EN) VALUES(?,?,?,?,?,?,?,? )", new String[]{String.valueOf(BJID), "GMT+08:00", "1", "0", "北京", "北京", "北京", "Beijing"});
                    return null;
                }

                @Override
                public String twServer() {
                    wDb.execSQL("INSERT OR IGNORE INTO PreferCity (CITY_ID,ZONE,IS_DEFAULT,IS_SEL,ZH_CN,ZH_HK,ZH_TW,EN) VALUES(?,?,?,?,?,?,?,? )", new String[]{String.valueOf(TBID), "Asia/Taipei", "1", "0", "台北", "臺北", "臺北", "Taipei"});
                    return null;
                }

                @Override
                public String hkServer() {
                    wDb.execSQL("INSERT OR IGNORE INTO PreferCity (CITY_ID,ZONE,IS_DEFAULT,IS_SEL,ZH_CN,ZH_HK,ZH_TW,EN) VALUES(?,?,?,?,?,?,?,? )", new String[]{String.valueOf(HKID), "Asia/Hong_Kong", "1", "0", "香港", "香港", "香港", "Hong Kong"});
                    return null;
                }
            });
            wDb.setTransactionSuccessful();
            result = true;
        } catch (Exception ex) {
            L.e("addDefaultCityError=::" + ex.getMessage());
        } finally {
            wDb.endTransaction();
        }
        return result;
    }

    /**
     * 初始化间隔时间表
     *
     * @param wDb
     */
    public void initIntervalTable(SQLiteDatabase wDb) {
        wDb.beginTransaction();
        try {
            wDb.execSQL("INSERT OR IGNORE INTO IntervalTime (ID,TIME,START,STATUS) VALUES(?,?,?,?)",
                    new String[]{DBColumn.IntervalColumns.ID_DEFAULT + "",
                            DBColumn.IntervalColumns.INTERVAL_DEFAULT + "",
                            DBColumn.IntervalColumns.START_DEFAULT + "",
                            DBColumn.IntervalColumns.STATUS_DEFAULT + ""});
            wDb.setTransactionSuccessful();
        } catch (Exception ex) {
            L.e("initIntervalTable=::" + ex.getMessage());
            ex.printStackTrace();
        } finally {
            wDb.endTransaction();
        }
    }

    /**
     * 初始化振动设置
     *
     * @param wDb
     */
    public void initVibrationTable(SQLiteDatabase wDb) {
        L.e("initVibrationTable");
        wDb.beginTransaction();
        try {
            wDb.execSQL("INSERT OR IGNORE INTO VIBRATION (ID,STRONGER_STATUS,NOTDISTURB_STATUS,START_TIME,END_TIME) VALUES(?,?,?,?,?)",
                    new String[]{DBColumn.VibrationSetting.ID_DEFAULT + "",
                            DBColumn.VibrationSetting.STRONGER_STATUS_DEFAULT + "",
                            DBColumn.VibrationSetting.NOTDISTURB_STATUS_DEFAULT + "",
                            DBColumn.VibrationSetting.START_TIME_DEFAULT + "",
                            DBColumn.VibrationSetting.END_TIME_DEFAULT + ""});
            wDb.setTransactionSuccessful();
        } catch (Exception ex) {
            L.e("initVibrationTable=::" + ex.getMessage());
            ex.printStackTrace();
        } finally {
            wDb.endTransaction();
        }
    }

    /**
     * 初始化时间戳表
     *
     * @param wDb
     */
    public void initSyncTable(SQLiteDatabase wDb) {
        String[] src = {USER_REGISTER_KEY, USER_KEY, INTERVAL_ALARM_KEY, NORMAL_ALARM_KEY, WORLD_CITY_KEY, VIP_KEY, STEPS_KEY, HTTP_SYNC_KEY, DEVICE_SYNC_KEY, VIBRATE_SETTING_KEY};
        wDb.beginTransaction();
        try {
            for (String item : src) {
                wDb.execSQL("INSERT OR IGNORE INTO Synchronous (KEY,TIMESTAMP) VALUES(?,?)", new String[]{item + "", "0"});
            }
            wDb.setTransactionSuccessful();
        } catch (Exception ex) {
            L.e("initSyncTable=::" + ex.getMessage());
            ex.printStackTrace();
        } finally {
            wDb.endTransaction();
        }
    }
    //</editor-fold>

    /**
     * @param key
     * @param value
     */
    public void saveCache(String key, Object value) {
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DBColumn.CACHE.KEY, key);
            if (value instanceof Integer) {
                values.put(DBColumn.CACHE.VALUE, String.valueOf(value));
            } else {
                values.put(DBColumn.CACHE.VALUE, value.toString());
            }
            wDb.replace(DBColumn.CACHE.TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            wDb.close();
        }
    }

    /**
     * 获取缓存数据
     *
     * @return 数据
     */
    public String getCache(String key) {
        String result = "";
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = rDb.rawQuery("SELECT * FROM " + DBColumn.CACHE.TABLE_NAME + " WHERE KEY = ?", new String[]{key});
            while (cursor.moveToNext()) {
                result = cursor.getString(cursor.getColumnIndex(DBColumn.CACHE.VALUE));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            rDb.close();
        }
        return result;
    }

    /**
     * 获取缓存数据
     *
     * @return 数据
     */
    public String getCacheWithDefault(String key, String defaultVal) {
        String result = null;
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = rDb.rawQuery("SELECT * FROM " + DBColumn.CACHE.TABLE_NAME + " WHERE KEY = ?", new String[]{key});
            while (cursor.moveToNext()) {
                result = cursor.getString(cursor.getColumnIndex(DBColumn.CACHE.VALUE));
            }
        } catch (Exception e) {
            L.e("getCacheWithDefault Exception:" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
            }
            rDb.close();
        }
        L.e("getCacheWithDefault:" + result);
        return TextUtils.isEmpty(result) ? defaultVal : result;
    }

    //<editor-fold desc="Interval">
    public boolean updateInterval(IntervalDao dao) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            //UPDATE
            ContentValues values = new ContentValues();
            values.put(DBColumn.IntervalColumns.TIME, dao.time);
            values.put(DBColumn.IntervalColumns.START, dao.start);
            values.put(DBColumn.IntervalColumns.STATUS, dao.status);
            wDb.update(DBColumn.IntervalColumns.TABLE_NAME, values, DBColumn.IntervalColumns.ID + "=?", new String[]{DBColumn.IntervalColumns.ID_DEFAULT + ""});
            result = true;
        } catch (Exception ex) {
            L.e("updateInterval :=" + ex.getMessage());
        }
        return result;
    }

    /**
     * 获取间隔时间
     *
     * @return
     */
    public IntervalDao getInterval() {
        IntervalDao result = new IntervalDao();
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[]{
                    DBColumn.IntervalColumns.TIME,
                    DBColumn.IntervalColumns.STATUS,
                    DBColumn.IntervalColumns.START,
            };
            cursor = rDb.query(DBColumn.IntervalColumns.TABLE_NAME, fetch_columns, DBColumn.IntervalColumns.ID + "=?", new String[]{DBColumn.IntervalColumns.ID_DEFAULT + ""}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    result.time = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.IntervalColumns.TIME));
                    result.status = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.IntervalColumns.STATUS));
                    result.start = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.IntervalColumns.START));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception ex) {
            L.e("getInterval=" + ex.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        L.e("getInterval=" + result.status + "" + "," + result.time + "," + result.start);
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="User">
    public boolean updateUser(WatchUserDao dao) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            //UPDATE
            ContentValues values = new ContentValues();
            values.put(DBColumn.WatchUserColumns.HEIGHT, dao.height);
            values.put(DBColumn.WatchUserColumns.WEIGHT, dao.weight);
            values.put(DBColumn.WatchUserColumns.GENDER, dao.gender);
            values.put(DBColumn.WatchUserColumns.BIRTH, dao.birth);
            wDb.update(DBColumn.WatchUserColumns.TABLE_NAME, values, DBColumn.WatchUserColumns.ID + "=?", new String[]{DBColumn.WatchUserColumns.ID_DEFAULT + ""});
            result = true;
        } catch (Exception ex) {
            L.e("updateUser :=" + ex.getMessage());
        }
        return result;
    }

    public WatchUserDao getWatchUserInfo() {
        WatchUserDao result = new WatchUserDao();
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[]{
                    DBColumn.WatchUserColumns.BIRTH,
                    DBColumn.WatchUserColumns.HEIGHT,
                    DBColumn.WatchUserColumns.WEIGHT,
                    DBColumn.WatchUserColumns.GENDER,
            };
            cursor = rDb.query(DBColumn.WatchUserColumns.TABLE_NAME, fetch_columns, DBColumn.WatchUserColumns.ID + "=?", new String[]{DBColumn.WatchUserColumns.ID_DEFAULT + ""}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    result.birth = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.WatchUserColumns.BIRTH));
                    result.gender = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.WatchUserColumns.GENDER));
                    result.height = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.WatchUserColumns.HEIGHT));
                    result.weight = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.WatchUserColumns.WEIGHT));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception ex) {
            L.e("getWatchUserInfo Ex=" + ex.getMessage());

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Vibration">
    public boolean updateVibration(VibrationDao dao) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            //UPDATE
            ContentValues values = new ContentValues();
            values.put(DBColumn.VibrationSetting.STRONGER_STATUS, dao.stronger ? "1" : "0");
            values.put(DBColumn.VibrationSetting.NOTDISTURB_STATUS, dao.notdisturb ? "1" : "0");
            values.put(DBColumn.VibrationSetting.START_TIME, dao.startTime);
            values.put(DBColumn.VibrationSetting.END_TIME, dao.endTime);
            wDb.update(DBColumn.VibrationSetting.TABLE_NAME, values, DBColumn.VibrationSetting.ID + "=?", new String[]{DBColumn.VibrationSetting.ID_DEFAULT + ""});
            result = true;
        } catch (Exception ex) {
            L.e("updateVibration :=" + ex.getMessage());
        }
        return result;
    }

    public VibrationDao getVibrationInfo() {
        VibrationDao result = new VibrationDao();
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[]{
                    DBColumn.VibrationSetting.STRONGER_STATUS,
                    DBColumn.VibrationSetting.NOTDISTURB_STATUS,
                    DBColumn.VibrationSetting.START_TIME,
                    DBColumn.VibrationSetting.END_TIME,
            };
            cursor = rDb.query(DBColumn.VibrationSetting.TABLE_NAME, fetch_columns, DBColumn.VibrationSetting.ID + "=?", new String[]{DBColumn.VibrationSetting.ID_DEFAULT + ""}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    result.stronger = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.VibrationSetting.STRONGER_STATUS)) == 1;
                    result.notdisturb = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.VibrationSetting.NOTDISTURB_STATUS)) == 1;
                    result.startTime = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.VibrationSetting.START_TIME));
                    result.endTime = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.VibrationSetting.END_TIME));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception ex) {
            L.e("getVibrationInfo Ex=" + ex.getMessage());

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="SyncColumns Operation">
    public int getKeyTimeStamp(String key) {
        int result = 0;
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[]{
                    DBColumn.SyncColumns.TIMESTAMP,
            };
            cursor = rDb.query(DBColumn.SyncColumns.TABLE_NAME, fetch_columns, DBColumn.SyncColumns.KEY + "=?", new String[]{key}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    result = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.SyncColumns.TIMESTAMP));
                } while (cursor.moveToNext());

            }
            cursor.close();
        } catch (Exception ex) {
            L.e("getKeyTimeStamp=" + ex.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }

    public boolean updateTimeStamp(String key, int timestamp) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            //UPDATE
            ContentValues values = new ContentValues();
            values.put(DBColumn.SyncColumns.TIMESTAMP, timestamp);
            wDb.update(DBColumn.SyncColumns.TABLE_NAME, values, DBColumn.SyncColumns.KEY + "=?", new String[]{key});
            result = true;
        } catch (Exception ex) {
            L.e("updateTimeStamp :=" + ex.getMessage());
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Step Operation ">
    public LinkedList<HttpStepHistory> getStepSyncData(int from, int to) {
        LinkedList<HttpStepHistory> result = new LinkedList<>();
        Cursor cursor = null;
        final SQLiteDatabase db = getReadableDatabase();
        try {
            //(remotetime,todayzero)区间不包含
            cursor = db.rawQuery("SELECT START, END, STEP FROM STEP WHERE START > ? AND START < ? ORDER BY START DESC ",
                    new String[]{String.valueOf(from), String.valueOf(to)});
            while (cursor.moveToNext()) {
                HttpStepHistory item = new HttpStepHistory();
                item.start = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.START));
                item.end = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.END));
                item.count = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.STEP));
                result.add(item);
            }
        } catch (Exception e) {
            L.e("getStepSyncData Error=::" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }

    /**
     * 计步数据入库
     *
     * @param source
     * @return
     */
    public synchronized boolean addSteps(List<StepDao> source) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        wDb.beginTransaction();
        try {
            for (StepDao item : source) {
                ContentValues values = new ContentValues();
                values.put(DBColumn.StepColumns.START, item.start);
                values.put(DBColumn.StepColumns.END, item.end);
                values.put(DBColumn.StepColumns.STEP, item.step);
                values.put(DBColumn.StepColumns.DURATION, item.duration);
                values.put(DBColumn.StepColumns.DISTANCE, item.distance);
                values.put(DBColumn.StepColumns.CONSUME, item.consume);
                values.put(DBColumn.StepColumns.DAY, item.day);
                values.put(DBColumn.StepColumns.WEEK, item.week);
                values.put(DBColumn.StepColumns.MON, item.mon);
                values.put(DBColumn.StepColumns.YEAR, item.year);
                wDb.replace(DBColumn.StepColumns.TABLE_NAME, null, values);
            }
            //添加完毕后更新时间戳
            updateTimeStamp(STEPS_KEY, getMaxStartTime());
            L.e("addSteps to db && timestamp:" + getMaxStartTime());
            wDb.setTransactionSuccessful();
            result = true;
        } catch (Exception ex) {
            L.e("addStepsError=::" + ex.getMessage());
            ex.printStackTrace();
        } finally {
            L.e("addSteps endTransaction");
            wDb.endTransaction();
        }
        // 计步数据同步完成后更新时间戳
        return result;
    }


    public  boolean addTodayStep(StepDao item) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DBColumn.StepColumns.START, item.start);
            values.put(DBColumn.StepColumns.END, item.end);
            values.put(DBColumn.StepColumns.STEP, item.step);
            values.put(DBColumn.StepColumns.DURATION, item.duration);
            values.put(DBColumn.StepColumns.DISTANCE, item.distance);
            values.put(DBColumn.StepColumns.CONSUME, item.consume);
            values.put(DBColumn.StepColumns.DAY, item.day);
            values.put(DBColumn.StepColumns.WEEK, item.week);
            values.put(DBColumn.StepColumns.MON, item.mon);
            values.put(DBColumn.StepColumns.YEAR, item.year);
            wDb.replace(DBColumn.StepColumns.TABLE_NAME, null, values);
            result = true;
        } catch (Exception ex) {
            L.e("addTodayStep=::" + ex.getMessage());
            ex.printStackTrace();
        }
        L.e("addTodayStep=::" + result);
        return result;
    }

    /**
     * 计步时间戳
     * 由于保存的计步时间戳小于今天的零点时间，所以是昨天的
     *
     * @return
     */
    public int getMaxStartTime() {
        int ret = 0;
        final SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT MAX(START) AS START FROM STEP WHERE START <" + TimeUtil.getTodayZero(getSettingZone()), null);
            while (cursor.moveToNext()) {
                ret = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.START));
            }
        } catch (Exception e) {
            L.e("getMaxStartTime Error=::" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return ret;
    }


    /**
     * 计步数据日数据
     *
     * @return
     */
    public synchronized List<StepDao> getStepDayData() {
        Cursor cursor = null;
        List<StepDao> result = new ArrayList<>();
        int register = getRegisterZeroTime();
        L.e("getStepDayData register: " + register);
        final SQLiteDatabase db = getReadableDatabase();
        try {
            cursor = db.rawQuery("SELECT YEAR,DAY,STEP,DURATION,DISTANCE,CONSUME FROM STEP WHERE START >= ?  ORDER BY START DESC", new String[]{String.valueOf(register)});
            L.e("getStepDayData=:: start");
            while (cursor.moveToNext()) {
                StepDao item = new StepDao();
                item.year = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.YEAR));
                item.day = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.DAY));
                item.step = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.STEP));
                item.duration = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.DURATION));
                item.distance = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.DISTANCE));
                item.consume = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.CONSUME));
                result.add(item);
            }
        } catch (Exception e) {
            L.e("getStepDayData Error=::" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }


    /**
     * 计步数据周数据
     *
     * @return
     */
    public List<StepDao> getStepWeekData() {
        Cursor cursor = null;
        List<StepDao> result = new ArrayList<>();
        int register = getRegisterZeroTime();
        final SQLiteDatabase db = getReadableDatabase();
        try {
            cursor = db.rawQuery("SELECT ROUND(AVG(STEP),0) AS STEP , YEAR , WEEK , ROUND(AVG(DURATION),0) AS DURATION,SUM(DISTANCE) AS DISTANCE,SUM(CONSUME) AS CONSUME FROM STEP WHERE STEP>0 AND  START >= ? GROUP BY WEEK,YEAR ORDER BY START DESC;", new String[]{String.valueOf(register)});
            while (cursor.moveToNext()) {
                StepDao item = new StepDao();
                item.year = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.YEAR));
                item.week = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.WEEK));
                item.step = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.STEP));
                item.duration = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.DURATION));
                item.distance = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.DISTANCE));
                item.consume = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.CONSUME));
                result.add(item);
            }
        } catch (Exception e) {
            L.e("getStepWeekData Error=::" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }

    /**
     * 包含注册那天
     * @return
     */
    private int getRegisterZeroTime() {
        int register = getKeyTimeStamp(USER_REGISTER_KEY);
        return register - 24 * 3600;
    }

    /**
     * 计步数据月数据
     *
     * @return
     */
    public List<StepDao> getStepMonData() {
        Cursor cursor = null;
        List<StepDao> result = new ArrayList<>();
        int register = getRegisterZeroTime();
        final SQLiteDatabase db = getReadableDatabase();
        L.e("getStepMonData：start");
        try {
            cursor = db.rawQuery("SELECT ROUND(AVG(STEP),0) AS STEP,  YEAR , MON, ROUND(AVG(DURATION),0) AS DURATION,SUM(DISTANCE) AS DISTANCE,SUM(CONSUME) AS CONSUME FROM STEP WHERE STEP>0  AND  START >= ? GROUP BY MON,YEAR ORDER BY START DESC", new String[]{String.valueOf(register)});
            while (cursor.moveToNext()) {
                StepDao item = new StepDao();
                item.year = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.YEAR));
                item.mon = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.MON));
                item.step = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.STEP));
                item.duration = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.DURATION));
                item.distance = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.DISTANCE));
                item.consume = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.StepColumns.CONSUME));
                result.add(item);
            }
        } catch (Exception e) {
            L.e("getStepMonData Error=::" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Alarm Operation">

    /**
     * @return
     */
    public boolean syncAlarm(List<AlarmDao> src) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        wDb.beginTransaction();
        try {
            wDb.delete(DBColumn.AlarmColumns.TABLE_NAME, null, null);
            for (AlarmDao item : src) {
                ContentValues values = new ContentValues();
                values.put(DBColumn.AlarmColumns.ID, item.id);
                values.put(DBColumn.AlarmColumns.STATUS, item.status ? 1 : 0);
                values.put(DBColumn.AlarmColumns.DESC, item.desc);
                values.put(DBColumn.AlarmColumns.TIME, item.seconds);
                values.put(DBColumn.AlarmColumns.EXTEND, item.extend);
                values.put(DBColumn.AlarmColumns.REPEATTYPE, item.repeatType);
                wDb.insert(DBColumn.AlarmColumns.TABLE_NAME, null, values);
            }
            wDb.setTransactionSuccessful();
            result = true;
        } catch (Exception ex) {
            L.e("syncAlarm Exception=::" + ex.getMessage());
        } finally {
            wDb.endTransaction();
        }
        return result;
    }

    /**
     * @return
     */
    public boolean addAlarmClock(AlarmDao item) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        wDb.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(DBColumn.AlarmColumns.ID, item.id);
            values.put(DBColumn.AlarmColumns.TIME, item.seconds);
            values.put(DBColumn.AlarmColumns.EXTEND, item.extend);
            values.put(DBColumn.AlarmColumns.REPEATTYPE, item.repeatType);
            values.put(DBColumn.AlarmColumns.DESC, item.desc);
            values.put(DBColumn.AlarmColumns.STATUS, item.status ? 1 : 0);
            wDb.insert(DBColumn.AlarmColumns.TABLE_NAME, null, values);
            wDb.setTransactionSuccessful();
            result = true;
        } catch (Exception ex) {
            L.e("AlarmClockOperateError=::" + ex.getMessage());
        } finally {
            wDb.endTransaction();
        }
        return result;
    }

    /**
     * 获取打开的联系人的数量
     *
     * @param
     * @return
     */
    public int getOpenVipCount() {
        final SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        int count = 0;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM VIP WHERE STATUS = 1", null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception ex) {
            L.e("getOpenVipCount :=" + ex.getMessage());
        }
        L.e("getOpenVipCount:" + count);
        return count;
    }

    /**
     * 获取打开的闹钟的数量
     *
     * @param
     * @return
     */
    public int getOpenAlarmCount() {
        final SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        int count = 0;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM AlarmClock WHERE STATUS = 1", null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception ex) {
            L.e("getOpenAlarmCount :=" + ex.getMessage());
        }
        L.e("getOpenAlarmCount:" + count);
        return count;
    }

    /**
     * @param
     * @return
     */
    public List<Integer> getAllAlarmID() {
        List<Integer> result = new ArrayList<>();
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[]{
                    DBColumn.AlarmColumns.ID,
            };
            cursor = rDb.query(DBColumn.AlarmColumns.TABLE_NAME, fetch_columns, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    result.add(cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.AlarmColumns.ID)));
                } while (cursor.moveToNext());

            }
            cursor.close();
        } catch (Exception ex) {
            L.e("getAllAlarmIDError=" + ex.getMessage());

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }

    public String getAlarmDescByID(int id) {
        String result = "";
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[]{
                    DBColumn.AlarmColumns.DESC,
            };
            cursor = rDb.query(DBColumn.AlarmColumns.TABLE_NAME, fetch_columns, DBColumn.AlarmColumns.ID + "=?", new String[]{id + ""}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.AlarmColumns.DESC));
                } while (cursor.moveToNext());

            }
            cursor.close();
        } catch (Exception ex) {
            L.e("getAllAlarmDesc=" + ex.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;

    }

    public boolean updateAlarmClock(int id, boolean isOn) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            //UPDATE
            ContentValues values = new ContentValues();
            values.put(DBColumn.AlarmColumns.STATUS, isOn ? 1 : 0);
            wDb.update(DBColumn.AlarmColumns.TABLE_NAME, values, DBColumn.AlarmColumns.ID + "=?", new String[]{id + ""});

            result = true;
        } catch (Exception ex) {
            L.e("updateAlarmClock :=" + ex.getMessage());
        }
        return result;
    }

    public boolean updateAlarmClock(AlarmDao item) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            //UPDATE
            ContentValues values = new ContentValues();
            values.put(DBColumn.AlarmColumns.TIME, item.seconds);
            values.put(DBColumn.AlarmColumns.EXTEND, item.extend);
            values.put(DBColumn.AlarmColumns.STATUS, item.status ? 1 : 0);
            values.put(DBColumn.AlarmColumns.REPEATTYPE, item.repeatType);
            values.put(DBColumn.AlarmColumns.DESC, item.desc);
            wDb.update(DBColumn.AlarmColumns.TABLE_NAME, values, DBColumn.AlarmColumns.ID + "=?", new String[]{item.id + ""});
            result = true;
        } catch (Exception ex) {
            L.e("updateAlarmClock :=" + ex.getMessage());
        }
        L.e("updateAlarmClock success");
        return result;
    }

    public boolean deleteAlarmByID(int id) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            wDb.delete(DBColumn.AlarmColumns.TABLE_NAME, DBColumn.AlarmColumns.ID + "=?", new String[]{id + ""});
            result = true;
        } catch (Exception ex) {
            L.e("deleteAlarmByID :=" + ex.getMessage());
        }
        return result;
    }

    /**
     * @param
     * @return
     */
    public List<AlarmDao> getAllAlarm() {
        List<AlarmDao> result = new ArrayList<>();
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {
            final String[] fetch_columns = new String[]{
                    DBColumn.AlarmColumns.ID,
                    DBColumn.AlarmColumns.REPEATTYPE,
                    DBColumn.AlarmColumns.TIME,
                    DBColumn.AlarmColumns.EXTEND,
                    DBColumn.AlarmColumns.STATUS,
                    DBColumn.AlarmColumns.DESC
            };
            cursor = rDb.query(DBColumn.AlarmColumns.TABLE_NAME, fetch_columns, null, null, null, null, DBColumn.AlarmColumns.TIME + " ASC");
            if (cursor.moveToFirst()) {
                do {
                    Boolean is_on = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.AlarmColumns.STATUS)) == 1;
                    result.add(new AlarmDao(
                                    cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.AlarmColumns.ID)),
                                    cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.AlarmColumns.TIME)),
                                    cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.AlarmColumns.EXTEND)),
                                    cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.AlarmColumns.REPEATTYPE)),
                                    is_on,
                                    cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.AlarmColumns.DESC))
                            )
                    );

                } while (cursor.moveToNext());

            }
            cursor.close();
        } catch (Exception ex) {
            L.e("getAllAlarm=" + ex.getMessage());

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="WorldCity Operation">

    /**
     * @return
     */
    public boolean syncWorldCity(List<HttpWorldCity> src) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        String selectServer = "";
        wDb.beginTransaction();
        try {
            wDb.delete(DBColumn.PreferCityColumns.TABLE_NAME, null, null);
            for (final HttpWorldCity item : src) {
                final ContentValues values = new ContentValues();
                values.put(DBColumn.PreferCityColumns.CITY_ID, item.id);
                values.put(DBColumn.PreferCityColumns.ZONE, item.zone);
                values.put(DBColumn.PreferCityColumns.ZH_CN, item.zh_cn);
                values.put(DBColumn.PreferCityColumns.ZH_HK, item.zh_hk);
                values.put(DBColumn.PreferCityColumns.ZH_TW, item.zh_tw);
                values.put(DBColumn.PreferCityColumns.EN, item.en);
                selectServer = Configuration.ServerHandle(new Configuration.ServerHandler() {
                    @Override
                    public String defaultServer() {
                        values.put(DBColumn.PreferCityColumns.IS_DEFAULT, item.id == BJID ? 1 : 0);
                        return String.valueOf(BJID);
                    }

                    @Override
                    public String cnServer() {
                        values.put(DBColumn.PreferCityColumns.IS_DEFAULT, item.id == BJID ? 1 : 0);
                        return String.valueOf(BJID);
                    }

                    @Override
                    public String twServer() {
                        values.put(DBColumn.PreferCityColumns.IS_DEFAULT, item.id == TBID ? 1 : 0);
                        return String.valueOf(TBID);
                    }

                    @Override
                    public String hkServer() {
                        values.put(DBColumn.PreferCityColumns.IS_DEFAULT, item.id == HKID ? 1 : 0);
                        return String.valueOf(HKID);
                    }
                });
                values.put(DBColumn.PreferCityColumns.IS_SEL, item.select ? 1 : 0);
                wDb.insert(DBColumn.PreferCityColumns.TABLE_NAME, null, values);
            }
            L.e("syncWorldCity selectServer: " + selectServer);
            boolean hasDefaultID = false;
            for (HttpWorldCity item : src) {
                if (item.id == Integer.parseInt(selectServer)) {
                    hasDefaultID = true;
                    break;
                }
            }
            L.e("syncWorldCity hasDefaultID: " + hasDefaultID);
            if (!hasDefaultID) {
                L.e("syncWorldCity has not defaultID");
                initPreferCity2(getWritableDatabase());
                SPManager.put(context, SP_ARG_HAS_DEFAULT_CITY, false);
            } else {
                SPManager.put(context, SP_ARG_HAS_DEFAULT_CITY, true);
                L.e("syncWorldCity has  defaultID");
            }
            wDb.setTransactionSuccessful();
            result = true;
        } catch (Exception ex) {
            L.e("syncWorldCity=::" + ex.getMessage());
        } finally {
            wDb.endTransaction();
        }
        return result;
    }


    /**
     * @param
     * @return
     */
    public synchronized List<PreferCitiesDao> getAllPreferCities() {
        List<PreferCitiesDao> result = new ArrayList<>();
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[]{
                    DBColumn.PreferCityColumns.CITY_ID,
                    DBColumn.PreferCityColumns.ZH_CN,
                    DBColumn.PreferCityColumns.EN,
                    DBColumn.PreferCityColumns.ZH_HK,
                    DBColumn.PreferCityColumns.ZH_TW,
                    DBColumn.PreferCityColumns.ZONE,
                    DBColumn.PreferCityColumns.IS_SEL,
                    DBColumn.PreferCityColumns.IS_DEFAULT,
            };
            cursor = rDb.query(DBColumn.PreferCityColumns.TABLE_NAME, fetch_columns, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    Boolean b0 = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.PreferCityColumns.IS_SEL)) == 1;
                    Boolean b1 = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.PreferCityColumns.IS_DEFAULT)) == 1;
                    result.add(new PreferCitiesDao(
                            (long) cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.PreferCityColumns.CITY_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.PreferCityColumns.ZH_CN)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.PreferCityColumns.EN)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.PreferCityColumns.ZH_TW)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.PreferCityColumns.ZH_HK)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.PreferCityColumns.ZONE)),
                            b0,
                            b1)
                    );
                } while (cursor.moveToNext());

            }
            cursor.close();
        } catch (Exception ex) {
            L.e("getPreferCitiesDaoError=" + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }


    /**
     * 获取时区
     *
     * @param
     * @return
     */
    public String getSettingZone() {
        String result = "";
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {
            final String[] fetch_columns = new String[]{
                    DBColumn.PreferCityColumns.ZONE,
            };
            cursor = rDb.query(DBColumn.PreferCityColumns.TABLE_NAME, fetch_columns, DBColumn.PreferCityColumns.IS_SEL + "=?", new String[]{"1"}, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.PreferCityColumns.ZONE));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception ex) {
            L.e("getZoneException =" + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        L.e("getSettingZone:" + result);
        return result;
    }


    /**
     * 新增偏爱的城市
     *
     * @param
     */
    public boolean addPreferCity(List<PreferCitiesDao> list) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        wDb.beginTransaction();
        try {
            for (PreferCitiesDao item : list) {
                ContentValues values = new ContentValues();
                values.put(DBColumn.PreferCityColumns.CITY_ID, item.id);
                values.put(DBColumn.PreferCityColumns.ZH_CN, item.zh_cn);
                values.put(DBColumn.PreferCityColumns.ZH_TW, item.zh_tw);
                values.put(DBColumn.PreferCityColumns.ZH_HK, item.zh_hk);
                values.put(DBColumn.PreferCityColumns.EN, item.en);
                values.put(DBColumn.PreferCityColumns.IS_SEL, 0);
                values.put(DBColumn.PreferCityColumns.IS_DEFAULT, 0);
                values.put(DBColumn.PreferCityColumns.ZONE, item.zone);
                wDb.insert(DBColumn.PreferCityColumns.TABLE_NAME, null, values);
            }
            wDb.setTransactionSuccessful();
            result = true;
        } catch (Exception ex) {
            L.e("addListPreferCityError=::" + ex.getMessage());
        } finally {
            wDb.endTransaction();
        }
        return result;
    }


    /**
     * 删除
     *
     * @param id
     * @return
     */
    public synchronized boolean deletePreferCityByID(int id) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            wDb.delete(DBColumn.PreferCityColumns.TABLE_NAME, DBColumn.PreferCityColumns.CITY_ID + "=?", new String[]{id + ""});
            result = true;
        } catch (Exception ex) {
            L.e("deletePreferCityByIDError :=" + ex.getMessage());
        }
        return result;
    }

    /**
     * 更改城市是否设为默认
     *
     * @param id
     * @return
     */
    public synchronized boolean updateSelPreferCity(int id) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            //UPDATE
            ContentValues value0 = new ContentValues();
            value0.put(IS_SEL, 0);
            wDb.update(DBColumn.PreferCityColumns.TABLE_NAME, value0, null, null);//全部设置为0未选中
            ContentValues value1 = new ContentValues();
            value1.put(IS_SEL, "1");
            wDb.update(DBColumn.PreferCityColumns.TABLE_NAME, value1, DBColumn.PreferCityColumns.CITY_ID + "=?", new String[]{id + ""});
            result = true;
        } catch (Exception ex) {
            L.e("updateSelPreferCity :=" + ex.getMessage());
        }
        return result;
    }

    //</editor-fold>

    //<editor-fold desc="Vip">

    /**
     * @return
     */
    public boolean syncVip(List<VipEntity> src) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        wDb.beginTransaction();
        try {
            wDb.delete(DBColumn.VIP.TABLE_NAME, null, null);
            for (VipEntity item : src) {
                ContentValues values = new ContentValues();
                values.put(DBColumn.VIP.ID, item.id);
                values.put(CONTACTID, item.contactId);
                values.put(DBColumn.VIP.NAME, item.name);
                values.put(DBColumn.VIP.NUMBER, item.number);
                values.put(DBColumn.VIP.STATUS, item.status);
                wDb.insert(DBColumn.VIP.TABLE_NAME, null, values);
            }
            wDb.setTransactionSuccessful();
            result = true;
        } catch (Exception ex) {
            L.e("syncVip=::" + ex.getMessage());
        } finally {
            wDb.endTransaction();
        }
        return result;
    }


    /**
     * 单个添加Vip联系人,成功才能写入手表
     *
     * @return
     */
    public boolean addVipContact(VipEntity vip) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            wDb.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(DBColumn.VIP.ID, vip.id);
            values.put(CONTACTID, vip.contactId);
            values.put(DBColumn.VIP.NAME, vip.name);
            values.put(DBColumn.VIP.STATUS, vip.status ? "1" : "0");
            values.put(DBColumn.VIP.NUMBER, vip.number);
            result = (wDb.insert(DBColumn.VIP.TABLE_NAME, null, values) != -1);
            wDb.setTransactionSuccessful();
        } catch (Exception ex) {
            L.e("addVipContact=::" + ex.getMessage());
        } finally {
            wDb.endTransaction();
        }
        L.e("addVipContact result:" + vip.id + ":" + result);
        return result;
    }

    public List<VipEntity> getVipContact() {
        List<VipEntity> result = new ArrayList<>();
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[]{
                    CONTACTID,
                    DBColumn.VIP.ID,
                    DBColumn.VIP.NAME,
                    DBColumn.VIP.STATUS,
                    DBColumn.VIP.NUMBER
            };
            cursor = rDb.query(DBColumn.VIP.TABLE_NAME, fetch_columns, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    result.add(new VipEntity(
                                    cursor.getInt(cursor.getColumnIndexOrThrow(CONTACTID)),
                                    cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.VIP.ID)),
                                    cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.VIP.NUMBER)),
                                    cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.VIP.NAME)),
                                    cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.VIP.STATUS)) == 1
                            )
                    );
                } while (cursor.moveToNext());

            }
            cursor.close();
        } catch (Exception ex) {
            L.e("getVipContact=" + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }

    /**
     * 开关vip
     *
     * @return
     */
    public boolean updateVip(VipEntity vip) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            //UPDATE
            ContentValues value0 = new ContentValues();
            value0.put(DBColumn.VIP.STATUS, vip.status ? 1 : 0);
            wDb.update(DBColumn.VIP.TABLE_NAME, value0, DBColumn.VIP.NAME + "=? AND " + DBColumn.VIP.NUMBER + "=?", new String[]{vip.name, vip.number});//
            result = true;
        } catch (Exception ex) {
            L.e("updateVip :=" + ex.getMessage());
        }
        return result;
    }

    public boolean deleteVipContact(VipEntity vip) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            wDb.delete(DBColumn.VIP.TABLE_NAME, DBColumn.VIP.NAME + "=? AND " + DBColumn.VIP.NUMBER + "=?", new String[]{vip.name, vip.number});
            result = true;
        } catch (Exception ex) {
            L.e("deleteVipContact :=" + ex.getMessage());
        }
        return result;
    }

    /**
     * vip是否需要打开
     *
     * @return
     */
    public boolean isVipNeedOpen() {
        final SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        int count = 0;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM VIP WHERE STATUS > 0", null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception ex) {
            L.e("deleteVipContact :=" + ex.getMessage());
        }
        L.e(count > 0 ? "isVipNeedOpen = true" : "isVipNeedOpen = false");
        return count > 0;
    }
    //</editor-fold>


    public int getConfigV(String arg) {
        int result = 0;
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[]{
                    DBColumn.ConfigVersion.VERSION,
            };
            cursor = rDb.query(DBColumn.ConfigVersion.TABLE_NAME, fetch_columns, DBColumn.ConfigVersion.NAME + "=?", new String[]{arg}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    result = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.ConfigVersion.VERSION));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception ex) {
            L.e("getConfigV=" + ex.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        L.e("getConfigV--" + arg + ":" + result);
        return result;
    }

    public boolean updateConfigV(String n, int v) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            //UPDATE
            ContentValues value0 = new ContentValues();
            value0.put(DBColumn.ConfigVersion.VERSION, v);
            wDb.update(DBColumn.ConfigVersion.TABLE_NAME, value0, DBColumn.ConfigVersion.NAME + "=?", new String[]{n});//
            result = true;
        } catch (Exception ex) {
            L.e("updateConfigV :=" + ex.getMessage());
        }
        L.e("updateConfigV success");
        return result;
    }

    /**
     * 低电关闭所有振动
     */
    public boolean closeAllVibration() {
        boolean ret = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        wDb.beginTransaction();
        try {
            wDb.execSQL("UPDATE IntervalTime SET STATUS = ? ;", new String[]{OFF});
            wDb.execSQL("UPDATE AlarmClock SET STATUS = 0 ;");
            wDb.execSQL("UPDATE VIP  SET STATUS = 0 ;");
            SPManager.put(context,SP_INCOMING_SWITCH,false);
            wDb.setTransactionSuccessful();
            ret = true;
        } catch (Exception ex) {
            L.e("closeAllVibration Exception=::" + ex.getMessage());
            ex.printStackTrace();
            ret = false;
        } finally {
            wDb.endTransaction();
        }
        return ret;
    }


    /**
     * 关闭所有振动
     *
     * @return
     */
    public boolean isNeedCloseVibration() {
        final SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        int sum = 0;
        try {
//            cursor = db.rawQuery("SELECT * from (SELECT count(*) as c1  FROM  AlarmClock WHERE STATUS = 1) " +
//                    "LEFT JOIN " +
//                    "(SELECT count(*) as c2  FROM  VIP WHERE STATUS = 1) " +
//                    "LEFT JOIN " +
//                    "(SELECT count(*) as c3  FROM  IntervalTime WHERE STATUS = ?) ", new String[]{ON});
//            if (cursor.moveToFirst()) {
//                sum = cursor.getInt(0) + cursor.getInt(1) + cursor.getInt(2);
//            }
            cursor = db.rawQuery("SELECT * from (SELECT count(*) as c1  FROM  AlarmClock WHERE STATUS = 1) " +
                    "LEFT JOIN " +
                    "(SELECT count(*) as c3  FROM  IntervalTime WHERE STATUS = ?) ", new String[]{ON});
            if (cursor.moveToFirst()) {
                sum = cursor.getInt(0) + cursor.getInt(1);
            }
            cursor.close();
        } catch (Exception ex) {
            L.e("isNeedCloseVibration :=" + ex.getMessage());
        }
        L.e("isNeedCloseVibration :=" + (sum > 0));
        return sum > 0 || (Boolean) SPManager.get(context,SP_INCOMING_SWITCH,false);
    }

    public void fixBugWorldCityNull(HttpCityRes bean) {
        List<PreferCitiesDao> daos = getAllPreferCities();
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            if (null != bean && null != daos) {
                for (PreferCitiesDao dao : daos) {
                    if (TextUtils.isEmpty(dao.en)) {
                        for (HttpCityRes.CityListBean item : bean.getCity_list()) {
                            if (dao.id == Long.parseLong(item.getId())) {
                                //UPDATE
                                ContentValues v = new ContentValues();
                                v.put(DBColumn.PreferCityColumns.EN, item.getEn());
                                wDb.update(DBColumn.PreferCityColumns.TABLE_NAME, v, DBColumn.PreferCityColumns.CITY_ID + "=?", new String[]{item.getId()});//
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            L.e("updateConfigV :=" + ex.getMessage());
        }
    }

}
