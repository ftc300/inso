package com.inshow.watch.android.manager;

import com.inshow.watch.android.tools.L;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.inshow.watch.android.tools.Constants.SystemConstant.WATCH_SYSTEM_START_TIME;

/**
 * Created by chendong on 2017/2/4.
 */
public class BleManager {
    /**
     * 获取手表设定的起始时间的秒数
     *
     * @return
     */
    public static int getWatchSysStartTimeSecs() {
        return getSecondsFromDate(WATCH_SYSTEM_START_TIME);
    }

    //将指定日期转化为秒数
    public static int getSecondsFromDate(String expireDate) {
        if (expireDate == null || expireDate.trim().equals(""))
            return 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(expireDate);
            return (int) (date.getTime() / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    //获取当天时间的秒数
    public static int getTodayTimeSeconds() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(sdf.format(new Date()));
            return (int) (date.getTime() / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public static byte[] I2B_ClockDriver(int value) {
        L.e( "ClockDriver:int=" + value + ";" + "byteString=" + bytesToHexString(I2B_OneBit(value)));
        return I2B_OneBit(value);
    }

    public static byte[] I2B_StepDriver(int value) {
        L.e( "I2B_StepDriver:int=" + value + ";" + "byteString=" + bytesToHexString(I2B_OneBit(value)));
        return I2B_OneBit(value);
    }

    public static byte[] I2B_OneBit(int value) {
        byte[] src = new byte[1];
        src[0] = (byte) (value & 0x0FF);
        return src;
    }

    /**
     * 闹钟设置
     * 1B id：[1..10]
     * 1B 命令: 4打开；3关闭；2设置；1清除；0仅更新状态
     * 1B 状态：3过期；2打开；1关闭；0无效
     * 2B	类型：Bit15-0: 0响一次；1-每天；2法定工作日；3法定节假日；4Sunday；5Monday；6Tuesday；7Wednesday；8Thursday；9Friday；10Saturday
     * 4B	时间：duration
     * @param value
     * @param seconds
     * @param repeatType
     * @return
     */
    public static byte[] setAlarm(int[] value, int seconds, byte[] repeatType) {
        byte[] src = new byte[9];
        src[8] = (byte) ((seconds >> 24) & 0x0FF);
        src[7] = (byte) ((seconds >> 16) & 0x0FF);
        src[6] = (byte) ((seconds >> 8) & 0x0FF);
        src[5] = (byte) (seconds & 0x0FF);
        src[4] = repeatType[1];
        src[3] = repeatType[0];
        src[2] = (byte) (value[2] & 0x0FF);
        src[1] = (byte) (value[1] & 0x0FF);
        src[0] = (byte) (value[0] & 0x0FF);
        L.e( "AlarmClockWriteByteString=" + bytesToHexString(src));
        return src;
    }

    /**
     * Vip联系人
     * @param id
     * @param operate
     * @param content
     * @return
     */
    public static byte[] setVip(int id, int operate,byte[] content){
        byte[] src = new byte[20];
        src[0] = (byte) (id & 0x0FF);
        src[1] = (byte) (operate & 0x0FF);
        System.arraycopy(content,0,src,2,content.length>18?18:content.length);
        L.e("setVipWriteByteString=" + bytesToHexString(src));
        return  src;
    }

    public static byte[] changeVipState(int operate){
        byte[] src = new byte[2];
        src[0] = 0x01;
        src[1] = (byte) (operate & 0x0FF);
        L.e("setVipWriteByteString=" + bytesToHexString(src));
        return  src;
    }

    //通知手表它显示的时间
    public static byte[] I2B_WatchTime(int value, int modeTime) {
        byte[] src = new byte[4];
        src[3] = (byte) ((modeTime >> 24) & 0x0FF);
        src[2] = (byte) ((modeTime >> 16) & 0x0FF);
        src[1] = (byte) ((modeTime >> 8) & 0x0FF);
        src[0] = (byte) (modeTime & 0x0FF);
        L.e( "WatchTime:int=" + value + ";" + "modeTime:" + modeTime + ";byteString=" + bytesToHexString(src));
        return src;
    }
    //通知手表它显示的时间
    public static byte[] I2B_WatchTime( int modeTime) {
        byte[] src = new byte[4];
        src[3] = (byte) ((modeTime >> 24) & 0x0FF);
        src[2] = (byte) ((modeTime >> 16) & 0x0FF);
        src[1] = (byte) ((modeTime >> 8) & 0x0FF);
        src[0] = (byte) (modeTime & 0x0FF);
        L.e(  "WatchPointTime:" + modeTime + ";byteString=" + bytesToHexString(src));
        return src;
    }



    //同步时间
    public static byte[] I2B_SyncTime(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0x0FF);
        src[2] = (byte) ((value >> 16) & 0x0FF);
        src[1] = (byte) ((value >> 8) & 0x0FF);
        src[0] = (byte) (value & 0x0FF);
        L.e( "TimeSync:int=" + value + ";" + "byteString=" + bytesToHexString(src));
        return src;
    }


    /**
     * 间隔提醒	0x3203	Read/write	1Byte	命令: 5复位；4打开；3关闭；2设置；1清除；0仅更新状态
     * 1Byte	状态：2:打开；1关闭；0无效
     * 1Byte	间隔分钟：[1..60]
     * 2Byte	当前间隔秒：[1..3600]
     * @param value
     * @return
     */
    public static byte[] I2B_IntervalRemind(int[] value) {
        byte[] src = new byte[5];
        src[4] = (byte) (value[4] & 0x0FF);
        src[3] = (byte) (value[3] & 0x0FF);
        src[2] = (byte) (value[2] & 0x0FF);
        src[1] = (byte) (value[1] & 0x0FF);
        src[0] = (byte) (value[0] & 0x0FF);
        L.e( "IntervalRemind:byteString=" + bytesToHexString(src));
        return src;
    }


    /**
     *振动提醒
     * @param value
     * @return
     */
    public static  byte[] setWriteVibration(int[] value){
        byte[] src = new byte[8];
        src[7] = (byte) ((value[4] >> 8) & 0x0FF);
        src[6] = (byte) (value[4] & 0x0FF);
        src[5] = (byte) ((value[3] >> 8) & 0x0FF);
        src[4] = (byte) (value[3] & 0x0FF);
        src[3] = (byte) ((value[2] >> 8) & 0x0FF);
        src[2] = (byte) (value[2] & 0x0FF);
        src[1] = (byte) (value[1] & 0x0FF);
        src[0] = (byte) (value[0] & 0x0FF);
        L.e( "setWriteVibration:byteString=" + bytesToHexString(src));
        return src;
    }

    /**
     *
     * @param value
     * @return
     */
    public static byte[] setWriteIntervalByte(int[] value) {
        byte[] src = new byte[5];
        src[4] = (byte) ((value[3] >> 8) & 0x0FF);
        src[3] = (byte) (value[3] & 0x0FF);
        src[2] = (byte) (value[2] & 0x0FF);
        src[1] = (byte) (value[1] & 0x0FF);
        src[0] = (byte) (value[0] & 0x0FF);
        L.e( "IntervalRemind:byteString=" + bytesToHexString(src));
        return src;
    }


    /**
     * 控制位
     * @param value
     * @return
     */
    public static byte[] I2B_Control(int[] value) {
        byte[] src = new byte[4];
        src[3] = (byte) (value[3] & 0x0FF);
        src[2] = (byte) (value[2] & 0x0FF);
        src[1] = (byte) (value[1] & 0x0FF);
        src[0] = (byte) (value[0] & 0x0FF);
        L.e( "I2B_Control:byteString=" + bytesToHexString(src));
        return src;
    }


    //间隔提醒返回剩余时间 当前间隔时间
    public static int getIRRemainInterval(byte[] value) {
        return (value[3] & 0x0FF)+((value[4] & 0x0FF) << 8);
    }

    //间隔提醒返回剩余时间 当前间隔时间
    public static int getIRInterval(byte[] value) {
        return value[2] & 0x0FF;
    }

    //间隔提醒返回的打开状态
    public static int getIRState(byte[] value) {
        return value[1] & 0x0FF;
    }

    //sign  0xFF转成 unsign 0xoFF  的int
    //电池
    public static int B2I_getBattery(byte[] b) {
        return b[0] & 0x0FF;
    }


    //电池电量
    public static int B2I_getBatteryLevel1(byte[] b) {
        return (b[0] & 0x0FF) + ((b[1] & 0x0FF) << 8);
    }

    //电池电量
    public static int[] B2I_getBatteryLevel2(byte[] b) {
        return new int[]{(b[0] & 0x0FF) + ((b[1] & 0x0FF) << 8),(b[2] & 0x0FF) + ((b[3] & 0x0FF) << 8)};
    }


    //计步
    //    当前步数,当前步行时间	0x3101	Read, Notify	4Bytes+4Bytes	0x(12, 0D, 00, 00)

    public static int[] B2I_getStep(byte[] b) {
        int[] ret = new int[2];
        ret[0] = (b[0] & 0x0FF) + ((b[1] & 0x0FF) << 8) + ((b[2] & 0x0FF) << 16) + ((b[3] & 0x0FF) << 24);
        ret[1] = (b[4] & 0x0FF) + ((b[5] & 0x0FF) << 8) + ((b[6] & 0x0FF) << 16) + ((b[7] & 0x0FF) << 24);
        return ret;
    }

    /**
     * 历史计步数据
     * 4B	计步周期唯一标识，写0然后读取，返回当前计步周期数据，连续读返回上一条历史纪录，无效纪录ID为－1
     * 4B	开始时间，秒单位
     * 4B	结束时间，秒单位
     * 4B	计步周期内的步数
     * @param b
     * @return
     */
    public static int[] B2I_getHistoryStep(byte[] b) {
        L.e("B2I_getHistoryStep:"+ bytesToHexString(b));
        int[] ret = new int[4];
        ret[0] = (b[0] & 0x0FF) + ((b[1] & 0x0FF) << 8) + ((b[2] & 0x0FF) << 16) + ((b[3] & 0x0FF) << 24);//id
        ret[1] = (b[4] & 0x0FF) + ((b[5] & 0x0FF) << 8) + ((b[6] & 0x0FF) << 16) + ((b[7] & 0x0FF) << 24);//id
        ret[2] = (b[8] & 0x0FF) + ((b[9] & 0x0FF) << 8) + ((b[10] & 0x0FF) << 16) + ((b[11] & 0x0FF) << 24);//id
        ret[3] = (b[12] & 0x0FF) + ((b[13] & 0x0FF) << 8) + ((b[14] & 0x0FF) << 16) + ((b[15] & 0x0FF) << 24);//id
        L.e("B2I_getHistoryStep:"+"data[0]:"+ret[0]+",data[1]:"+ret[1]+",data[2]:"+ret[2]+",data[3]:"+ret[3]);
        return ret;
    }


    /**
     *  写计步数据GSensor采样数据
     * @param b
     * @return
     */
    public static int[] B2I_getStepSteam(byte[] b) {
        L.e("GSensor:"+ bytesToHexString(b));
        int[] ret = new int[4];
        ret[0] = (b[0] & 0x0FF) + ((b[1] & 0x0FF) << 8) + ((b[2] & 0x0FF) << 16) + ((b[3] & 0x0FF) << 24);//id
        ret[1] = (b[4] & 0x0FF) + ((b[5] & 0x0FF) << 8) + ((b[6] & 0x0FF) << 16) + ((b[7] & 0x0FF) << 24);//id
        ret[2] = (b[8] & 0x0FF) + ((b[9] & 0x0FF) << 8) + ((b[10] & 0x0FF) << 16) + ((b[11] & 0x0FF) << 24);//id
        ret[3] = (b[12] & 0x0FF) + ((b[13] & 0x0FF) << 8) + ((b[14] & 0x0FF) << 16) + ((b[15] & 0x0FF) << 24);//id
        return ret;
    }


    /**
     * 调试手表日志
     * @param b
     * @return
     */
    public static int[] B2I_getWatchLog(byte[] b) {
        int[] ret = new int[4];
        ret[0] = (b[0] & 0x0FF) + ((b[1] & 0x0FF) << 8) + ((b[2] & 0x0FF) << 16) + ((b[3] & 0x0FF) << 24);//id
        ret[1] = (b[4] & 0x0FF) + ((b[5] & 0x0FF) << 8) + ((b[6] & 0x0FF) << 16) + ((b[7] & 0x0FF) << 24);//id
        ret[2] = (b[8] & 0x0FF) + ((b[9] & 0x0FF) << 8) + ((b[10] & 0x0FF) << 16) + ((b[11] & 0x0FF) << 24);//id
        ret[3] = (b[12] & 0x0FF) + ((b[13] & 0x0FF) << 8) + ((b[14] & 0x0FF) << 16) + ((b[15] & 0x0FF) << 24);//id
        L.e("B2I_getHistoryStep:"+"data[0]:"+ret[0]+",data[1]:"+ret[1]+",data[2]:"+ret[2]+",data[3]:"+ret[3]);
        return ret;
    }

    /**
     * 1 Byte	剩余电量百分比
     * 4 Byte	使用总时间,分钟单位
     * 1 Byte	计时消耗电量百分比
     * 1 Byte	计步消耗电量百分比
     * 1 Byte	振动消耗电量百分比
     * 1 Byte	数据传输消耗电量百分比
     * @param b
     * @return
     */
    public static int[] getPowerConsumption(byte[] b) {
        int[] result = new int[4];
        result[0] = b[0] & 0x0FF;
        result[1] = (b[1] & 0x0FF) + ((b[2] & 0x0FF) << 8) + ((b[3] & 0x0FF) << 16) + ((b[4] & 0x0FF) << 24);
        result[2] = b[5] & 0x0FF;
        result[3] =(b[6] & 0x0FF) + ((b[7] & 0x0FF) << 8) + ((b[8] & 0x0FF) << 16) + ((b[9] & 0x0FF) << 24);
        L.e("getPowerConsumption:byteString=" + bytesToHexString(b) +"，电池电量为:"+result[0]);
        return result;
    }

    //控制位
    public static int[] getControl(byte[] b) {
        int[] result = new int[4];
        result[0] = b[0] & 0x0FF;
        result[1] = b[1] & 0x0FF;
        result[2] = b[2] & 0x0FF;
        result[3] = b[3] & 0x0FF;
        L.e("I2B_Control:byteString=" + bytesToHexString(b)+";重启标志:"+result[1]+",绑定标志:"+result[2]);
        return result;
    }


    //当前时间
    public static int B2I_getTime(byte[] b) {
        L.e( "当前时间：" + bytesToHexString(b));
        return (b[0] & 0x0FF) + ((b[1] & 0x0FF) << 8) + ((b[2] & 0x0FF) << 16) + ((b[3] & 0x0FF) << 24);
    }

    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null) return "";
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
//          String hexString = Integer.toHexString(bytes[i] & 0xFF);
            String hexString = Integer.toHexString(bytes[i] & 0x0FF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result += hexString.toUpperCase();
        }
        return result;
    }

}
