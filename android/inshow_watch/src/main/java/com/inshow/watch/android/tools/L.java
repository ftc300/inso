package com.inshow.watch.android.tools;

import android.app.Activity;
import android.util.Log;

import static com.inshow.watch.android.MessageReceiver.isDebug;
import static com.inshow.watch.android.MessageReceiver.isDebugLogFlag;

/**
 * Created by chendong on 2017/2/9.
 * Log统一管理
 */
public class L {
    // 下面四个是默认INSHOW_LOGTAG的函数
    public static void i(String msg) {
        if (isDebug)
            Log.i(Constants.SystemConstant.IN_SHOW_LOG_TAG, msg);
    }

    public static void d(String msg) {
        if (isDebug)
            Log.d(Constants.SystemConstant.IN_SHOW_LOG_TAG, msg);
    }

    public static void e(String msg) {
        if (isDebug || isDebugLogFlag) Log.d(Constants.SystemConstant.IN_SHOW_LOG_TAG, msg);
//        if(isDebug) {
//            try {
//                FileUtil.writeLogDataToFile(msg, FileUtil.getLogFilePath());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public static void v(String msg) {
        if (isDebug)
            Log.v(Constants.SystemConstant.IN_SHOW_LOG_TAG, msg);
    }
    //下面是传入类名打印log
    public static void i(Class<?> _class,String msg){
        if (isDebug)
            Log.i(_class.getName(), msg);
    }
    public static void d(Class<?> _class,String msg){
        if (isDebug)
            Log.d(_class.getName(), msg);
    }
    public static void d(Activity _class, String msg){
        if (isDebug)
            Log.d(_class.getClass().getName(), msg);
    }
    public static void e(Class<?> _class,String msg){
        if (isDebug)
            Log.e(_class.getName(), msg);
    }
    public static void v(Class<?> _class,String msg){
        if (isDebug)
            Log.v(_class.getName(), msg);
    }
    // 下面是传入自定义INSHOW_LOGTAG的函数
    public static void i(String INSHOW_LOGTAG, String msg) {
        if (isDebug)
            Log.i(INSHOW_LOGTAG, msg);
    }

    public static void d(String INSHOW_LOGTAG, String msg) {
        if (isDebug)
            Log.d(INSHOW_LOGTAG, msg);
    }

    public static void e(String INSHOW_LOGTAG, String msg) {
        if (isDebug)
            Log.e(INSHOW_LOGTAG, msg);
    }

    public static void w(String INSHOW_LOGTAG, String msg) {
        if (isDebug)
            Log.w(INSHOW_LOGTAG, msg);
    }

    public static void v(String INSHOW_LOGTAG, String msg) {
        if (isDebug)
            Log.v(INSHOW_LOGTAG, msg);
    }
}
