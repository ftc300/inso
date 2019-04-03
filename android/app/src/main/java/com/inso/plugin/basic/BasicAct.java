package com.inso.plugin.basic;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.inso.R;
import com.inso.plugin.manager.SPManager;
import com.inso.plugin.provider.DBHelper;
import com.inso.plugin.tools.Rom;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

import static com.inso.plugin.tools.Constants.SystemConstant.EXTRAS_EVENT_BUS;
import static com.inso.plugin.tools.Constants.SystemConstant.SP_ARG_MAC;

/**
 * Created by chendong on 2017/2/4.
 * activity基类
 */
public class BasicAct extends AppCompatActivity {

    protected View mTitleView, mContentView;
    protected FrameLayout flTitle, flContent, flSelectAll;
    protected LinearLayout llBasic;
    protected String MAC;
    protected String MODEL;
    protected String DID;
    protected String UID;
    protected Context mContext;
    protected boolean needPush = false;//是否需要上传至米家服务器 操作后设置为true
    protected DBHelper mDBHelper;
    private List<BasicAct> mListAct = new LinkedList<>();
    protected boolean mBackFlag = false;
    private boolean hasReceiveDisconnect = false;
    protected boolean conHasResp = false;


    //直接跳转
    protected void switchTo(Class<?> to) {
        Intent intent = new Intent(this,to);
        startActivity(intent);
    }

    //带任意参数的跳转
    protected void switchTo(Class<?> to, Map<String, Object> extras) {
        Intent i = new Intent(this,to);
        putExtras(extras, i);
        startActivity(i);
    }

    //EvebtBus的跳转
    protected void switchToWithEventBus(Class<?> to) {
        Intent i = new Intent(this,to);
        Map<String, Object> map = new HashMap<>();
        map.put(EXTRAS_EVENT_BUS, true);
        putExtras(map, i);
        startActivity(i);
    }

    /**
     * intent 中 传递数据
     *
     * @param extras
     * @param i
     */
    protected static void putExtras(Map<String, Object> extras, Intent i) {
        if (extras != null) {
            for (String name : extras.keySet()) {
                Object obj = extras.get(name);
                if (obj instanceof String) {
                    i.putExtra(name, (String) obj);
                }
                if (obj instanceof Integer) {
                    i.putExtra(name, (Integer) obj);
                }
                if (obj instanceof String[]) {
                    i.putExtra(name, (String[]) obj);
                }
                if (obj instanceof Boolean) {
                    i.putExtra(name, (Boolean) obj);
                }
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_activity_base);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext = this;
        mDBHelper = new DBHelper(mContext);
        flTitle = (FrameLayout) findViewById(R.id.act_base_title);
        flContent = (FrameLayout) findViewById(R.id.act_base_content);
        flSelectAll = (FrameLayout) findViewById(R.id.select_all_title_bar);
        llBasic = (LinearLayout) findViewById(R.id.act_base);
        //mHostActivity.setTitleBarPadding(flTitle);
        MAC = (String)SPManager.get(mContext,SP_ARG_MAC,"");
        setActStyle(getActStyle());
        //注册EventBus
        if (getIntent().getBooleanExtra(EXTRAS_EVENT_BUS, false))
            EventBus.getDefault().register(this);
        //渲染页面
        if (!isNeedTitle()) {
            flTitle.setVisibility(View.GONE);
        } else if (mTitleView == null) {
            final int contentRes = getTitleRes();
            if (contentRes > 0) {
                mTitleView = View.inflate(BasicAct.this, contentRes, null);
                if (mTitleView != null)
                    flTitle.addView(mTitleView);
            }
        }
        if (mContentView == null) {
            final int contentRes = getContentRes();
            if (contentRes > 0) {
                mContentView = View.inflate(BasicAct.this, contentRes, null);
                if (mContentView != null)
                    flContent.addView(mContentView);
            }
        }
        ButterKnife.bind(this);
        preInitViewData();
        initViewOrData();
    }


    /**
     * 存储DeviceStat信息，方便其他页面调用
     */
    protected void storeDeviceStat(){

    }

    /**
     * 默认需要重新连接
     *
     * @return
     */
    protected boolean needReconnect() {
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        mDBHelper.close();
    }



    /**
     * 默认显示title
     *
     * @return
     */
    protected boolean isNeedTitle() {
        return true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {//防止异常导致unregister抛异常
            if (getIntent().getBooleanExtra(EXTRAS_EVENT_BUS, false))
                EventBus.getDefault().unregister(this);//取消注册
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void setTitleText(String title) {
        if (getTitleRes() == R.layout.std_titlebar_device_color
                || getTitleRes() == R.layout.std_titlebar_device_color_two
                || getTitleRes() == R.layout.std_titlebar_device_white
                || getTitleRes() == R.layout.std_titlebar_device_white_two
                || getTitleRes() == R.layout.title_bar_black
                || getTitleRes() == R.layout.title_bar_selectall
                || getTitleRes() == R.layout.title_bar_transparent_black
                || getTitleRes() == R.layout.title_bar_transparent_white
                || getTitleRes() == R.layout.watch_title_bar_transparent_black
                || getTitleRes() == R.layout.watch_title_bar_transparent_white)
            ((TextView) findViewById(R.id.title_bar_title)).setText(title);
    }

    protected void setTitleText(String title, String subTitile) {
        if (getTitleRes() == R.layout.std_titlebar_device_color
                || getTitleRes() == R.layout.std_titlebar_device_color_two
                || getTitleRes() == R.layout.std_titlebar_device_white
                || getTitleRes() == R.layout.std_titlebar_device_white_two
                || getTitleRes() == R.layout.title_bar_black
                || getTitleRes() == R.layout.title_bar_transparent_black
                || getTitleRes() == R.layout.watch_title_bar_transparent_black
                || getTitleRes() == R.layout.title_bar_transparent_white
                || getTitleRes() == R.layout.watch_title_bar_transparent_white) {
            ((TextView) findViewById(R.id.title_bar_title)).setText(title);
            ((TextView) findViewById(R.id.sub_title_bar_title)).setText(title);
            ((TextView) findViewById(R.id.sub_title_bar_title)).setVisibility(View.VISIBLE);
        }
    }

    protected void setSubTitleText(String subTitile) {
        if (getTitleRes() == R.layout.std_titlebar_device_color
                || getTitleRes() == R.layout.std_titlebar_device_color_two
                || getTitleRes() == R.layout.std_titlebar_device_white
                || getTitleRes() == R.layout.std_titlebar_device_white_two
                || getTitleRes() == R.layout.title_bar_black
                || getTitleRes() == R.layout.title_bar_transparent_black
                || getTitleRes() == R.layout.watch_title_bar_transparent_black
                || getTitleRes() == R.layout.title_bar_transparent_white
                || getTitleRes() == R.layout.watch_title_bar_transparent_white) {
            ((TextView) findViewById(R.id.sub_title_bar_title)).setText(subTitile);
            ((TextView) findViewById(R.id.sub_title_bar_title)).setVisibility(View.VISIBLE);
        }
    }

    protected void setBtnOnBackPress() {
        findViewById(R.id.title_bar_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackFlag = true;
                finish();
            }
        });
    }

    protected void setBtnOnBackPress(int id) {
        findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackFlag = true;
                finish();
            }
        });
    }


    /**
     * 默認不需要加載動畫
     *
     * @return
     */
    protected String getLoadingTip() {
        return null;
    }



    /**
     * 获取Content需要显示的View的资源文件
     *
     * @return
     */
    protected int getContentRes() {
        return 0;
    }

    /**
     * 获取Title需要显示的View的资源文件
     *
     * @return
     */
    protected int getTitleRes() {
        return R.layout.watch_title_bar_transparent_white;
    }

    /**
     */
    protected void initViewOrData() {

    }

    protected void preInitViewData() {

    }

    /**
     * 默认返回BW
     *
     * @return
     */
    protected ActStyle getActStyle() {
        return ActStyle.BW;
    }

    /**
     * 设置样式
     * https://github.com/msdx/status-bar-compat
     * StatusBarCompat是一个用于设置系统状态栏颜色的兼容库，兼容Android 4.4.2(HttpAPI 19)以上，使用简单，仅需要一行代码的调用。
     * 第三方ROM适配支持
     * ROM	适配说明
     * MIUI	调用小米的API适配
     * Flyme	调用魅族API适配，并且增加不主动设置可能不兼容的Flyme的状态栏的API
     * EMUI3.1	对于6.0以下5.0及其以上的EMUI，使用4.4.2的方式来处理（EMUI3.1无法使用5.0API设置）
     */
    protected void setActStyle(ActStyle style) {
        switch (style) {
            case BW:
                llBasic.setBackgroundColor(ContextCompat.getColor(mContext, R.color.main_bg));
                flContent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.main_bg));
                break;
            case WT:
                llBasic.setBackgroundColor(ContextCompat.getColor(mContext, Rom.isMIUI() ? R.color.class_F : R.color.white));
                ((ImageView) findViewById(R.id.title_bar_return)).setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.std_tittlebar_main_device_back));
                ((TextView) findViewById(R.id.title_bar_title)).setTextColor(ContextCompat.getColor(mContext, R.color.std_word_001));
                findViewById(R.id.divider_line).setVisibility(View.VISIBLE);
                StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,Rom.isMIUI() ? R.color.class_F : R.color.white));
                break;
            case DFU:
                llBasic.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                ((TextView) findViewById(R.id.title_bar_title)).setTextColor(ContextCompat.getColor(mContext, R.color.std_word_001));
                findViewById(R.id.divider_line).setVisibility(View.VISIBLE);
                StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.watch_white));
                break;
            case BT:
                llBasic.setBackgroundColor(ContextCompat.getColor(mContext, R.color.mi_title_color));
                StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.mi_title_color));
                break;
            case GT:
                llBasic.setBackgroundColor(ContextCompat.getColor(mContext, R.color.class_F));
                StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.class_F));
                break;
            case DT:
                llBasic.setBackgroundColor(ContextCompat.getColor(mContext, R.color.watch_gray));
                StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.watch_gray));
                break;
        }
    }


    public enum ActStyle {
        BW,//黑底 内容白色
        WT,//白底
        BT,//黑底
        GT,//灰色
        DT,
        DFU
    }

    @SuppressWarnings("unchecked")
    public final <E extends View> E getView(int id) {
        try {
            return (E) findViewById(id);
        } catch (ClassCastException ex) {
            throw ex;
        }
    }

    @Override
    public void onBackPressed() {
        if (isNeedBackPress()) {
            super.onBackPressed();
            mBackFlag = true;
        }
    }

    /**
     * hook是否屏蔽返回按钮
     *
     * @return
     */
    protected boolean isNeedBackPress() {
        return true;
    }
}
