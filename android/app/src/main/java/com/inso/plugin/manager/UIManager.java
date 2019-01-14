package com.inso.plugin.manager;

import android.content.Context;
import android.content.DialogInterface;

import com.inso.R;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;
import com.xiaomi.smarthome.common.ui.dialog.XQProgressDialogSimple;

/**
 * Created by chendong on 2017/2/9.
 * 负责页面的跳转&&弹出的Dialog&&加载动画 etc
 */
public class UIManager {

    private static UIManager instance;

    public UIManager() {}

    /**
     * 单例
     */
    public static UIManager getInstance() {
        if (instance == null) {
            instance = new UIManager();
        }
        return instance;
    }


    public static XQProgressDialogSimple getXQProgressDialogSimple(Context context,String msg)
    {
        XQProgressDialogSimple dialog;
        dialog = new XQProgressDialogSimple(context);
        dialog.setMessage(msg);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        return  dialog;
    }

    public static void showAlertDialog(Context context,String msg){
        new MLAlertDialog.Builder(context)
                .setCancelable(false)
                .setMessage(msg)
                .setPositiveButton(context.getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }



}





















