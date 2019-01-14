package com.inshow.watch.android;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.inshow.watch.android.act.mainpagelogic.MainAct;
import com.inshow.watch.android.act.user.BirthdayAct;
import com.inshow.watch.android.act.user.SexAct;
import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.manager.SPManager;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.sync.http.ResponseBase;
import com.inshow.watch.android.sync.http.bean.HttpRegister;
import com.inshow.watch.android.tools.Configuration;
import com.inshow.watch.android.tools.Constants;
import com.inshow.watch.android.tools.DataCleanManager;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.TimeUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.inshow.watch.android.sync.http.HttpSyncHelper.INSHOW_HTTP_START_TIME;
import static com.inshow.watch.android.tools.Constants.HttpConstant.TYPE_USER_INFO;
import static com.inshow.watch.android.tools.Constants.SystemConstant.EXTRAS_DEVICE_STATE;
import static com.inshow.watch.android.tools.Constants.SystemConstant.EXTRAS_EVENT_BUS;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_BLUETOOTH_CONNECTED;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_DEVICE_NAME;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_DID;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_MAC;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_MODEL;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_USERID;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_IS_FIRST_OPEN;
import static com.inshow.watch.android.tools.Constants.TimeStamp.USER_KEY;
import static com.inshow.watch.android.tools.Constants.TimeStamp.USER_REGISTER_KEY;

/**
 * 所有插件入口函数，必须实现
 */
public class MessageReceiver {
    public static final boolean hasOpenAsync = true;
    public static final boolean isDebug = true;//调试开关
    public static final boolean isDebugLogFlag = false;//日志调试开关
    public static long deltaTimeFromUTC = 0;//unit:ms
    public static String PRE_FILE_NAME = "inshow_prefer_";
    private boolean firstComing = true;


    private String getPhoneInfo() {
        String phoneInfo = "MOBILE INFO:\n产品名称：" + Build.PRODUCT;
        phoneInfo += "\tCPU型号:" + Build.HARDWARE;
        phoneInfo += "\tCPU类型1:" + Build.CPU_ABI;
        phoneInfo += "\tCPU类型2:" + Build.CPU_ABI2;
        phoneInfo += "\t标签:" + Build.TAGS;
        phoneInfo += "\t手机型号:" + Build.MODEL;
        phoneInfo += "\tSDK版本:" + Build.VERSION.SDK;
        phoneInfo += "\tSDK版本号:" + Build.VERSION.SDK_INT;
        phoneInfo += "\t系统版本:" + Build.VERSION.RELEASE;
        phoneInfo += "\t设备安卓版本:" + Build.VERSION.RELEASE;
        phoneInfo += "\t设备驱动:" + Build.DEVICE;
        phoneInfo += "\t显示:" + Build.DISPLAY;
        phoneInfo += "\t品牌:" + Build.BRAND;
        phoneInfo += "\t主板:" + Build.BOARD;
        phoneInfo += "\t标识:" + Build.FINGERPRINT;
        phoneInfo += "\tid:" + Build.ID;
        phoneInfo += "\t制造商:" + Build.MANUFACTURER;
        phoneInfo += "\t用户组:" + Build.USER;
        phoneInfo += "\t序列号:" + Build.SERIAL;
        return phoneInfo;
    }

}
