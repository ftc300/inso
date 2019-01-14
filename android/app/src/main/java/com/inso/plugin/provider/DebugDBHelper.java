package com.inso.plugin.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.inso.plugin.dao.DebugStepDao;
import com.inso.plugin.dao.DebugStepPeriodDao;
import com.inso.plugin.manager.SPManager;
import com.inso.plugin.tools.L;

import java.util.ArrayList;
import java.util.List;
import static com.inso.plugin.tools.Constants.SystemConstant.SP_ARG_USERID;

/**
 * Created by cd 2017/03/08
 */
public class DebugDBHelper extends SQLiteOpenHelper {
    public static final String DATABASENAME_DEBUG = "debuginshow.db";
    private static int DEBUG_DB_VERSION = 1;

    public DebugDBHelper(Context context) {
        super( new DatabaseContext(context),
                (String) SPManager.get(context, SP_ARG_USERID, "") + DATABASENAME_DEBUG,
                null,
                DEBUG_DB_VERSION);
//       super(context, MessUtil.getDataBaseName((String) SPManager.get(context, SP_ARG_USERID, "")),null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_DEBUG_STEP_TABLE = "CREATE TABLE IF NOT EXISTS [DebugStep] (" +
                "  [START_TIME] INTEGER, " +
                "  [END_TIME] INTEGER, " +
                "  [START_STEP] INTEGER, " +
                "  [END_STEP] INTEGER, " +
                "  [TYPE] INTEGER, " +
                "  [GOAL] INTEGER, " +
                "  [MAC] NVARCHAR); ";
        db.execSQL(CREATE_DEBUG_STEP_TABLE);

        final String CREATE_DEBUG_STEP_PERIOD_TABLE = "CREATE TABLE IF NOT EXISTS [DebugStepPeriod] (" +
                "  [PERIOD] INTEGER, " +
                "  [START_TIME] INTEGER, " +
                "  [START_STEP] INTEGER, " +
                "  [TYPE] INTEGER, " +
                "  [MAC] NVARCHAR NOT NULL UNIQUE); ";
        db.execSQL(CREATE_DEBUG_STEP_PERIOD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    /**
     * 开始时记录
     *
     * @param item
     * @return
     */
    public boolean replaceDebugStepPeriod(DebugStepPeriodDao item) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DBColumn.DebugStepPeriodColumns.MAC, item.mac);
            values.put(DBColumn.DebugStepPeriodColumns.START_TIME, item.starttime);
            values.put(DBColumn.DebugStepPeriodColumns.START_STEP, item.startstep);
            values.put(DBColumn.DebugStepPeriodColumns.PERIOD, item.period);
            values.put(DBColumn.DebugStepPeriodColumns.TYPE, item.type);
            wDb.replace(DBColumn.DebugStepPeriodColumns.TABLE_NAME, null, values);
            result = true;
        } catch (Exception ex) {
            L.e("addDebugStep=::" + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }


    public boolean deleteDebugStepPeriod(String MAC) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            wDb.delete(DBColumn.DebugStepPeriodColumns.TABLE_NAME, DBColumn.DebugStepPeriodColumns.MAC + "=?", new String[]{MAC + ""});
            result = true;
        } catch (Exception ex) {
            L.e("deleteDebugStepPeriod :=" + ex.getMessage());
        }
        return result;
    }

    public DebugStepPeriodDao getDebugStepPeriodDao(String MAC) {
        DebugStepPeriodDao dao = null;
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {
            final String[] fetch_columns = new String[]{
                    DBColumn.DebugStepPeriodColumns.MAC,
                    DBColumn.DebugStepPeriodColumns.START_TIME,
                    DBColumn.DebugStepPeriodColumns.START_STEP,
                    DBColumn.DebugStepPeriodColumns.PERIOD,
                    DBColumn.DebugStepPeriodColumns.TYPE,
            };
            cursor = rDb.query(DBColumn.DebugStepPeriodColumns.TABLE_NAME, fetch_columns, DBColumn.DebugStepPeriodColumns.MAC + "=?", new String[]{MAC}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    dao = new DebugStepPeriodDao();
                    dao.mac = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.DebugStepPeriodColumns.MAC));
                    dao.starttime = cursor.getLong(cursor.getColumnIndexOrThrow(DBColumn.DebugStepPeriodColumns.START_TIME));
                    dao.startstep = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.DebugStepPeriodColumns.START_STEP));
                    dao.period = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.DebugStepPeriodColumns.PERIOD));
                    dao.type = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.DebugStepPeriodColumns.TYPE));
                    L.e(dao.toString());
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception ex) {
            L.e("DebugStepPeriodDao Exception :=" + ex.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return dao;
    }


    /**
     * 结束时记录
     *
     * @param item
     * @return
     */
    public boolean addDebugStep(DebugStepDao item) {
        boolean result = false;
        final SQLiteDatabase wDb = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(DBColumn.DebugStepColumns.MAC, item.mac);
            values.put(DBColumn.DebugStepColumns.STARTTIME, item.starttime);
            values.put(DBColumn.DebugStepColumns.ENDTIME, item.endtime);
            values.put(DBColumn.DebugStepColumns.STARTSTEP, item.startstep);
            values.put(DBColumn.DebugStepColumns.ENDSTEP, item.endstep);
            values.put(DBColumn.DebugStepColumns.TYPE, item.type);
            values.put(DBColumn.DebugStepColumns.GOAL, item.goal);
            wDb.insert(DBColumn.DebugStepColumns.TABLE_NAME, null, values);
            result = true;
        } catch (Exception ex) {
            L.e("addDebugStep=::" + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }

    public List<DebugStepDao> getDebugStepDao(String MAC) {
        List<DebugStepDao> result = new ArrayList<>();
        final SQLiteDatabase rDb = getReadableDatabase();
        Cursor cursor = null;
        try {

            final String[] fetch_columns = new String[]{
                    DBColumn.DebugStepColumns.MAC,
                    DBColumn.DebugStepColumns.STARTTIME,
                    DBColumn.DebugStepColumns.ENDTIME,
                    DBColumn.DebugStepColumns.STARTSTEP,
                    DBColumn.DebugStepColumns.ENDSTEP,
                    DBColumn.DebugStepColumns.GOAL,
                    DBColumn.DebugStepColumns.TYPE,
            };
            if (TextUtils.isEmpty(MAC)) {
                cursor = rDb.query(DBColumn.DebugStepColumns.TABLE_NAME, fetch_columns, null, null, null, null, DBColumn.DebugStepColumns.STARTTIME + " DESC");
            } else {
                cursor = rDb.query(DBColumn.DebugStepColumns.TABLE_NAME, fetch_columns, DBColumn.DebugStepColumns.MAC + "=?", new String[]{MAC}, null, null, DBColumn.DebugStepColumns.STARTTIME + " DESC");
            }
            if (cursor.moveToFirst()) {
                do {
                    DebugStepDao dao = new DebugStepDao();
                    dao.mac = cursor.getString(cursor.getColumnIndexOrThrow(DBColumn.DebugStepColumns.MAC));
                    dao.starttime = cursor.getLong(cursor.getColumnIndexOrThrow(DBColumn.DebugStepColumns.STARTTIME));
                    dao.endtime = cursor.getLong(cursor.getColumnIndexOrThrow(DBColumn.DebugStepColumns.ENDTIME));
                    dao.startstep = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.DebugStepColumns.STARTSTEP));
                    dao.endstep = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.DebugStepColumns.ENDSTEP));
                    dao.type = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.DebugStepColumns.TYPE));
                    dao.goal = cursor.getInt(cursor.getColumnIndexOrThrow(DBColumn.DebugStepColumns.GOAL));
                    L.e(dao.toString());
                    result.add(dao);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception ex) {
            L.e("getDebugStepDao Exception :=" + ex.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }
}
