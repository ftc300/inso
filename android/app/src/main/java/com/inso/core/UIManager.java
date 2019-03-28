package com.inso.core;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.inso.R;

/**
 * @Title:
 * @Description:对话框样式
 * @Author:new7
 * @Since:2015-8-10
 * @Version:1.1.0
 */
public class UIManager {

    private static UIManager instance;

    private Dialog loadingDialog;

    private UIManager() { }

    /**
     * 单例模式
     * @return
     */
    public static UIManager getInstance() {
        if (instance == null) {
            instance = new UIManager();
        }
        return instance;
    }



    /**
     * action sheet dialog
     *
     * @param context
     * @param view
     * @return
     */

    public static Dialog getActionSheet(Context context, View view) {
        final Dialog dialog = new Dialog(context, R.style.action_sheet);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        view.setBackgroundResource(R.drawable.alertdialog_bg);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        int marigin = Utils.dpToPx(context,10);
        lp.x = marigin; // left margin
        lp.y = marigin; // bottom margin
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.action_sheet_animation); // 添加动画

        return dialog;
    }

}
