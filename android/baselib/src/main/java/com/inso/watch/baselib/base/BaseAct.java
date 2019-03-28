package com.inso.watch.baselib.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.inso.watch.baselib.hook.HookCore;
import com.inso.watch.baselib.hook.HookListenerContract;
import com.inso.watch.baselib.hook.ListenerManager;
import com.inso.watch.commonlib.utils.StatusBarCompatUtil;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/1/9
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class BaseAct extends AppCompatActivity{
    protected Context mContext;
    private static long LAST_TIME = 0;
    private static long MIN_INTERVAL_DEFAULT = 600;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        LAST_TIME = 0;
        ListenerManager.Builer builer = new ListenerManager.Builer();
        builer.buildOnClickListener(new HookListenerContract.OnClickListener() {
            @Override
            public void doInListener(View v) {
            }
        });
        HookCore.getInstance().startHook(this, ListenerManager.create(builer));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StatusBarCompatUtil.compat(this);

    }
}
