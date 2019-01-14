package com.inshow.watch.android.act.alarm;

import android.content.Context;

import com.inshow.watch.android.R;
import com.inshow.watch.android.provider.DBHelper;
import com.inshow.watch.android.tools.Configuration;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static com.inshow.watch.android.MessageReceiver.deltaTimeFromUTC;
import static com.inshow.watch.android.tools.TimeUtil.getDefaultDataUnit;
import static com.inshow.watch.android.tools.TimeUtil.getEnDataUnit;

/**
 * Created by chendong on 2017/3/6.
 */

public class AlarmHelper {
    private static final int Repeat_Once = 0x0001;
    private static final int Repeat_Everyday = 0x0002;
    private static final int Repeat_Working = 0x0004;
    private static final int Repeat_Holiday = 0x0008;
    private static final int Repeat_Sunday = 0x0010;
    private static final int Repeat_Monday = 0x0020;
    private static final int Repeat_Tuesday = 0x0040;
    private static final int Repeat_Wednesday = 0x0080;
    private static final int Repeat_Thursday = 0x0100;
    private static final int Repeat_Friday = 0x0200;
    private static final int Repeat_Saturday = 0x0400;

    /**
     * 获取选取的星期之间的差值天数
     *
     * @param selectList
     * @return
     */
    public static int getDeltaDays(CirLinkList selectList, int todayWeekNum) {
        CirLinkList totalList = new CirLinkList();
        for (int i = 1; i < 8; i++) {
            totalList.insertList(i);
        }
        totalList.print();
        if (selectList != null) {
            if (selectList.isContain(todayWeekNum)) {//select contain
                int nextWeekNum = (int) selectList.getNextElement(selectList.getElementIndex(todayWeekNum)).value;
                int deltaNum = nextWeekNum - todayWeekNum;
                int ret = deltaNum > 0 ? deltaNum : 7 + deltaNum;
                return selectList.size() == 1 ? 7 : ret;
            } else {// not contain
                int index = totalList.getElementIndex(todayWeekNum);
                int value;
                int days = 0;
                while (true) {
                    value = (int) totalList.getNextElement(index).value;
                    index = totalList.getElementIndex(value);
                    days++;
                    if (selectList.isContain(value)) {
                        return days;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * 下一个节假日距离今天要几天
     *
     * @param db
     * @param zone
     * @return
     */
    public static int getDeltaHolidayDays(DBHelper db, String zone) {
        int ret = 0;
        SimpleDateFormat sdf = new SimpleDateFormat(TimeUtil.DATE_FORMAT_YYYYMMDD, Locale.getDefault());
        Calendar cNext = Calendar.getInstance();
        cNext.setTimeInMillis(System.currentTimeMillis() + deltaTimeFromUTC);
        cNext.setTimeZone(TimeZone.getTimeZone(zone));
        while (true) {
            cNext.add(Calendar.DATE, 1);
            ret++;
            if (TimeUtil.isHoliday(db, sdf.format(cNext.getTime()))) break;
        }
        L.e("getDeltaHolidayDays:" + ret);
        return ret;
    }

    /**
     * 下一个工作日距离今天要几天
     *
     * @param db
     * @param zone
     * @return
     */
    public static int getDeltaWorkingDays(DBHelper db, String zone) {
        int ret = 0;
        SimpleDateFormat sdf = new SimpleDateFormat(TimeUtil.DATE_FORMAT_YYYYMMDD, Locale.getDefault());
        Calendar cNext = Calendar.getInstance();
        cNext.setTimeInMillis(System.currentTimeMillis() + deltaTimeFromUTC);
        cNext.setTimeZone(TimeZone.getTimeZone(zone));
        while (true) {
            cNext.add(Calendar.DATE, 1);
            ret++;
            if (!TimeUtil.isHoliday(db, sdf.format(cNext.getTime()))) break;
        }
        return ret;
    }

    /**
     * 闹钟间隔的秒数
     *
     * @param repeatType
     * @param zone
     * @param alarmTime  闹铃时间
     * @return
     */
    public static int getNextAlarmRestSeconds(DBHelper db, String repeatType, String zone, int alarmTime) {
        String todayStr = TimeUtil.getNowTimeStringYMD(zone);
        int todayWeekNum = TimeUtil.getDayForWeek(todayStr);
        int todayZeroTime = TimeUtil.getTodayZero(zone);
        int currentTime = TimeUtil.getNowTimeSeconds(zone);
        int deltaToday = currentTime - todayZeroTime;
        int delta = alarmTime - deltaToday;
        L.e("todayStr :" + todayStr + "," +
                "todayWeekNum:" + todayWeekNum + "," +
                "todayZeroTime:" + todayZeroTime + "," +
                "currentTime:" + currentTime + "," +
                "deltaToday:" + deltaToday + "," +
                "alarmTime:" + alarmTime + "," +
                "delta:" + delta
        );
        if (repeatType.length() == 1) {
            switch (Integer.parseInt(repeatType)) {
                case 0:
                    return delta > 0 ? delta : 24 * 3600 + delta;
                case 1:
                    return delta > 0 ? delta : 24 * 3600 + delta;
                case 2:
                    if (!TimeUtil.isHoliday(db, todayStr) && delta > 0) return delta;
                    else {
                        int days = getDeltaWorkingDays(db, zone);
                        return 24 * 3600 * days + delta;
                    }
                case 3:
                    if (TimeUtil.isHoliday(db, todayStr) && delta > 0) return delta;
                    else {
                        int days = getDeltaHolidayDays(db, zone);
                        return 24 * 3600 * days + delta;
                    }
                case 4:
                    CirLinkList list = new CirLinkList();
                    //from mon to fri
                    for (int i = 1; i < 6; i++) {
                        list.insertList(i);
                    }
                    if (delta > 0 && list.isContain(todayWeekNum)) {
                        return delta;
                    } else {
                        int days = getDeltaDays(list, todayWeekNum);
                        return 24 * 3600 * days + delta;
                    }
            }
        } else {
            String[] selectType = repeatType.split(",");
            CirLinkList list = new CirLinkList();
            for (int i = 1; i < selectType.length; i++) {
                switch (Integer.parseInt(selectType[i])) {
                    case 0:
                        list.insertList(1);
                        break;
                    case 1:
                        list.insertList(2);
                        break;
                    case 2:
                        list.insertList(3);
                        break;
                    case 3:
                        list.insertList(4);
                        break;
                    case 4:
                        list.insertList(5);
                        break;
                    case 5:
                        list.insertList(6);
                        break;
                    case 6:
                        list.insertList(7);
                        break;
                }
            }
            list.print();
            if (delta > 0 && list.isContain(todayWeekNum)) {
                L.e("delta2:" + delta);
                return delta;
            } else {
                int days = getDeltaDays(list, todayWeekNum);
                L.e("days:" + days + "," + "delta3:" + (24 * 3600 * days + delta));
                return 24 * 3600 * days + delta;
            }
        }
        return 0;
    }


    public static String getDisplayRemainTime(final Context context, int time) {
        L.e("getDisplayRemainTime:" + time);
        final int day, hour, minute;
        if (time < 60) return context.getString(R.string.less_than_one_minute);
        day = time / 24 / 3600;
        hour = (time % (24 * 3600)) / 3600;
        minute = ((time % (24 * 3600)) % 3600) / 60;
        return Configuration.getInstance().LocaleHandler2(context, new Configuration.LocaleHandler2() {
            @Override
            public String enHandle() {
                return "Vibrate after " + getEnDataUnit(day, context.getString(R.string.time_day)) + getEnDataUnit(hour, context.getString(R.string.time_hour)) + getEnDataUnit(minute, context.getString(R.string.time_min));
            }

            @Override
            public String defaultHandle() {
                return getDefaultDataUnit(day, context.getString(R.string.time_day)) + getDefaultDataUnit(hour, context.getString(R.string.one_hour)) + getDefaultDataUnit(minute, context.getString(R.string.alarm_time_min)) + context.getString(R.string.how_many_hours_ring);
            }
        });
    }

    /**
     * 显示的时间
     *
     * @param seconds
     * @return
     */
//    public static String getDisplayClock(int seconds) {
//        StringBuffer ret = new StringBuffer();
//        int hour = seconds / 3600;
//        int min = seconds % 3600 / 60;
//        ret.append(hour < 10 ? "0" + hour : hour).append(":").append(min < 10 ? "0" + min : min);
//        return ret.toString();
//    }
    public static String getDisplayClock(int seconds) {
        int h = seconds / 3600 % 12;
        return String.format(Locale.getDefault(), "%02d:%02d", h == 0 ? 12 : h, seconds % 3600 / 60);
    }

    public static String getDayPeriod(Context c, int seconds) {
        return seconds / 3600 < 12 ? c.getString(R.string.morning) : c.getString(R.string.afternoon);
    }


    public static String getDisplayRepeatType(Context context, String repeatType) {
        if (repeatType.length() == 1) {
            return context.getResources().getStringArray(R.array.repeattype)[Integer.parseInt(repeatType)];
        } else {
            String[] selectType = repeatType.split(",");
            StringBuffer buffer = new StringBuffer();
            for (int i = 1; i < selectType.length; i++) {
                buffer.append(context.getResources().getStringArray(R.array.simple_week)[Integer.parseInt(selectType[i])]).append(" ");
            }
            return buffer.toString();
        }
    }

    public static byte[] getRepeatWriteBytes(String repeatType) {
        int resultInt = 0x0000;
        if (repeatType.length() == 1) {
            switch (Integer.parseInt(repeatType)) {
                case 0:
                    resultInt = Repeat_Once;
                    break;
                case 1:
                    resultInt = Repeat_Everyday;
                    break;
                case 2:
                    resultInt = Repeat_Working;
                    break;
                case 3:
                    resultInt = Repeat_Holiday;
                    break;
                case 4:
                    resultInt = Repeat_Monday | Repeat_Tuesday | Repeat_Wednesday | Repeat_Thursday | Repeat_Friday;
                    break;
            }
        } else {
            String[] selectType = repeatType.split(",");
            for (int i = 1; i < selectType.length; i++) {
                switch (Integer.parseInt(selectType[i])) {
                    case 0:
                        resultInt |= Repeat_Monday;
                        break;
                    case 1:
                        resultInt |= Repeat_Tuesday;
                        break;
                    case 2:
                        resultInt |= Repeat_Wednesday;
                        break;
                    case 3:
                        resultInt |= Repeat_Thursday;
                        break;
                    case 4:
                        resultInt |= Repeat_Friday;
                        break;
                    case 5:
                        resultInt |= Repeat_Saturday;
                        break;
                    case 6:
                        resultInt |= Repeat_Sunday;
                        break;
                }
            }
        }
        byte[] result = new byte[2];
        result[1] = (byte) ((resultInt >> 8) & 0x0ff);
        result[0] = (byte) ((resultInt) & 0x0ff);
        return result;
    }

}
