package com.inso.plugin.act.datasheet;

import android.support.annotation.NonNull;
import android.widget.TextView;

/**
 * Comment:
 * Author: ftc300
 * Date: 2018/10/30
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class AccessBarData {
    public String time;
    public TextView[] tvArr;

    public AccessBarData() {
    }

    public AccessBarData(String arg_time, @NonNull TextView[] arg_tvArr) {
        time = arg_time;
        tvArr = arg_tvArr;
    }

    @Override
    public String toString() {
        try {
            return time + " " + tvArr[0].getText() + tvArr[1].getText() + tvArr[2].getText() + tvArr[3].getText();
        }catch (Exception e){
            return "";
        }
    }

}
