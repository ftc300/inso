package com.inso.watch.baselib.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.githang.statusbar.StatusBarCompat;
import com.inso.watch.baselib.R;


/**
 * 公用Activity，不包含任何业务，用于加载Fragment
 */
public class CommonAct extends BaseAct  {

    private static final String EXA_NAME = "name";
    private static final String EXA_ARGS = "args";
    protected Context mContext;
    private Fragment mFragment;
    /**
     * 启动公用的Activity
     * @param context 上下文对象
     * @param fragment Fragment
     */
    public static void start(Context context, Class<?> fragment) {
        start(context, fragment, null);
    }

    /**
     * 启动公用的Activity
     * @param from 源Fragment
     * @param fragment Fragment
     */
    public static void start4Result(Context context, Fragment from, Class<?> fragment) {
        start4Result(context, from, fragment, null);
    }

    /**
     * 启动公用的Activity
     * @param from 源Fragment
     * @param fragment Fragment
     */
    public static void start4Result(Context context, Fragment from, Class<?> fragment, Bundle args) {
        from.startActivityForResult(configIntent(context, fragment, args), 0);
    }

    /**
     * 启动公用的Activity
     * @param context 上下文对象
     * @param fragment Fragment
     * @param args Fragment参数
     */
    public static void start(Context context, Class<?> fragment, Bundle args) {
        context.startActivity(configIntent(context, fragment, args));
    }

    /**
     * 需要登录跳转
     * @param context
     * @param fragment
     * TODO：startNeedLogin
     */
    public static void startNeedLogin(Context context, Class<?> fragment) {
//        if (!ShoveUser.newInstance().isLogin()) {
//            Intent login = new Intent(Constant.ACTION_LOGIN);
//            login.setPackage(context.getPackageName());
//            context.startActivity(login);
//            return;
//        }
//        start(context, fragment);
    }


    public static Intent configIntent(Context context, Class<?> fragment) {
        return configIntent(context, fragment, null);
    }

    public static Intent configIntent(Context context, Class<?> fragment, Bundle args) {
        Intent intent = new Intent(context, CommonAct.class);
        intent.putExtra(EXA_NAME, fragment.getName());
        intent.putExtra(EXA_ARGS, args);
        return intent;
    }

    public static Intent configIntent(Context context, Class<?> fragment, long id) {
        Intent intent = new Intent(context, CommonAct.class);
        intent.putExtra(EXA_NAME, fragment.getName());
        Bundle args = new Bundle();
        intent.putExtra(EXA_ARGS, args);
        return intent;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //固定竖屏(禁止横屏)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.common_act_container);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white));
        String fragmentName = getIntent().getStringExtra(EXA_NAME);
        Bundle args = getIntent().getBundleExtra(EXA_ARGS);
        if (!TextUtils.isEmpty(fragmentName)) {
            mFragment = Fragment.instantiate(this, fragmentName, args);
            // 添加Fragment
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.container, mFragment,"parentfragment");
            ft.commit();
        } else {
            finish();
        }
    }

    public void onClick(View v) {
        if (mFragment != null && mFragment instanceof View.OnClickListener) {
            ((View.OnClickListener) mFragment).onClick(v);
        }
    }

}
