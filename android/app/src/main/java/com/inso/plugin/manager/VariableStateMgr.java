package com.inso.plugin.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import static com.inso.plugin.tools.Constants.SystemConstant.SP_MAC_UPLOADED_MAC;


/**
 * Comment: 管理变量的状态 并根据不同状态实现各功能
 * Author: ftc300
 * Date: 2018/9/17
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class VariableStateMgr {

    public static  abstract class BaseAction{
        abstract void doActionThing();
    }

    public interface IActionByState {
        void onPositive() ;

        void onNegative();
    }

    private VariableStateMgr() {
    }

    public static VariableStateMgr getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final VariableStateMgr INSTANCE = new VariableStateMgr();
    }

    private static boolean macUploadedState(Context context,String mac) {
        return TextUtils.equals((String) SPManager.get(context, SP_MAC_UPLOADED_MAC, ""),mac);
    }

    public void setMacUploaded(Context context,String mac) {
        SPManager.put(context, SP_MAC_UPLOADED_MAC, mac);
    }

    public static class MACAction extends  BaseAction{
        IActionByState actionByState;
        Context context;
        String mac;

        public MACAction(Context context,String mac, @NonNull IActionByState actionByState) {
            this.actionByState = actionByState;
            this.context = context;
            this.mac = mac;
        }

        @Override
        public void doActionThing() {
            if(actionByState!=null){
                if(macUploadedState(context,mac)){
                    actionByState.onPositive();
                }else {
                    actionByState.onNegative();
                }
            }
        }
    }

}

