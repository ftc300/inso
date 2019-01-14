
package com.inso.plugin.tools;


import java.math.BigDecimal;
import java.util.Locale;

/**
 * Created by chendong on 2017/4/28.
 */
public class NumUtil {


    public static int getHalfUp(double i){
        return (int)Math.round(i);
    }
    /**
     * 以千分位的格式显示
     * @return
     */
    public static String dividerIntNum(int num) {
        return String.format("%,d", num);
    }

    /**
     * 以千分位保留一位小数的格式显示
     * @return
     */
    public static String dividerDoubleNum(double num) {
        return String.format("%,.1f", num);
    }

    /**
     * 保留一位小数的格式显示
     * @return
     */
    public static String doubleNumRestOne(double num) {
        if(num <= 0) return "0";
        if(num < 0.1) return "0.1";
        return String.format(Locale.getDefault(),"%.1f", num);
    }

    /**
     * 时长时间
     * @param seconds
     * @return
     */
    public static  int getRestMin(int seconds){
        int rest = seconds % 3600 ;
        if(rest <= 0) return  0;
        if(rest < 60) return  1;
        return seconds % 3600 / 60 ;
    }

    /**
     * 不满10就补0
     * @return
     */
    public static String formatTwoDigitalNum(int src) {
        return  String.format("%02d", src);
    }

}
