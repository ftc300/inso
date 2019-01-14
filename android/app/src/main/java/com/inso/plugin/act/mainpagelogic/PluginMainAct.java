package com.inso.plugin.act.mainpagelogic;

import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.inso.R;
import com.inso.plugin.basic.BasicAct;
import com.inso.plugin.basic.BasicFragment;
import com.inso.plugin.fragment.FragmentBottom;
import com.inso.plugin.fragment.FragmentTop;
import com.inso.plugin.manager.SPManager;
import com.inso.plugin.tools.L;
import com.inso.plugin.view.MainDragLayout;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static com.inso.plugin.tools.Constants.SystemConstant.SP_ARG_BLUETOOTH_CONNECTED;
import static com.inso.plugin.tools.Constants.SystemConstant.SP_ARG_DEVICE_NAME;

/**
 * Created by chendong on 2017/2/17.
 * 首页Act
 *
 * @author chendong
 */
public class PluginMainAct extends BasicAct {
    private BasicFragment firstF, secondF;
    private TextView title, subTitle;
    private ImageView barReturn, barMore;
    private int[] mPowerConsumption = new int[6];
    private int mBatteryLevel, mHaveUsedTime;
    private String[] menus;
    private String mCurrentV;
    private ImageView imgRedPoint;
    private boolean hasScanFound = false;
    private int openScanTryCount;
    private final int TRY_LIMIT = 2;
    private final int BATTERY_LIMIT = 30;
    private MainDragLayout dragLayout;
    private ScheduledExecutorService dfuPool;
    private String mDownLoadFilePath = "";
    private boolean batteryHasReaded = false;
    private boolean resumeFromOtherPage = false;
    private int SCAN_INTERVAL = 5 * 1000;
    private boolean isScanning = true; //正在扫描设备中
    private Timer timer;
    private boolean neverResponse = true;
    private final String DFU_ERROR_VERSION = "1.0.5_21";
    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private AtomicInteger nowAtomicPercent = new AtomicInteger(0);
    private AtomicInteger lastAtomicPercent = new AtomicInteger(0);
    private int index = 0;


    @Override
    protected int getTitleRes() {
        return R.layout.watch_title_bar_transparent_black;
    }

    @Override
    protected int getContentRes() {
        return R.layout.watch_act_main_new;
    }

    @Override
    protected void initViewOrData() {
        //性能问题 主线程中网络请求并且下载文件
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        timer = new Timer();
        barReturn = (ImageView) findViewById(R.id.title_bar_return);
        barReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackFlag = true;
            }
        });
        barMore = (ImageView) findViewById(R.id.title_bar_more);
        menus = getResources().getStringArray(R.array.menu_normal_array);
        barMore.setEnabled(false);
        title = (TextView) findViewById(R.id.title_bar_title);
        subTitle = (TextView) findViewById(R.id.sub_title_bar_title);
        dragLayout = (MainDragLayout) findViewById(R.id.dragLayout);
        imgRedPoint = (ImageView) findViewById(R.id.title_bar_redpoint);
        setTitleText(mDBHelper.getCacheWithDefault(SP_ARG_DEVICE_NAME, getString(R.string.A01)));
        setPageStyle();
        barMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                L.e("barMore set onClick listener.");
            }
        });
        firstF = FragmentTop.getInstance();
        secondF = FragmentBottom.getInstance(100);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.firstF, firstF)
                .add(R.id.secondF, secondF)
                .commitAllowingStateLoss();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                connectSuccessCallback();
            }
        },3000);
    }


    /**
     * 连接成功回调
     */
    private void connectSuccessCallback() {
        dragLayout.setAllowMove(true);
        barMore.setEnabled(true);
        if (firstF.isAdded()) {
            ((FragmentTop) firstF).renderSuccess();
        }
        if (secondF.isAdded()) {
            ((FragmentBottom) secondF).renderSuccess();
        }
    }

    /**
     * 连接失败
     */
    private void connectFailCallback() {
        SPManager.put(mContext, SP_ARG_BLUETOOTH_CONNECTED, false);
        ((FragmentTop) firstF).renderFail();
        ((FragmentBottom) secondF).renderFail();
    }


    @Override
    public void onPause() {
        super.onPause();
        resumeFromOtherPage = true;
    }

    /**
     * dfu set null
     */
    private void setOnClickListenersNull() {
        findViewById(R.id.sub_title_bar_title).setOnClickListener(null);
        findViewById(R.id.title_bar_more).setOnClickListener(null);
    }


    private void setPageStyle() {
        title.setTextColor(ContextCompat.getColor(mContext, R.color.white_90_transparent));
        subTitle.setTextColor(ContextCompat.getColor(mContext, R.color.white_50_transparent));
        barReturn.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.std_tittlebar_main_device_back_white));
        barMore.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.std_tittlebar_main_device_more_unable));
        llBasic.setBackgroundColor(ContextCompat.getColor(mContext, R.color.main_bg));
        flContent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.main_bg));
    }

    @Override
    public void onDestroy() {
//        if (isScanning) {
//            XmBluetoothManager.getInstance().stopScan();
//            isScanning = false;
//        }
//        if (isDeviceConnected()) {
//            XmBluetoothManager.getInstance().disconnect(MAC);
//        }
//        if (null != asyncHttpManager) {
//            asyncHttpManager.releaseHttpAsyncManager();
//        }
        if (null != timer) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        super.onDestroy();
    }

}
