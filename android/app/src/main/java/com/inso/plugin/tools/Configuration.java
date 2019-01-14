package com.inso.plugin.tools;

import android.content.Context;
import android.support.annotation.StringDef;
import android.text.TextUtils;


import java.util.Locale;

import static com.inso.plugin.tools.Configuration.SettingEnum.*;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/12/19
 * @ 描述:
 */

public class Configuration {
    private static Configuration configuration;

    public synchronized static Configuration getInstance() {
        if (null == configuration) {
            configuration = new Configuration();
        }
        return configuration;
    }

    public static class SettingEnum {
        public final static String LANGUAGE_EN = "en_US";
        public final static String LANGUAGE_CN = "zh_CN";
        public final static String LANGUAGE_TW = "zh_TW";
        public final static String LANGUAGE_HK = "zh_HK";
        //    "cn":中国大陆 "tw":台湾 "sg":新加坡 "in":印度
        public final static String SERVER_CN = "cn";
        public final static String SERVER_TW = "tw";
        public final static String SERVER_SG = "sg";
        public final static String SERVER_IN = "in";
        public final static String SERVER_HK = "hk";

    }

    @StringDef({LANGUAGE_EN, LANGUAGE_CN, LANGUAGE_TW, LANGUAGE_HK})
    public @interface SelectLanguage {

    }

    @StringDef({SERVER_CN, SERVER_TW, SERVER_SG, SERVER_IN})
    public @interface SelectServer {

    }


    @SelectLanguage
    public String LocaleHandler(Context context, LocaleHandler handler) {
        String s = context.getResources().getConfiguration().locale.toString();
        if (equalsElements(LANGUAGE_CN, s)) {
            handler.cnHandle();
            return LANGUAGE_CN;
        } else if (equalsElements(LANGUAGE_EN, s)) {
            handler.enHandle();
            return LANGUAGE_EN;
        } else if (equalsElements(LANGUAGE_TW, s)) {
            handler.twHandle();
            return LANGUAGE_TW;
        } else if (equalsElements(LANGUAGE_HK, s)) {
            handler.hkHandle();
            return LANGUAGE_HK;
        } else {
            handler.cnHandle();
            return LANGUAGE_CN;
        }
    }

    public String LocaleHandler2(Context context, LocaleHandler2 handler) {
        String s = context.getResources().getConfiguration().locale.toString();
        if (equalsElements(LANGUAGE_EN, s)) {
            return handler.enHandle();
        } else {
            return handler.defaultHandle();
        }
    }

    public static String ServerHandle(ServerHandler handler) {
        String server = "";
//        if (XmPluginHostApi.instance().getApiLevel() >= 60) {
//            server = XmPluginHostApi.instance().getGlobalSettingServer(false);
//        } else {
//            server =  XmPluginHostApi.instance().getGlobalSettingServer();
//        }
        server = SERVER_CN;
        L.e("ServerHandle : "+server);
        if (equalsElements(SERVER_TW, server)) {
            return handler.twServer();
        } else if (equalsElements(SERVER_CN, server)) {
            return handler.cnServer();
        } else if (equalsElements(SERVER_HK, server)) {
            return handler.hkServer();
        } else {
            return handler.defaultServer();
        }
    }


    public void ServerHandle(ServerHandler2 handler) {
        String server = "";
//        if (XmPluginHostApi.instance().getApiLevel() >= 60) {
//            server =  XmPluginHostApi.instance().getGlobalSettingServer(false);
//        } else {
//            server =  XmPluginHostApi.instance().getGlobalSettingServer();
//        }
        server = SERVER_CN;
        if (equalsElements(SERVER_CN, server)) {
            handler.cnServer();
        } else {
            handler.defaultServer();
        }
    }


    public static Locale getLocale(Context context) {
        return context.getResources().getConfiguration().locale;
    }

//    public static String getServerConfigDatabaseName(ServerHandler handler){
//        if (equalsElements(SERVER_TW, XmPluginHostApi.instance().getGlobalSettingServer())) {
//            handler.twServer();
//        } else {
//            handler.defaultServer();
//        }
//    }

    /**
     * @param e1
     * @param e2
     * @return
     */
    public static boolean equalsElements(String e1, String e2) {
        return TextUtils.equals(e1.toUpperCase().trim(), e2.toUpperCase().trim());
    }

    public interface LocaleHandler {
        void cnHandle();

        void twHandle();

        void hkHandle();

        void enHandle();

        void defaultHandle();
    }


    /**
     * 英文单复数
     */
    public interface LocaleHandler2 {
        String enHandle();

        String defaultHandle();
    }


    public interface ServerHandler2 {

        void defaultServer();

        void cnServer();
    }


    public interface ServerHandler {

        String defaultServer();//默认显示为中文简体

        String cnServer();

        String twServer();

        String hkServer();
    }

}
