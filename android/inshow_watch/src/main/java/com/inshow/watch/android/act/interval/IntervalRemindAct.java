package com.inshow.watch.android.act.interval;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.inshow.watch.android.R;
import com.inshow.watch.android.act.mainpagelogic.LowPowerManager;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.dao.IntervalDao;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.sync.http.bean.HttpInterval;
import com.inshow.watch.android.tools.Constants;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.TimeUtil;
import com.inshow.watch.android.view.CircleProgressView;
import com.inshow.watch.android.view.GifView;
import com.xiaomi.smarthome.device.api.Callback;

import org.json.JSONArray;

import java.util.Timer;
import java.util.TimerTask;

import static com.inshow.watch.android.act.interval.IntervalHelper.getCalcuOriginRemain;
import static com.inshow.watch.android.act.interval.IntervalHelper.isIntegral;
import static com.inshow.watch.android.tools.Constants.OFF;
import static com.inshow.watch.android.tools.Constants.ON;
import static com.inshow.watch.android.tools.Constants.TimeStamp.INTERVAL_ALARM_KEY;

/**
 * Created by chendong on 2017/1/22.
 * 间隔提醒 必须同步时间
 * <p>
 * 2、计算整点时分剩余时间
 */
public class IntervalRemindAct extends BasicAct {

    private CheckBox switchButton;
    private CircleProgressView circleProgressView;
    private Button btnSetInterval, btnReset;
    private int interval;//unit:s 当前设置的间隔时长
    private int timerRemain;//当前剩余的时间时长
    private GifView gifView;
    private Timer timer;
    private TextView tvRemain, tvTopTip, tvCenterTip;
    private FrameLayout flCir;
    private IntervalDao originDao;
    private boolean mSwitchStatus;
    private IntervalHelper intervalHelper;

    private ISaveOperation saveOperation = new ISaveOperation() {

        @Override
        public IntervalDao getItem() {
            return new IntervalDao(getInterval(), getStartTime(), mSwitchStatus ? ON : OFF);
        }

        @Override
        public void saveData() {
            mDBHelper.updateInterval(getItem());
        }

        @Override
        public int getInterval() {
            return interval;
        }

        @Override
        public int getStartTime() {
            return TimeUtil.getNowTimeSeconds();
        }

    };

    @Override
    protected int getTitleRes() {
        return R.layout.watch_title_transparent_white_remind;
    }

    @Override
    protected int getContentRes() {
        return R.layout.watch_activity_interval_remind;
    }

    @Override
    protected void initViewOrData() {
        setBtnOnBackPress();
        bindView();
        initData();
        btnSetInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intervalHelper = new IntervalHelper(mContext, MAC, saveOperation);
                intervalHelper.showSetIntervalDialog(false, interval, new IntervalHelper.IDialogClick() {
                    @Override
                    public void OK(int pickInterval) {
                        needPush = true;
                        interval = pickInterval;
                        timerRemain = isIntegral(pickInterval) ? TimeUtil.getIntegralDeltaTime(mDBHelper) : pickInterval;
                        intervalHelper.setWatchInterval();
                        renderUIByCheckState(true);
                    }

                    @Override
                    public void Cancel() {

                    }
                });
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                needPush = true;
                timerRemain = interval;
                intervalHelper = new IntervalHelper(mContext, MAC, saveOperation);
                intervalHelper.resetWatchInterval();
                renderUIByCheckState(true);
            }

        });

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                needPush = true;
                mSwitchStatus = switchButton.isChecked();
                intervalHelper = new IntervalHelper(mContext, MAC, saveOperation);
                if (!mSwitchStatus) {//closed
                    intervalHelper.closeWatchInterval();
                    renderUIByCheckState(false);
                    if (timer != null) {//clear timer
                        timer.cancel();
                        timer = null;
                    }
                    return;
                }
                //open
                intervalHelper.showSetIntervalDialog(true, interval, new IntervalHelper.IDialogClick() {
                    @Override
                    public void OK(int pickInterval) {
                        interval = pickInterval;
                        timerRemain = isIntegral(pickInterval) ? TimeUtil.getIntegralDeltaTime(mDBHelper) : pickInterval;
                        startTimer();
                        intervalHelper.setWatchInterval();
                        renderUIByCheckState(mSwitchStatus);
                        LowPowerManager.getInstance().tipOnlyOnce(mContext);
                    }

                    @Override
                    public void Cancel() {
                        mSwitchStatus = false;
                        switchButton.setChecked(false);
                    }
                });

            }
        });

    }

    /**
     * 还原原来的设置 渲染页面
     */
    private void initData() {
        L.d("initData ");
        originDao = mDBHelper.getInterval();
        L.d(originDao.toString());
        interval = originDao.time;
        timerRemain = originDao.time;
        mSwitchStatus = ON.equals(originDao.status);
        switchButton.setChecked(mSwitchStatus);
        if (mSwitchStatus) {
            L.d("initData getCalcuOriginRemain");
            timerRemain = getCalcuOriginRemain(mDBHelper,originDao);
            startTimer();
        }
        renderUIByCheckState(mSwitchStatus);
    }



    /**
     * 控件绑定和初始值
     */
    private void bindView() {
        circleProgressView = (CircleProgressView) findViewById(R.id.circleProgressView);
        tvRemain = (TextView) findViewById(R.id.tvRemain);
        tvTopTip = (TextView) findViewById(R.id.tvRemindTopTip);
        tvCenterTip = (TextView) findViewById(R.id.tvRemindCenterTip);
        switchButton = (CheckBox) findViewById(R.id.switchButton);
        btnSetInterval = (Button) findViewById(R.id.setIntervalTime);
        btnReset = (Button) findViewById(R.id.reset);
        gifView = (GifView) findViewById(R.id.gifview);
        flCir = (FrameLayout) findViewById(R.id.flCir);
        tvRemain.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/MIUI_EX_Light.ttf"));
        ((TextView) mTitleView.findViewById(R.id.title_bar_title)).setText(getString(R.string.interval_remind));
    }


    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (switchButton.isChecked()) {
                            if (timerRemain > 1) {
                                timerRemain -= 1;
                            } else {
                                circleProgressView.setVisibility(View.GONE);
                                gifView.setVisibility(View.VISIBLE);
                                gifView.play();
                                gifView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        gifView.pause();
                                        gifView.setVisibility(View.GONE);
                                        circleProgressView.setVisibility(View.VISIBLE);
                                    }
                                }, 4000);
//                                timerRemain = interval;
                                timerRemain = isIntegral(interval) ? 3600: interval; // FIX BUG: 2018/1/2  整点gif后停留在59:59不动
                            }
                            renderUIByCheckState(true);
                        }
                    }
                });
            }
        }, 0, 1000);//render per 100 ms for 1 minute smoothly
    }


    /**
     * 根据打开情况渲染页面
     *
     * @param isCheck
     */
    private void renderUIByCheckState(boolean isCheck) {
        mSwitchStatus = isCheck;
        btnSetInterval.setEnabled(isCheck);
        btnReset.setEnabled(isCheck);
        circleProgressView.setMaxProgress(interval);
        circleProgressView.setProgress(isCheck ? timerRemain : 0);
        tvRemain.setTextColor(ContextCompat.getColor(mContext, R.color.primaryColor));
        tvRemain.setText(TimeUtil.getRemainDigitalNum(timerRemain));
        tvTopTip.setText(isIntegral(interval) ? getResources().getString(R.string.interval_integral_tip) : String.format(getResources().getString(interval / 60 == 1? R.string.interval_tip_one : R.string.interval_tip), interval / 60));
        tvCenterTip.setText(getResources().getString(R.string.interval_off));
        btnReset.setEnabled(!isIntegral(interval));
        if (isCheck) {
            flCir.setVisibility(View.VISIBLE);
            tvTopTip.setVisibility(View.VISIBLE);
            btnSetInterval.setVisibility(View.VISIBLE);
            btnReset.setVisibility(View.VISIBLE);
            tvCenterTip.setVisibility(View.GONE);
        } else {
            flCir.setVisibility(View.GONE);
            tvTopTip.setVisibility(View.GONE);
            btnSetInterval.setVisibility(View.GONE);
            btnReset.setVisibility(View.INVISIBLE);
            tvCenterTip.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onPause() {
        if (needPush && mBackFlag) {
            mDBHelper.updateTimeStamp(INTERVAL_ALARM_KEY, TimeUtil.getNowTimeSeconds());
            IntervalDao dao = mDBHelper.getInterval();
            HttpInterval bean = new HttpInterval(dao.time, dao.start, dao.status);
            HttpSyncHelper.pushData(new RequestParams(
                    MODEL,
                    UID,
                    DID,
                    Constants.HttpConstant.TYPE_USER_INFO,
                    INTERVAL_ALARM_KEY,
                    new Gson().toJson(bean),
                    mSyncHelper.getLocalIntervalKeyTime()
            ), new Callback<JSONArray>() {
                @Override
                public void onSuccess(JSONArray jsonArray) {
                    L.e("setIntervalDataSuccess:" + jsonArray.toString());
                }

                @Override
                public void onFailure(int i, String s) {
                    L.e("setIntervalDataError:" + s);
                }
            });
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        super.onDestroy();
    }

}

