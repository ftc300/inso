package com.inso.plugin.act.mainpagelogic;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.inso.R;
import com.inso.plugin.manager.SPManager;
import com.inso.plugin.tools.L;
import com.inso.plugin.tools.ToastUtil;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;

import static com.inso.plugin.tools.Constants.OFF;
import static com.inso.plugin.tools.Constants.ON;


/**
 * @ 创建者:   CoderChen
 * @ 时间:     2018/3/20
 * @ 描述:
 */


public class LowPowerManager {

    private static volatile LowPowerManager mInstance;
    public static final String CLOSE_VIBRATE_AUTO_SWITCH_STATE = "CLOSE_VIBRATE_AUTO_SWITCH_STATE";
    public static final String TIP_ONLY_ONCE_CLICKED = "TIP_ONLY_ONCE_CLICKED";
    public static final String SP_DB_IGNORE_LOW_POWER_TIP = "SP_DB_IGNORE_LOW_POWER_TIP";
    private boolean isLowPower = false;

    public boolean isLowPower() {
        return isLowPower;
    }

    public void setLowPower(boolean lowPower) {
        isLowPower = lowPower;
    }

    private LowPowerManager() {
    }

    public static LowPowerManager getInstance() {
        if (mInstance == null) {
            synchronized (LowPowerManager.class) {
                if (mInstance == null) {
                    mInstance = new LowPowerManager();
                }
            }
        }
        return mInstance;
    }


    public void showCloseVibrateTip(final Context c, final IFlowControl control) {
        try {
            if(TextUtils.equals((String) SPManager.get(c, SP_DB_IGNORE_LOW_POWER_TIP, OFF), OFF)) {
                new MLAlertDialog.Builder(c)
                        .setMessage(c.getString(R.string.low_power_msg))
                        .setCancelable(false)
                        .setPositiveButton(c.getString(R.string.close_vibration), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                control.clearAllTips();//同意关闭振动,就直接清除数据
                            }
                        })
                        .setNegativeButton(c.getString(R.string.ignore), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SPManager.put(c, SP_DB_IGNORE_LOW_POWER_TIP, ON);
                            }
                        })
                        .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void showRejectToast(Context c) {
        ToastUtil.showToastNoRepeat(c, c.getString(R.string.reject_switch));
    }


    /**
     * 自动关闭开关是否是打开的状态
     */
    interface IAutoCloseSwitch {
        void on();

        void off();
    }

    public interface ILowPowerHandle {
        void handle();
    }


    public void handleBottomLowPower(Context context, ILowPowerHandle handle) {
        L.e("handleBottomLowPower isLowPower:" + isLowPower);
        if (isLowPower) {
            showRejectToast(context);
        } else {
            handle.handle();
        }
    }


    /**
     * 默认自动关闭振动开关是打开的
     * @param c
     * @return
     */
    public boolean getAutoSwitchState(Context c) {
        return (Boolean) SPManager.get(c, CLOSE_VIBRATE_AUTO_SWITCH_STATE, true);
    }

    public void setAutoSwitchState(Context c, boolean checked) {
        SPManager.put(c, CLOSE_VIBRATE_AUTO_SWITCH_STATE, checked);
    }

    /**
     * 仅仅提示一次
     */
    public void tipOnlyOnce(final Context c){
        try {
            if(!(Boolean) SPManager.get(c, TIP_ONLY_ONCE_CLICKED, false)){
                new MLAlertDialog.Builder(c)
                        .setMessage(c.getString(R.string.tip_only_once))
                        .setCancelable(false)
                        .setPositiveButton(c.getString(R.string.connect_know), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SPManager.put(c, TIP_ONLY_ONCE_CLICKED, true);
                            }
                        })
                        .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startFlowControl(Context c,boolean haveData,IFlowControl control){
        if(isLowPower){
            if(haveData){//振动提醒打开
                if(!getAutoSwitchState(c)){//未开启自动关闭振动开关
                    showCloseVibrateTip(c,control);
                }else{//开启开关就直接全部清掉数据
                    control.clearAllTips();
                }
            }else{
                //没有要振动的数据
                control.clearAllTips();
            }
        }else {//正常不是低电情况下
            control.startSync();
        }
    }


    interface IFlowControl{
        void  clearAllTips();
        void  startSync();
    }





}
