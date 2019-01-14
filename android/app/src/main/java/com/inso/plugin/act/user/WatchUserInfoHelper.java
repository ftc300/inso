package com.inso.plugin.act.user;

import android.content.Context;

import com.inso.plugin.manager.SPManager;
import com.inso.plugin.tools.NumUtil;

import static com.inso.plugin.tools.Constants.SettingHelper.*;
import static com.inso.plugin.tools.Constants.SystemConstant.*;


/**
 * Created by chendong on 2017/5/16.
 */

public class WatchUserInfoHelper {
    public Context context;

    public WatchUserInfoHelper(Context context) {
        this.context = context;
    }

    public int getWatchUserHeight(){
        return (int) SPManager.get(context,SP_ARG_HEIGHT,HEIGHT_DEFAULT);
    }

    public int getWatchUserWeight(){
        return (int) SPManager.get(context,SP_ARG_WEIGHT,WEIGHT_DEFAULT);
    }

    public String getWatchUserGender(){
        return (String) SPManager.get(context,SP_ARG_GENDER,GENDER_DEFAULT);
    }
    public String getWatchUserBirth(){
        return (String) SPManager.get(context,SP_ARG_BIRTH,BIRTH_DEFAULT);
    }


    /**
     * 结束时间减去开始时间 转换成活动时长
     * @param duration
     * @return
     */
    public int[] getActiveDuration(int duration)
    {
        int h = duration / 3600;
        int min = duration % 3600 / 60;
        return new int[]{ h , min };
    }

    /**
     * 男性:身高X 0.415=步距
     * 女性:身高X 0.413=步距
     * @return m
     */
    public int getDistance(int step) {
        if(getWatchUserGender().equals("male")) {
            return  NumUtil.getHalfUp(getWatchUserHeight() * 0.415 * step/100);
        }else{
            return  NumUtil.getHalfUp(getWatchUserHeight() * 0.413 * step/100);
        }
    }

    /**
     *  跑步热量（kcal）＝体重（kg）×距离（公里）×1.036
     *  例如：体重60公斤的人，长跑8公里，那么消耗的热量＝60×8×0.7＝497.28 kcal(千卡)
     */
    public int getKCal(int step){
        return NumUtil.getHalfUp(getWatchUserWeight() * getDistance(step) * 0.7 /1000);
    }

}
