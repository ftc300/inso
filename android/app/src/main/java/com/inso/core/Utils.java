package com.inso.core;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.inso.R;
import com.inso.core.transformation.CropCircleTransformation;
import com.inso.plugin.act.mainpagelogic.PluginMainAct;
import com.squareup.picasso.Picasso;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.List;

import static android.content.Context.SHORTCUT_SERVICE;
import static com.inso.plugin.tools.Constants.SystemConstant.EXTRAS_EVENT_BUS;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/3/18
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class Utils {


    /**
     * 工具方法
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        return dm.widthPixels;
    }


    /**
     * sp或者 dp 装换为 px
     */
    public static int dpToPx(Context context, int dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dpValue * scale);
    }


    //添加快捷方式
    public static void addShortcut(final Context context,final String shortLabel) {
            AndPermission.with(context)
                    .runtime()
                    .permission(Manifest.permission.INSTALL_SHORTCUT)
                    .onGranted(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                            handleShortcut(context,shortLabel);
                        }
                    })
                    .onDenied(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                        }
                    })
                    .start();
    }

    public    static void handleShortcut(final Context context,final String shortLabel){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShortcutManager scm = (ShortcutManager) context.getSystemService(SHORTCUT_SERVICE);
            Intent launcherIntent = new Intent(Intent.ACTION_MAIN, Uri.EMPTY, context, PluginMainAct.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            launcherIntent.putExtra(EXTRAS_EVENT_BUS, true);
            ShortcutInfo si = new ShortcutInfo.Builder(context, "addShortcut")
                    .setIcon(Icon.createWithResource(context, R.drawable.watch2_ic))
                    .setShortLabel(shortLabel)
                    .setIntent(launcherIntent)
                    .build();
            assert scm != null;
            scm.requestPinShortcut(si, null);
        } else {
            Intent addShortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");//"com.android.launcher.action.INSTALL_SHORTCUT"
            addShortcutIntent.putExtra("duplicate", false);// 经测试不是根据快捷方式的名字判断重复的
            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortLabel);
            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(context.getApplicationContext(), R.drawable.watch2_ic));
            Intent launcherIntent = new Intent(context.getApplicationContext(), PluginMainAct.class);
            launcherIntent.putExtra(EXTRAS_EVENT_BUS, true);
            addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);
            context.sendBroadcast(addShortcutIntent);
        }
    }

    public static void showWebIcon(String url, View imgView,int defaultIcon) {
        if (!TextUtils.isEmpty(url)) {
            Picasso.get()
                    .load(url)
                    .placeholder(defaultIcon)
                    .fit()
                    .centerCrop()
                    .into((ImageView) imgView);
        }
    }
    public static void showCirecleWebIcon(String url, View imgView,int defaultIcon) {
        if (!TextUtils.isEmpty(url)) {
            Picasso.get()
                    .load(url)
                    .placeholder(defaultIcon)
                    .transform(new CropCircleTransformation())
                    .into((ImageView) imgView);
        }
    }

}
