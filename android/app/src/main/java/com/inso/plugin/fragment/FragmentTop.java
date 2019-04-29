
package com.inso.plugin.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inso.R;
import com.inso.core.BleMgr;
import com.inso.plugin.act.city.BleWorldCityHelper;
import com.inso.plugin.act.city.WorldTimeAct;
import com.inso.plugin.basic.BasicFragment;
import com.inso.plugin.dao.PreferCitiesDao;
import com.inso.plugin.event.ChangeUI;
import com.inso.plugin.event.HomePageBus;
import com.inso.plugin.manager.AppController;
import com.inso.plugin.sync.http.bean.HttpCityRes;
import com.inso.plugin.tools.Configuration;
import com.inso.plugin.tools.TextStyle;
import com.inso.plugin.tools.TimeUtil;
import com.inso.plugin.view.clock.CurrentTimeTv;
import com.inso.plugin.view.clock.DeviceStatus;
import com.inso.plugin.view.clock.MainClockView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.inso.plugin.event.ChangeUI.CONNECT_AGAIN;
import static com.inso.plugin.event.ChangeUI.CONNECT_DFU;
import static com.inso.plugin.event.ChangeUI.CONNECT_FAIL;
import static com.inso.plugin.event.ChangeUI.CONNECT_ING;
import static com.inso.plugin.event.ChangeUI.CONNECT_SUCCESS;
import static com.inso.plugin.event.ChangeUI.RENDER_AGAIN;
import static com.inso.plugin.manager.BleManager.I2B_SyncTime2;
import static com.inso.plugin.tools.Constants.ConfigVersion.WORLD_CITY;
import static com.inso.plugin.tools.Constants.SystemConstant.EXTRAS_EVENT_BUS;
import static com.inso.plugin.tools.Constants.TimeStamp.WORLD_CITY_KEY;


public class FragmentTop extends BasicFragment implements IRender {

    private HomePageBus bus;
    private MainClockView clockView;
    private int mCurrentIndex = 0;
    private TextView tvCity,tvConnecting, tvBleFail, tvNetFail;
    private List<PreferCitiesDao> mPreCities = new ArrayList<>();
    private int waitCounter;
    private String[] waitStrings;
    private CurrentTimeTv currentTv;
    private int mCurrentStatus;
    private TextStyle textStyle;
    private Activity activity;
    private String selectCity;
    private String strConnecting;
    private boolean reTry;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.watch_frg_first, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (isAdded() && null!=activity) {
           initDefaultView(view);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        clockView.setDrawFlag(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        clockView.setDrawFlag(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public static FragmentTop getInstance() {
        FragmentTop f = new FragmentTop();
        Bundle arg = new Bundle();
        arg.putBoolean(EXTRAS_EVENT_BUS, true);
        f.setArguments(arg);
        return f;
    }

    /**
     * 连接成功
     * performance
     */
    private void successEcho() {
        //fix bug insert world city null bug
        tvCity.setVisibility(View.VISIBLE);
        tvCity.setTextColor(ContextCompat.getColor(mContext,R.color.primaryColor));
        tvConnecting.setVisibility(View.INVISIBLE);
        showPreferCities();
        clockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchTo(WorldTimeAct.class);
            }
        });
        currentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchTo(WorldTimeAct.class);
            }
        });
    }


    /**
     * 连接失败performance
     */
    private void failEcho() {
        reTry = true;
        tvConnecting.setVisibility(View.INVISIBLE);
        tvBleFail.setVisibility(View.VISIBLE);
        tvCity.setVisibility(View.INVISIBLE);
        tvBleFail.setClickable(true);
        tvBleFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conAgain();
                bus.clickTryAgain = true;
                EventBus.getDefault().post(bus);
            }
        });
        clockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(reTry) {
                    conAgain();
                    bus.clickTryAgain = true;
                    EventBus.getDefault().post(bus);
                    reTry = false;
                }
            }
        });
    }

    private void conAgain() {
        clockView.setStatus(DeviceStatus.CONNECTING);
        tvBleFail.setClickable(false);
        tvBleFail.setVisibility(View.INVISIBLE);
        mCurrentStatus = CONNECT_ING;
        tvConnecting.setVisibility(View.VISIBLE);
        startTask();
    }


    @Subscribe
    public void onEventMainThread(ChangeUI event) {
        renderUI(event);
    }


    /**
     * 根据蓝牙连接情况更新UI
     * @param event
     */
    public void renderUI(ChangeUI event) {
        if (CONNECT_DFU == event.btCode) {
            startTask();
        }
        if (RENDER_AGAIN.equals(event.action)) {
            showPreferCities();
        }
        if(CONNECT_AGAIN.equals(event.action)){
            conAgain();
        }
    }


    /**
     * 从db取出数据渲染页面
     */
    private void showPreferCities() {
        mPreCities = mDBHelper.getAllPreferCities();
        for (int i = 0; i < mPreCities.size(); i++) {
            if (mPreCities.get(i).isSel)
                mCurrentIndex = i;
        }
        Configuration.getInstance().LocaleHandler(mContext, new Configuration.LocaleHandler() {
            @Override
            public void cnHandle() {
                selectCity = mPreCities.get(mCurrentIndex).zh_cn;
            }

            @Override
            public void twHandle() {
                selectCity = mPreCities.get(mCurrentIndex).zh_tw;
            }

            @Override
            public void hkHandle() {
                selectCity = mPreCities.get(mCurrentIndex).zh_hk;
            }

            @Override
            public void enHandle() {
                //just for bug en is null  2018/02/01
                if(TextUtils.isEmpty(mPreCities.get(mCurrentIndex).en)){
                    HttpCityRes bean = AppController.getGson().fromJson(mDBHelper.getCache(WORLD_CITY), HttpCityRes.class);
                    mDBHelper.fixBugWorldCityNull( bean);
                    mDBHelper.updateTimeStamp(WORLD_CITY_KEY, TimeUtil.getNowTimeSeconds());
                }
                //...
                selectCity = mPreCities.get(mCurrentIndex).en;
            }

            @Override
            public void defaultHandle() {
                tvCity.setText(mPreCities.get(mCurrentIndex).zh_cn);
            }
        });
        tvCity.setText(selectCity);
        currentTv.setTimeZone(TimeZone.getTimeZone(mPreCities.get(mCurrentIndex).zone));
        clockView.setZoneId(mDBHelper.getSettingZone());
        BleWorldCityHelper.setCurrentTime(MAC, I2B_SyncTime2(TimeUtil.getNowTimeSeconds(mDBHelper.getSettingZone()) - TimeUtil.getWatchSysStartTimeSecs()), new BleMgr.IWriteResponse() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFail() {

            }
        });

    }


    /**
     * 设置默认页面
     */
    private void initDefaultView(View view) {
        mCurrentStatus = CONNECT_ING;
        bus = new HomePageBus();
        bus.clickTryAgain = false;
        clockView = (MainClockView) view.findViewById(R.id.clock);
        currentTv = (CurrentTimeTv) view.findViewById(R.id.currentTv);
        tvCity = (TextView) view.findViewById(R.id.tv_selcity);
        tvConnecting = (TextView) view.findViewById(R.id.tv_connecting);
        tvBleFail = (TextView) view.findViewById(R.id.tv_ble_fail);
        tvNetFail = (TextView) view.findViewById(R.id.tv_net_fail);
        textStyle = new TextStyle(ContextCompat.getColor(mContext, R.color.mi_title_color), 0);
        waitStrings = new String[]{"...","..",".",""};
        Configuration.getInstance().LocaleHandler(mContext, new Configuration.LocaleHandler() {
            @Override
            public void cnHandle() {
                strConnecting = "设备连接中,请稍候";
            }

            @Override
            public void twHandle() {
                strConnecting = "裝置連接中,請稍候";
            }

            @Override
            public void hkHandle() {
                strConnecting = "裝置連接中,請稍候";
            }

            @Override
            public void enHandle() {
                strConnecting = "Connecting to your device, please wait";
            }

            @Override
            public void defaultHandle() {
                strConnecting = "设备连接中,请稍候";
            }
        });
//        tvConnecting.setText(textStyle.clear().span(strConnecting).span(waitStrings[0]).getText());
        waitCounter = 1;
        startTask();
        currentTv.onStart();
    }

    /**
     *开启任务
     */
    private void startTask() {
        final ScheduledExecutorService taskPool = Executors.newSingleThreadScheduledExecutor();
        taskPool.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCurrentStatus == CONNECT_ING) {
                            if (waitCounter > (waitStrings.length - 1) * 2) {
                                waitCounter = 0;
                            }
                            tvConnecting.setText(textStyle.clear().span(strConnecting)
                                    .span(waitStrings[waitStrings.length - 1 - waitCounter / 2])
                                    .spanColor(waitStrings[waitCounter / 2]).getText());//机智 呵呵哒
                            waitCounter++;
                        } else if (mCurrentStatus == CONNECT_FAIL) {
                            taskPool.shutdownNow();
                        } else if (mCurrentStatus == CONNECT_SUCCESS) {
                            taskPool.shutdownNow();
                        }
                    }
                });
            }
        }, 0, 250, TimeUnit.MILLISECONDS);
    }


    @Override
    public void renderSuccess() {
        mCurrentStatus = CONNECT_SUCCESS;
        clockView.setZoneId(mDBHelper.getSettingZone());
        clockView.setStatus(DeviceStatus.CONNECTED);
        successEcho();
    }


    @Override
    public void renderFail() {
        mCurrentStatus = CONNECT_FAIL;
        clockView.setStatus(DeviceStatus.TIMEOUT);
        failEcho();
    }

    @Override
    public void netLost() {
        clockView.setStatus(DeviceStatus.TIMEOUT);
        tvConnecting.setVisibility(View.INVISIBLE);
        tvCity.setVisibility(View.INVISIBLE);
        tvNetFail.setVisibility(View.VISIBLE);
    }
}
