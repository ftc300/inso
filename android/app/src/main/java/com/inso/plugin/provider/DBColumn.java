package com.inso.plugin.provider;

import com.inso.plugin.tools.Constants;

import static com.inso.plugin.tools.Constants.OFF;

/**
 * Created by chendong on 2017/3/8.
 */
public class DBColumn {

    /**
     * 手表用户的身体信息
     */
    public interface WatchUserColumns {
        String TABLE_NAME = "WatchUser";
        String ID = "ID";
        String WEIGHT = "WEIGHT";
        String HEIGHT = "HEIGHT";
        String BIRTH = "BIRTH";
        String GENDER = "GENDER";
        int WEIGHT_DEFAULT = Constants.SettingHelper.WEIGHT_DEFAULT;
        int HEIGHT_DEFAULT = Constants.SettingHelper.HEIGHT_DEFAULT;
        String BIRTH_DEFAULT = Constants.SettingHelper.BIRTH_DEFAULT;
        String GENDER_DEFAULT = Constants.SettingHelper.GENDER_DEFAULT;
        Integer ID_DEFAULT = 110;
    }

    /**
     * 间隔提醒
     */
    public interface IntervalColumns {
        String TABLE_NAME = "IntervalTime";
        String ID = "ID";
        String TIME = "TIME";
        String STATUS = "STATUS";
        String START = "START";
        Integer ID_DEFAULT = 110;
        Integer INTERVAL_DEFAULT = 60 * 60;
        Integer START_DEFAULT = 60 * 60;
        String STATUS_DEFAULT = OFF;
    }

    /**
     * 闹钟
     */
    public interface AlarmColumns {
        /* Table name */
        String TABLE_NAME = "AlarmClock";
        String ID = "ID";
        String TIME = "TIME";
        String REPEATTYPE = "REPEATTYPE";
        String DESC = "DESC";
        String STATUS = "STATUS";
        String EXTEND = "EXTEND";
    }

    /**
     * 世界城市 -- 采用读配置文件的形式
     * desperate
     */
    public interface WorldCityColumns {
        /* Table name */
        String TABLE_NAME = "WorldCity";
        String ID = "ID";//城市id
        String ZH_CN = "ZH_CN";//中文名称
        String ZONE = "ZONE";//时区
    }

    /**
     * 用户偏好城市
     */
    public interface PreferCityColumns {
        /* Table name */
        String TABLE_NAME = "PreferCity";
        String CITY_ID = "CITY_ID";//城市ID
        String ZONE = "ZONE";//时区
        String ZH_CN = "ZH_CN";//简体中文
        String EN = "EN";//英文
        String ZH_HK = "ZH_HK";//
        String ZH_TW = "ZH_TW";//
        String IS_SEL = "IS_SEL";//是否设置为手表时间
        String IS_DEFAULT = "IS_DEFAULT";//是否设置为默认时间
    }

    /**
     * 计步数据
     */
    public interface StepColumns {
        /* Table name */
        String TABLE_NAME = "Step";
        String START = "START";//开始时间
        String END = "END";//结束时间
        String STEP = "STEP";//步数
        String DURATION = "DURATION";//运动时长
        String DISTANCE = "DISTANCE";//距离
        String CONSUME = "CONSUME";//消耗
        String DAY = "DAY";//日
        String WEEK = "WEEK";//周
        String MON = "MON";//月
        String YEAR = "YEAR";//年
    }


    /**
     * 不同的KEY提交记录时间
     */
    public interface SyncColumns {
        /* Table name */
        String TABLE_NAME = "Synchronous";
        String KEY = "KEY";//提交时的key
        String TIMESTAMP = "TIMESTAMP";//提交时的时间戳
    }

    /**
     * 配置文件版本
     */
    public interface ConfigVersion {
        /* Table name */
        String TABLE_NAME = "ConfigVersion";
        String NAME = "NAME";//
        String VERSION = "VERSION";//
    }

    /**
     * VIP 联系人
     */
    public interface VIP {
        /* Table name */
        String TABLE_NAME = "VIP";
        String CONTACTID = "CONTACTID";//
        String ID = "ID";//
        String NAME = "NAME";//
        String NUMBER = "NUMBER";//
        String STATUS = "STATUS";//
    }

    /**
     * 缓存一些重要的信息
     * eg:世界时间,节假日列表
     */
    public interface CACHE {
        /* Table name */
        String TABLE_NAME = "CACHE";
        String KEY = "KEY";//
        String VALUE = "VALUE";//
    }

    /**
     * 振动设置
     */
    public interface VibrationSetting {
        /* Table name */
        String TABLE_NAME = "VIBRATION";
        String ID = "ID";
        String STRONGER_STATUS = "STRONGER_STATUS";//振动时长加倍开关
        String NOTDISTURB_STATUS = "NOTDISTURB_STATUS";//免打扰开关
        String START_TIME = "START_TIME";//
        String END_TIME = "END_TIME";//
        Integer ID_DEFAULT = 100;
        Integer STRONGER_STATUS_DEFAULT = 0;//振动时长加倍开关
        Integer NOTDISTURB_STATUS_DEFAULT = 0;//免打扰开关
        Integer START_TIME_DEFAULT = 23 * 60;//
        Integer END_TIME_DEFAULT = 7 * 60;//
    }


    public interface DebugStepColumns {
        String TABLE_NAME = "DebugStep";
        String STARTTIME = "START_TIME";
        String ENDTIME = "END_TIME";
        String STARTSTEP = "START_STEP";
        String ENDSTEP = "END_STEP";
        String GOAL = "GOAL";
        String TYPE = "TYPE";
        String MAC = "MAC";
    }

    public interface DebugStepPeriodColumns {
        String TABLE_NAME = "DebugStepPeriod";
        String START_TIME = "START_TIME";
        String START_STEP = "START_STEP";
        String PERIOD = "PERIOD";//当前状态 ，未开始跑，开始 ，结束
        String MAC = "MAC";
        String TYPE = "TYPE";
    }

}
