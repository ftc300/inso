
package com.inso.plugin.tools;

import android.annotation.SuppressLint;
import android.content.Context;

import com.inso.R;
import com.inso.plugin.manager.AppController;
import com.inso.plugin.provider.DBHelper;
import com.inso.plugin.sync.http.bean.HttpFestival;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.inso.plugin.tools.Constants.ConfigVersion.FESTIVAL_CHINA;
import static com.inso.plugin.tools.Constants.deltaTimeFromUTC;
import static java.util.Calendar.MONDAY;

/**
 * Created by chendong on 2017/3/20.
 */
public class TimeUtil {

    private static final String DATE_FORMAT_Y = "yyyy";
    private static final String DATE_FORMAT_CHINESE_M = "M";
    private static final String DATE_FORMAT_HHMM = "HH:mm";
    private static final String DATE_FORMAT_HHMMSS = "HH:mm:ss";
    private static final String DATE_FORMAT_M_DIAGONAL_D = "M/d";
    private static final String DATE_FORMAT_CHINESE_MD = "M月d日";
    private static final String DATE_FORMAT_EN_MD = "MMMM d";
    private static final String DATE_FORMAT_CHINESE_YMD = "yyyy年M月d日";
    public static final String DATE_FORMAT_YYYYMMDD = "yyyy-MM-dd";
    private static final String DATE_FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT_YMD = "yyyyMMdd";
    private static final String BEGIN_DATE_DEFAULT = "2017-05-01";
    public static final int BEGIN_DATE_SECOND_DEFAULT = 1493568000;
    public static final String DEFAULT_ZONE = "Asia/Shanghai";
    public static String mZone;

    public static void initZone(DBHelper dbHelper) {
        mZone = dbHelper.getSettingZone();
    }

    public static void releaseZone(){
        mZone = DEFAULT_ZONE;
    }




    /**
     * 获取当前手表的时分
     *
     * @param zone
     * @param calendar HOUR_OF_DAY/ HOUR
     * @return
     */
    public static int[] getWatchTime(String zone, int calendar) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone(zone));
        c.setTimeInMillis(System.currentTimeMillis() + deltaTimeFromUTC);
        return new int[]{c.get(calendar), c.get(Calendar.MINUTE)};
    }

    /**
     * @return
     */
    public static int getTodayZero() {
        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeZone(TimeZone.getTimeZone(zone)); 加了之后不是零点 why？
        calendar.setTimeInMillis(System.currentTimeMillis() + deltaTimeFromUTC);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        L.e("getTodayZero:" + convertLongTimeToDate(calendar.getTimeInMillis() / 1000));
        return (int) (calendar.getTimeInMillis() / 1000);
    }

    public static int getTodayZero(String zone) {
        int nowtime =  getNowTimeSeconds(zone);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(zone));
        int hour =  calendar.get(Calendar.HOUR_OF_DAY);
        int min =  calendar.get(Calendar.MINUTE);
        int sec =  calendar.get(Calendar.SECOND);
        int ret = nowtime - hour * 3600 - min * 60 - sec;
        L.e(hour + "," + min + "," + sec + "," + ret);
        return ret;
    }

    public enum TimeCompare{
        MORE,
        LESS ,
        EQUAL
    }
    // 都转换成年月日
    public static TimeCompare getMTS(String zone) {
        SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_YMD, Locale.getDefault());
        sdf1.setTimeZone(TimeZone.getTimeZone(zone));
        SimpleDateFormat sdf2 = new SimpleDateFormat(DATE_FORMAT_YMD, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + deltaTimeFromUTC);
        int serverTime = Integer.parseInt(sdf2.format(calendar.getTime()));
        int zoneTime = Integer.parseInt(sdf1.format(calendar.getTime()));
        L.e("server_time:" + serverTime + ",zoneTime:" + zoneTime );
        if(zoneTime > serverTime) {
            return TimeCompare.MORE;
        }else if(zoneTime == serverTime){
            return TimeCompare.EQUAL;
        }else {
            return TimeCompare.LESS;
        }
    }


    /**
     * eg:11:30 ,11*3600+30*60
     *
     * @param zone
     * @return
     */
    public static int getTodayDeltaSeconds(String zone) {
        return TimeUtil.getNowTimeSeconds(zone) - TimeUtil.getTodayTimeSeconds(zone);
    }

    /**
     * 是否是节假日
     *
     * @param day
     * @return
     */
    public static boolean isHoliday(DBHelper dbHelper, String day) {
        return getAllHoliday(dbHelper).contains(day);
    }

    /**
     * 所有假期
     *
     * @param dbHelper
     * @return
     */
    public static List<String> getAllHoliday(DBHelper dbHelper) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD, Locale.getDefault());
//        HttpFestival bean = AppController.getGson().fromJson(FileUtil.ReadFile(context,FileUtil.getFestivaFilePath()),HttpFestival.class);
        HttpFestival bean = AppController.getGson().fromJson(dbHelper.getCache(FESTIVAL_CHINA), HttpFestival.class);
        List<String> list = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int year = c.get(Calendar.YEAR);
        Calendar c2 = Calendar.getInstance();
        c2.set(year + 1, 0, 7);
        while (c.compareTo(c2) < 0) {
            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                list.add(sdf.format(c.getTime()));
            }
            c.add(Calendar.DATE, 1);
        }
        List<HttpFestival.Festival> festivalList = bean.getList();
        for (HttpFestival.Festival item : festivalList) {
            // type 1: 节假日，2：工作日
            if (item.getType() == 1) {
                list.add(item.getDate());
            } else if (item.getType() == 2) {
                list.remove(item.getDate());
            }
        }
        return list;
    }

    /**
     * 周一:1 周二：2 ....周日：7
     *
     * @param pTime
     * @return
     */
    public static int getDayForWeek(String pTime) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD, Locale.getDefault());
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int dayForWeek;
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }

    //获取当前时间的秒数

    /**
     *
     * @param zone
     * @return
     */
    public static int getNowTimeSeconds(String zone) {
        float ONE_HOUR_MILLIS = 60 * 60 * 1000;
        float ONE_HOUR_SECONDS = 60 * 60 ;
        float SERVER_TIME_ZONE_ID = 8;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + deltaTimeFromUTC);
        int ret = (int)(calendar.getTimeInMillis()/1000);
        TimeZone timeZone = TimeZone.getTimeZone(zone);
        Date nowDate = new Date();
        float offsetFromUtc = timeZone.getOffset(nowDate.getTime()) / ONE_HOUR_MILLIS;
        L.d("offsetFromUtc：" + offsetFromUtc);
        float offset = (offsetFromUtc - SERVER_TIME_ZONE_ID) * ONE_HOUR_SECONDS;
        return  ret + (int)offset;
    }


    /**
     * todo 获取服务端数据
     * @param zone
     * @return
     */
    public static int getUtcOffsetMinute(String zone){
        float ONE_MIN_MILLIS = 60 *1000;
        TimeZone timeZone = TimeZone.getTimeZone(zone);
        Date nowDate = new Date();
        return (int) (timeZone.getOffset(nowDate.getTime()) / ONE_MIN_MILLIS);
    }
//    //获取当前时间的秒数
    //201/10/27 remove 使用utc时间
//    public static int getNowTimeSeconds() {
//        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YMDHMS, Locale.getDefault());
//        Date date = null;
//        try {
//            date = sdf.parse(sdf.format(new Date()));
//            return (int) (date.getTime() / 1000);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }


    //获取当前时间的秒数
    public static int getNowTimeSeconds() {
//        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YMDHMS, Locale.getDefault());
//        sdf.setTimeZone(TimeZone.getTimeZone(DEFAULT_ZONE));
//        Date date = null;
//        try {
//            date = sdf.parse(sdf.format(new Date()));
//            return (int) ((date.getTime() + deltaTimeFromUTC) / 1000);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return 0;
//        }
        return (int)((System.currentTimeMillis() + deltaTimeFromUTC)/1000);
    }

    //获取当天时间的秒数
    public static int getTodayTimeSeconds(String zone) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone(zone));
        Date date = null;
        try {
            date = sdf.parse(sdf.format(new Date()));
            return (int) (date.getTime() / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public static String getRegisterTime(long time,DBHelper dbHelper) {
        if (time <= BEGIN_DATE_SECOND_DEFAULT) return BEGIN_DATE_DEFAULT;
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone(dbHelper.getSettingZone()));
        return sdf.format(d);
    }

    /**
     * 获取当前时间字符串
     *
     * @return
     */
    public static String getNowTimeString() {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat(DATE_FORMAT_YMDHMS, Locale.getDefault());
        return format.format(date);
    }


    /**
     * 获取当前时间字符串
     *
     * @return
     */
    public static String getNowTimeStringYMD(String zone) {
        Date date = new Date();
        date.setTime(System.currentTimeMillis() + deltaTimeFromUTC);
        DateFormat format = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD, Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone(zone));
        return format.format(date);
    }

    /**
     * 获取手表设定的起始时间的秒数
     * @return
     */
    public static int getWatchSysStartTimeSecs() {
        return 951840000;
    }

    /**
     * 获取形如x月y日
     *
     * @return
     */
    public static String getMonDay(Context context, String zone) {
        Date curDate = new Date();
        curDate.setTime(System.currentTimeMillis() + deltaTimeFromUTC);
        final SimpleDateFormat format = new SimpleDateFormat(context.getString(R.string.format_date_no_year), Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone(zone));
        return format.format(curDate);
    }

    /**
     * @param zone
     * @return
     */
    public static String getHHMM(String zone) {
        Date curDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_HHMM, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone(zone));
        curDate.setTime(System.currentTimeMillis() + deltaTimeFromUTC);
        return sdf.format(curDate);
    }

    /**
     * 星期几
     *
     * @param mContext
     * @param zone
     * @return
     */
    public static String getWeekOfDate(Context mContext, String zone) {
        Date curDate = new Date();
        curDate.setTime(System.currentTimeMillis() + deltaTimeFromUTC);
        String[] weekDays = mContext.getResources().getStringArray(R.array.week_foreign);
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.setTimeZone(TimeZone.getTimeZone(zone));
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * @param time 秒
     * @return
     */
    public static String getMD(long time) {
        Calendar gc = Calendar.getInstance();
        if (null != mZone) {
            gc.setTimeZone(TimeZone.getTimeZone(mZone));
        }else {
            gc.setTimeZone(TimeZone.getDefault());
        }
        gc.setTimeInMillis(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_M_DIAGONAL_D, Locale.getDefault());
        if (null != mZone) {
            format.setTimeZone(TimeZone.getTimeZone(mZone));
        }else {
            gc.setTimeZone(TimeZone.getDefault());
        }
        return format.format(gc.getTime());
    }

    /**
     * @param time 秒
     * @return
     */
    public static String convertLongTimeToDate(long time) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_YMDHMS, Locale.getDefault());
        return format.format(gc.getTime());
    }

    /**
     * @param time 毫秒
     * @return
     */
    public static String convertLongMilliTimeToDate(long time) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(time);
        SimpleDateFormat format = null;
        if (time / 1000 - getTodayZero() > 0) {
            format = new SimpleDateFormat(DATE_FORMAT_HHMMSS, Locale.getDefault());
            return "今天 " + format.format(gc.getTime());
        } else if (getTodayZero() - time / 1000 < 3600 * 24) {
            format = new SimpleDateFormat(DATE_FORMAT_HHMMSS, Locale.getDefault());
            return "昨天 " + format.format(gc.getTime());
        } else {
            format = new SimpleDateFormat(DATE_FORMAT_YMDHMS, Locale.getDefault());
            return format.format(gc.getTime());
        }
    }

    /**
     * 周起始终止日期  eg:4/17-4/23
     *
     * @param time s
     * @return
     */
    @SuppressLint("WrongConstant")
    public static String getWeekBegEnd(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_M_DIAGONAL_D, Locale.getDefault());
        Calendar begin = Calendar.getInstance();
        Calendar end = new GregorianCalendar();
        begin.setFirstDayOfWeek(MONDAY);
        begin.setTimeInMillis(time * 1000);
        begin.set(Calendar.DAY_OF_WEEK, begin.getFirstDayOfWeek()); // 周一
        end.setFirstDayOfWeek(MONDAY);
        end.setTimeInMillis(time * 1000);
        end.set(Calendar.DAY_OF_WEEK, end.getFirstDayOfWeek() + 6); //  周日
        return sdf.format(begin.getTime()) + "-" + sdf.format(end.getTime());
    }

    /**
     * 月份 eg:1月
     *
     * @param time s
     * @return
     */
    public static String getMon(long time) {
        Calendar gc = Calendar.getInstance();
        if (null != mZone) {
            gc.setTimeZone(TimeZone.getTimeZone(mZone));
        }else {
            gc.setTimeZone(TimeZone.getDefault());
        }
        gc.setTimeInMillis(time * 1000);
        return new SimpleDateFormat(DATE_FORMAT_CHINESE_M, Locale.getDefault()).format(gc.getTime());
    }

    /**
     * 年份 eg:2017
     *
     * @param time s
     * @return
     */
    public static String getYear(long time) {
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_Y, Locale.getDefault());
        gc.setTimeInMillis(time * 1000);
        return format.format(gc.getTime());
    }


    /**
     * @param beginDate
     * @return List
     */
//    public static List<Date> getDayDates(Date beginDate, DBHelper dbHelper) {
//        List<Date> lDate = new ArrayList<>();
//        if (null != mZone) {
//            TimeZone.setDefault(TimeZone.getTimeZone(mZone));
//        }
//        Date endDate = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_HHMM, Locale.getDefault());
//        sdf.setTimeZone(TimeZone.getTimeZone(dbHelper.getSettingZone()));
//        endDate.setTime(System.currentTimeMillis() + deltaTimeFromUTC);
//        lDate.add(beginDate);
//        Calendar cal = Calendar.getInstance();
//        if (null != mZone) {
//            cal.setTimeZone(TimeZone.getTimeZone(mZone));
//        }
//        cal.setTime(beginDate);
//        while (true) {
//            cal.add(Calendar.DAY_OF_MONTH, 1);
//            if (endDate.after(cal.getTime())) {
//                lDate.add(cal.getTime());
//            } else {
//                break;
//            }
//        }
//        return lDate;
//    }
    public static List<Date> getDayDates(Date beginDate, DBHelper dbHelper) {
        SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_FORMAT_M_DIAGONAL_D, Locale.getDefault());
        if (null != mZone) {
            sdf1.setTimeZone(TimeZone.getTimeZone(mZone));
        }else {
            sdf1.setTimeZone(TimeZone.getDefault());
        }
        L.e("" + mZone);
        List<Date> lDate = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        if (null != mZone) {
            calendar.setTimeZone(TimeZone.getTimeZone(mZone));
        }else {
            sdf1.setTimeZone(TimeZone.getDefault());
        }
        calendar.setTimeInMillis(getNowTimeSeconds(dbHelper.getSettingZone()) * 1000L);
        Date endDate = calendar.getTime();
        L.e("getEndDate:" + sdf1.format(endDate));
//        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_HHMM, Locale.getDefault());
//        sdf.setTimeZone(TimeZone.getTimeZone(dbHelper.getSettingZone()));
        lDate.add(beginDate);
        Calendar cal = Calendar.getInstance();
        if (null != mZone) {
            cal.setTimeZone(TimeZone.getTimeZone(mZone));
        }else {
            cal.setTimeZone(TimeZone.getDefault());
        }
        cal.setTime(beginDate);
        while (true) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            if (endDate.after(cal.getTime())) {
                lDate.add(cal.getTime());
            } else {
                Date date = lDate.get(lDate.size() - 1);
                if (!sdf1.format(date).equals(sdf1.format(endDate))) {
                    if(endDate.getTime() < beginDate.getTime() && lDate.size() == 1) {
                        lDate.add(0, endDate);
                    }else {
                        lDate.add(endDate);
                    }
                }
                break;
            }
        }
//        for (Date date : lDate) {
//            L.e("getDayDates:" + sdf1.format(date));
//        }
        return lDate;
    }

    /**
     * @param beginDate
     * @return List
     */
    public static List<Date> getMonDates(Date beginDate) {
        List<Date> lDate = new ArrayList<>();
        Date endDate = new Date();
        endDate.setTime(System.currentTimeMillis() + deltaTimeFromUTC);
        Calendar cal = Calendar.getInstance();
        cal.setTime(beginDate);
        //开始日期月份第一天，若现在时间为9/13,开始时间为8/15(下个月9/15)，则只能显示8月
        cal.set(Calendar.DAY_OF_MONTH, 1);
        if (System.currentTimeMillis() <= cal.getTimeInMillis()) {//特殊
            lDate.add(endDate);
            return lDate;
        }
        lDate.add(beginDate);
        while (true) {
            cal.add(Calendar.MONTH, 1);
            if (endDate.after(cal.getTime())) {
                lDate.add(cal.getTime());
            } else {
                break;
            }
        }
        return lDate;
    }

//    public static List<DataSheetTime> getUiWeek(String BeginDate) {
//        List<DataSheetTime> ret = new ArrayList<>();
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD, Locale.getDefault());
//            Date dB = sdf.parse(BeginDate);
//            for (Date item : getWeeKDates(dB, new Date())) {
//                ret.add(new DataSheetTime(getWeekBegEnd(item.getTime() / 1000), getYear(item.getTime() / 1000)));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return ret;
//    }

    /**
     * @param beginDate
     * @return List
     */
    public static List<DataSheetTime> getUiWeek(String beginDate) {
        List<DataSheetTime> ret = new ArrayList<>();
        Date endDate = new Date();
        endDate.setTime(System.currentTimeMillis() + deltaTimeFromUTC);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD, Locale.getDefault());
        Date bD = null;
        try {
            bD = sdf.parse(beginDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        endDate.setTime(System.currentTimeMillis() + deltaTimeFromUTC);
        Calendar begCal = Calendar.getInstance();
        begCal.setTime(bD);
        begCal.setFirstDayOfWeek(MONDAY);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        endCal.setFirstDayOfWeek(MONDAY);//周一为一周的开始 默认为周日
        if (System.currentTimeMillis() <= begCal.getTimeInMillis()) {//特殊
            int weekOfYear = endCal.get(Calendar.WEEK_OF_YEAR);
            ret.add(new DataSheetTime(getWeekBegEnd(endDate.getTime() / 1000), getYear(endDate.getTime() / 1000), weekOfYear));
            return ret;
        }
        int weekOfYear = begCal.get(Calendar.WEEK_OF_YEAR);
        ret.add(new DataSheetTime(getWeekBegEnd(bD.getTime() / 1000), getYear(bD.getTime() / 1000), weekOfYear));
        while (true) {
            begCal.add(Calendar.WEEK_OF_YEAR, 1);
            weekOfYear = begCal.get(Calendar.WEEK_OF_YEAR);
            if (endDate.after(begCal.getTime())) {
                ret.add(new DataSheetTime(getWeekBegEnd(begCal.getTimeInMillis() / 1000), getYear(begCal.getTimeInMillis() / 1000), weekOfYear));
            } else {
                if (getWeekBegEnd(begCal.getTimeInMillis() / 1000).equals(getWeekBegEnd(endCal.getTimeInMillis() / 1000))) {
                    ret.add(new DataSheetTime(getWeekBegEnd(endCal.getTimeInMillis() / 1000), getYear(endCal.getTimeInMillis() / 1000), weekOfYear));
                }
                break;
            }
        }
        return ret;
    }

    /**
     * 日期的后几天 周日期 以周一开始 （api是以周末）
     *
     * @param date
     * @param daynum
     * @return
     */
    public static Date getAfterDays(Date date, int daynum) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + daynum);
        return c.getTime();
    }


    public static List<DataSheetTime> getUiDays(String BeginDate, DBHelper dbHelper) {
        List<DataSheetTime> ret = new ArrayList<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD, Locale.getDefault());
            L.e("" + mZone);
            if (null != mZone) {
                sdf.setTimeZone(TimeZone.getTimeZone(mZone));
            }else {
                sdf.setTimeZone(TimeZone.getDefault());
            }
            Date dB = sdf.parse(BeginDate);
            for (Date item : getDayDates(dB, dbHelper)) {
                ret.add(new DataSheetTime(getMD(item.getTime() / 1000), getYear(item.getTime() / 1000)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }


    public static List<DataSheetTime> getUiMon(String BeginDate) {
        List<DataSheetTime> ret = new ArrayList<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD, Locale.getDefault());
            Date dB = sdf.parse(BeginDate);
            for (Date item : getMonDates(dB)) {
                ret.add(new DataSheetTime(getMon(item.getTime() / 1000), getYear(item.getTime() / 1000)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }


    public static class DataSheetTime {
        public String year;
        public String uidate;
        public int weekOfYear;

        public DataSheetTime(String uidate, String year, int weekOfYear) {
            this.uidate = uidate;
            this.year = year;
            this.weekOfYear = weekOfYear;
        }

        public DataSheetTime(String uidate, String year) {
            this.uidate = uidate;
            this.year = year;
        }

        @Override
        public String toString() {
            return "DataSheetTime{" +
                    "year='" + year + '\'' +
                    ", uidate='" + uidate + '\'' +
                    ", weekOfYear=" + weekOfYear +
                    '}';
        }
    }


    /**
     * 间隔提醒剩余时间
     *
     * @param time
     * @return
     */
    public static String getRemainDigitalNum(int time) {
        NumberFormat formatter = new DecimalFormat("00");
        if (time > 3600) return "59:59";
        return formatter.format(time / 60) + ":" + formatter.format(time % 60);
    }

    /**
     * @return
     */
    public static String getAjustTime(int h, int m) {
        NumberFormat formatter = new DecimalFormat("00");
        return formatter.format(h) + ":" + formatter.format(m);
    }


    /**
     * 获取校准时间
     *
     * @param h
     * @param m
     * @return
     */
    public static String getAdjustHHMM(int h, int m) {
        NumberFormat formatter = new DecimalFormat("00");
        return formatter.format(h) + ":" + formatter.format(m);
    }

    /**
     * 整点剩余时间
     *
     * @return
     */
    public static int getIntegralDeltaTime(DBHelper dbHelper) {
        L.d("getIntegralDeltaTime:" + getNowTimeSeconds(dbHelper.getSettingZone()) + ",dbHelper.getSettingZone():" + dbHelper.getSettingZone());
        return 3600 - getNowTimeSeconds(dbHelper.getSettingZone()) % 3600;
    }


    /**
     * 电池使用的时间
     *
     * @param time
     * @return
     */
    public static String getUsedTime(final Context context, int time) {
        final int day = time / (24 * 60);
        final int hour = (time % (24 * 60)) / 60;
        final int minute = (time % (24 * 60) % 60);
        return Configuration.getInstance().LocaleHandler2(context, new Configuration.LocaleHandler2() {
            @Override
            public String enHandle() {
                return "Battery works for " + getEnDataUnit(day, context.getString(R.string.time_day)) + getEnDataUnit(hour, context.getString(R.string.time_hour)) + getEnDataUnit(minute, context.getString(R.string.time_min));
            }

            @Override
            public String defaultHandle() {
                return new StringBuilder(context.getString(R.string.have_used))
                        .append(getDefaultDataUnit(day, context.getString(R.string.time_day)))
                        .append(getDefaultDataUnit(hour, context.getString(R.string.time_hour)))
                        .append(getDefaultDataUnit(minute, context.getString(R.string.time_min)))
                        .toString();
            }
        });
    }


    /**
     * @param data
     * @param unit
     * @return
     */
    public static String getEnDataUnit(int data, String unit) {
        if (data <= 0) return "";
        return data + " " + unit + " ";
    }

    /**
     * @param data
     * @param unit
     * @return
     */
    public static String getDefaultDataUnit(int data, String unit) {
        return data > 0 ? data + unit : "";
    }

}





