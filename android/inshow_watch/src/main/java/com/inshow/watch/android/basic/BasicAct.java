package com.inshow.watch.android.basic;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.inshow.watch.android.R;
import com.inshow.watch.android.WatchBleReceiver;
import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.manager.SPManager;
import com.inshow.watch.android.manager.UIManager;
import com.inshow.watch.android.model.BluetoothDeviceInfo;
import com.inshow.watch.android.provider.DBHelper;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.Rom;
import com.xiaomi.smarthome.bluetooth.Response;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;
import com.xiaomi.smarthome.common.plug.utils.TitleBarUtil;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;
import com.xiaomi.smarthome.common.ui.dialog.XQProgressDialogSimple;
import com.xiaomi.smarthome.device.api.XmPluginBaseActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.inshow.watch.android.tools.Constants.SystemConstant.EXTRAS_DEVICE_MAC;
import static com.inshow.watch.android.tools.Constants.SystemConstant.EXTRAS_DEVICE_NAME;
import static com.inshow.watch.android.tools.Constants.SystemConstant.EXTRAS_EVENT_BUS;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_BLUETOOTH_CONNECTED;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_DID;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_MAC;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_MODEL;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_USERID;
import static com.xiaomi.smarthome.bluetooth.XmBluetoothManager.Code.REQUEST_SUCCESS;

/**
 * Created by chendong on 2017/2/4.
 * activity基类
 */
public class BasicAct extends FragmentActivity {

    protected View mTitleView, mContentView;
    protected FrameLayout flTitle, flContent, flSelectAll;
    protected XQProgressDialogSimple mDialog;
    protected LinearLayout llBasic;
    protected String MAC;
    protected String MODEL;
    protected String DID;
    protected String UID;
    protected Context mContext;
    protected boolean needPush = false;//是否需要上传至米家服务器 操作后设置为true
    protected DBHelper mDBHelper;
    protected HttpSyncHelper mSyncHelper;
    private List<BasicAct> mListAct = new LinkedList<>();
    protected boolean mBackFlag = false;
    private boolean hasReceiveDisconnect = false;
    protected boolean conHasResp = false;

    //    protected Unbinder mBinder;
    //BDI == BluetoothDeviceInfo
    protected Map<String, Object> configBDIExtras(BluetoothDeviceInfo entity) {
        Map<String, Object> map = new HashMap<>();
        map.put(EXTRAS_DEVICE_NAME, entity.deviceName);
        map.put(EXTRAS_DEVICE_MAC, entity.deviceMac);
        return map;
    }

    //直接跳转
    @SuppressLint("NewApi")
    protected void switchTo(Class<?> to) {
        Intent intent = new Intent(this,to);
        startActivity(intent, null);
    }

    //带任意参数的跳转
    protected void switchTo(Class<?> to, Map<String, Object> extras) {
        Intent i = new Intent(this,to);
        putExtras(extras, i);
        startActivity(i, to.getName());
    }

    //EvebtBus的跳转
    protected void switchToWithEventBus(Class<?> to) {
        Intent i = new Intent(this,to);
        Map<String, Object> map = new HashMap<>();
        map.put(EXTRAS_EVENT_BUS, true);
        putExtras(map, i);
        startActivity(i, to.getName());
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

    protected WatchBleReceiver mReceiver = new WatchBleReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TitleBarUtil.enableTranslucentStatus(getWindow());
        setContentView(R.layout.watch_activity_base);
        mContext = this;
        mDBHelper = new DBHelper(mContext);
        mSyncHelper = new HttpSyncHelper(mDBHelper);
        AppController.getInstance().addActivity(this);
        storeDeviceStat();
        flTitle = (FrameLayout) findViewById(R.id.act_base_title);
        flContent = (FrameLayout) findViewById(R.id.act_base_content);
        flSelectAll = (FrameLayout) findViewById(R.id.select_all_title_bar);
        llBasic = (LinearLayout) findViewById(R.id.act_base);
        //mHostActivity.setTitleBarPadding(flTitle);
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
        preInitViewData();
        MAC = mDBHelper.getCache(SP_ARG_MAC);
        MODEL = mDBHelper.getCache(SP_ARG_MODEL);
        UID = mDBHelper.getCache(SP_ARG_USERID);
        DID = mDBHelper.getCache(SP_ARG_DID);
        mReceiver.setBleStateChangedListener(new onBleStateChangedListener() {
            @Override
            public void onBleTurnOff() {
                try {
                    if ((boolean) SPManager.get(mContext, SP_ARG_BLUETOOTH_CONNECTED, false))
                        showBleConBrokenDialog();
                }catch (Exception e){
                     e.printStackTrace();
                     finish();
                }
            }

            @Override
            public void onDisconnect() {
                L.e("ConnectChanged onDisconnect");
                if ((boolean) SPManager.get(mContext, SP_ARG_BLUETOOTH_CONNECTED, false) && !hasReceiveDisconnect) {
                    hasReceiveDisconnect = true;
                    if (needReconnect() && XmBluetoothManager.getInstance().isBluetoothOpen()) {
                        L.e("BasicAct:connect silence");
                        XmBluetoothManager.getInstance().removeToken(MAC);
                        XmBluetoothManager.getInstance().secureConnect(MAC, new Response.BleConnectResponse() {
                            @Override
                            public void onResponse(int code, Bundle data) {
                                if (code == REQUEST_SUCCESS) {
                                    L.e("BasicAct:connect silence success");
                                    hasReceiveDisconnect = false;
                                }
//                                else if (code == REQUEST_FAILED) {
//                                    L.e("BasicAct:connect silence fail");
//                                    showBleConBrokenDialog();
//                                }
                                else {
                                    L.e("BasicAct:connect silence fail return code:" + code);
                                    showBleConBrokenDialog();
                                }
                            }
                        });
                    } else {
                        L.e("BasicAct:showBleConBrokenDialog()");
                        showBleConBrokenDialog();
                    }
                }
            }
        });
        initViewOrData();
        //加载页面
        if (!TextUtils.isEmpty(getLoadingTip())) {
            mDialog = UIManager.getXQProgressDialogSimple(BasicAct.this, getLoadingTip());
//            flContent.setVisibility(View.INVISIBLE);
            mDialog.show();
        }
    }

    private void showBleConBrokenDialog() {
        try {
            new MLAlertDialog.Builder(mContext)
                    .setCancelable(false)
                    .setMessage(getString(R.string.ble_connect_off))
                    .setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            BasicAct.this.finish();
                            AppController.getInstance().exit();
                        }
                    })
                    .show();
        }catch (Exception e){
            e.printStackTrace();
        }
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
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(XmBluetoothManager.ACTION_CONNECT_STATUS_CHANGED);
        filter.addAction(XmBluetoothManager.ACTION_CHARACTER_CHANGED);
        filter.addAction(XmBluetoothManager.ACTION_RENAME_NOTIFY);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {//防止异常导致unregister抛异常
            if (mReceiver != null) unregisterReceiver(mReceiver);
            AppController.getInstance().removeActivity(this);
            if (getIntent().getBooleanExtra(EXTRAS_EVENT_BUS, false))
                EventBus.getDefault().unregister(this);//取消注册
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void cancelLoadingDialog() {
        if (mDialog != null) {
            flContent.setVisibility(View.VISIBLE);
            mDialog.cancel();
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

    protected void dismissLoading() {
        flContent.setVisibility(View.VISIBLE);
        mDialog.dismiss();
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
     * StatusBarCompat是一个用于设置系统状态栏颜色的兼容库，兼容Android 4.4.2(API 19)以上，使用简单，仅需要一行代码的调用。
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
                ////mHostActivity.enableWhiteTranslucentStatus();
                if (Rom.isEMUI()) {
                    StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.main_bg));
                }
                break;
            case WT:
                llBasic.setBackgroundColor(ContextCompat.getColor(mContext, Rom.isMIUI() ? R.color.class_F : R.color.white));
                ((ImageView) findViewById(R.id.title_bar_return)).setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.std_tittlebar_main_device_back));
                ((TextView) findViewById(R.id.title_bar_title)).setTextColor(ContextCompat.getColor(mContext, R.color.std_word_001));
                findViewById(R.id.divider_line).setVisibility(View.VISIBLE);
                //mHostActivity.enableBlackTranslucentStatus();
                if (Rom.isEMUI()) {
                    StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.watch_white));
                }
                break;
            case DFU:
                llBasic.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
                ((TextView) findViewById(R.id.title_bar_title)).setTextColor(ContextCompat.getColor(mContext, R.color.std_word_001));
                findViewById(R.id.divider_line).setVisibility(View.VISIBLE);
                //mHostActivity.enableBlackTranslucentStatus();
                if (Rom.isEMUI()) {
                    StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.watch_white));
                }
                break;
            case BT:
                llBasic.setBackgroundColor(ContextCompat.getColor(mContext, R.color.mi_title_color));
                //mHostActivity.enableWhiteTranslucentStatus();
                if (Rom.isEMUI()) {
                    L.e("isEMUI");
                    StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.mi_title_color));
                }
                break;
            case GT:
                llBasic.setBackgroundColor(ContextCompat.getColor(mContext, R.color.class_F));
                //mHostActivity.enableBlackTranslucentStatus();
                if (Rom.isEMUI()) {
                    StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.class_F));
                }
                break;
            case DT:
                llBasic.setBackgroundColor(ContextCompat.getColor(mContext, R.color.watch_gray));
                //mHostActivity.enableBlackTranslucentStatus();
                if (Rom.isEMUI()) {
                    StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.watch_gray));
                }
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
