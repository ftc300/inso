
package com.inshow.watch.android.tools;

/**
 * Created by chendong on 2017/2/4.
 */
public class Constants {
    public static final String ON = "on";
    public static final String OFF = "off";

    //常量管理
    public static class SystemConstant {
        public static final int BJID = 1; //北京ID
        public static final int TBID = 261; //台北ID
        public static final int HKID = 117; //香港ID
        public static final String IN_SHOW_LOG_TAG = "InShow_LogTAG";
        public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
        public static final String EXTRAS_DEVICE_MAC = "DEVICE_ADDRESS";
        public static final String EXTRAS_EVENT_BUS = "EVENTBUS";
        public static final String EXTRAS_DEVICE_STATE = "EXTRAS_DEVICE_STATE";
        public static final String WATCH_SYSTEM_START_TIME = "2000-03-01";
        public static final String SP_ARG_MAC = "SP_ARG_MAC";
        public static final String SP_ARG_MODEL = "SP_ARG_MODEL";
        public static final String SP_ARG_USERID = "SP_ARG_USERID";
        public static final String SP_ARG_DID = "SP_ARG_DID";
        public static final String SP_ARG_FIRMWARE_VERSION = "SP_ARG_FIRMWARE_VERSION";
        public static final String SP_ARG_DEVICE_NAME = "SP_ARG_DEVICE_NAME";
        public static final String SP_ARG_BIRTH = "SP_ARG_BIRTH";
        public static final String SP_ARG_HEIGHT = "SP_ARG_HEIGHT";
        public static final String SP_ARG_WEIGHT = "SP_ARG_WEIGHT";
        public static final String SP_ARG_GENDER = "SP_ARG_GENDER";
        public static final String SP_ARG_HAS_DEFAULT_CITY = "SP_ARG_HAS_DEFAULT_CITY"; //切换服务器导致的问题
        public static final String SP_ARG_BLUETOOTH_CONNECTED = "SP_ARG_BLUETOOTH_CONNECTED";
        public static final String SP_ARG_DFU_MODE = "SP_ARG_DFU_MODE";
        public static final String SP_DEBUG_ARG_LOCAL_TIME = "SP_DEBUG_ARG_LOCAL_TIME";
        public static final String SP_DEBUG_ARG_BLE_SECURITY = "SP_DEBUG_ARG_BLE_SECURITY";
        public static final String SP_DEBUG_FLAG = "SP_DEBUG_FLAG";
        public static final String SP_DEBUG_WATCHLOG_FLAG = "SP_DEBUG_WATCHLOG_FLAG";
        public static final String SP_DEBUG_DFU = "SP_DEBUG_DFU";
        public static final String SP_DB_VERSION = "SP_DB_VERSION";
        public static final String SP_IS_FIRST_OPEN = "isFirstOpen";
        public static final String SP_MAC_UPLOADED_MAC = "SP_MAC_UPLOADED_MAC";
        public static final String SP_INITDB_FIRST = "SP_INITDB_FIRST";
        public static final String SP_INCOMING_SWITCH = "SP_INCOMING_SWITCH";
        public static final String SP_VIP_OLD_PLUGIN = "SP_VIP_OLD_PLUGIN";
    }

    //UUID管理
    public static class GattUUIDConstant {
        public static final String BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
        public static final String CHARACTERISTIC_BATTERY = "00002a19-0000-1000-8000-00805f9b34fb";
        public static final String IN_SHOW_SERVICE = "c99a3001-7f3c-4e85-bde2-92f2037bfd42";
        public static final String CHARACTERISTIC_TODAY_STEP = "c99a3101-7f3c-4e85-bde2-92f2037bfd42";
        public static final String CHARACTERISTIC_CONTROL = "c99a3102-7f3c-4e85-bde2-92f2037bfd42";
        public static final String BATTERY_LEVEL = "c99a3103-7f3c-4e85-bde2-92f2037bfd42";
        public static final String CHARACTERISTIC_CURRENT_TIME = "c99a3104-7f3c-4e85-bde2-92f2037bfd42";
        public static final String CHARACTERISTIC_CLOCK_DRIVER = "c99a3105-7f3c-4e85-bde2-92f2037bfd42";
        public static final String CHARACTERISTIC_STEP_DRIVER = "c99a3106-7f3c-4e85-bde2-92f2037bfd42";
        public static final String CHARACTERISTIC_SYNC_WATCH_TIME = "c99a3107-7f3c-4e85-bde2-92f2037bfd42";
        public static final String CHARACTERISTIC_STEP_DRIVER_COMPLETE = "c99a3108-7f3c-4e85-bde2-92f2037bfd42";
        public static final String CHARACTERISTIC_SYNC_CURRENT_TIME = "c99a3109-7f3c-4e85-bde2-92f2037bfd42";
        public static final String CHARACTERISTIC_ALARM_CLOCK = "c99a3201-7f3c-4e85-bde2-92f2037bfd42";
        public static final String CHARACTERISTIC_VIBRATION_SETTING = "c99a3202-7f3c-4e85-bde2-92f2037bfd42";
        public static final String CHARACTERISTIC_INTERVAL_REMIND = "c99a3203-7f3c-4e85-bde2-92f2037bfd42";
        public static final String CHARACTERISTIC_HISTORY_STEP = "c99a3204-7f3c-4e85-bde2-92f2037bfd42";
        public static final String CHARACTERISTIC_DEBUG_LOG = "c99a3206-7f3c-4e85-bde2-92f2037bfd42";
        public static final String CHARACTERISTIC_POWER_CONSUMPTION = "c99a3300-7f3c-4e85-bde2-92f2037bfd42";
        public static final String CHARACTERISTIC_VIP = "c99a3205-7f3c-4e85-bde2-92f2037bfd42";
    }

    public static class HttpConstant {
        public static final String URL_SET_USER_DEVICE_DATA = "/user/set_user_device_data";
        public static final String URL_GET_USER_DEVICE_DATA = "/user/get_user_device_data";
        public static final String URL_GET_UTC = "/device/get_utc_time";
        public static final String URL_GET_APP_CONFIG = "/device/getThirdConfig";
        public static final String TYPE_PROP = "prop";
        public static final String TYPE_USER_INFO = "user_info";
    }

    public static class SettingHelper {
        public static final int HEIGHT_DEFAULT = 170;
        public static final String BIRTH_DEFAULT = "1990-01";
        public static final String GENDER_DEFAULT = "male";
        public static final int WEIGHT_DEFAULT = 70;
    }

    public static class TimeStamp {
        //=========标记各个key更新到服务端的时间
        public static final String USER_REGISTER_KEY = "user_register";
        public static final String USER_KEY = "user";
        public static final String INTERVAL_ALARM_KEY = "interval_alarm";
        public static final String VIBRATE_SETTING_KEY = "vibrate_setting";
        public static final String NORMAL_ALARM_KEY = "normal_alarm";
        public static final String WORLD_CITY_KEY = "world_city";
        public static final String VIP_KEY = "vip";
        public static final String STEPS_KEY = "steps";
        public static final String CURRENT_STEPS = "current_steps";
        public static final String DFU_KEY = "dfu_version";
        public static final String BATTERY_LEVEL_KEY = "battery_level";
        public static final String REPORT_STATUS_KEY = "report_status";
        public static final String HTTP_SYNC_KEY = "http_sync_key";
        public static final String DEVICE_SYNC_KEY = "device_sync_key";
        public static final String TIMESTAMP_INFO = "timestamp";
        public static final String POINTER_ADJUST = "pointer_adjust";
        public static final String HTTP_MAC = "mac";
    }


    public static class ConfigVersion {
        public static final String FESTIVAL_CHINA = "festival_china";
        public static final String WORLD_CITY = "world_city";
        public static final String CONFIG_VERSION = "config_version";
        public static final Integer VERSION_DEFAULT = 0;
    }

}
