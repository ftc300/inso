package com.inshow.watch.android.act.debug;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.tools.TimeUtil;
import com.xiaomi.smarthome.bluetooth.Response;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;
import com.xiaomi.smarthome.common.ui.widget.TimePicker;
import java.util.UUID;
import static com.inshow.watch.android.manager.BleManager.I2B_ClockDriver;
import static com.inshow.watch.android.manager.BleManager.I2B_StepDriver;
import static com.inshow.watch.android.manager.BleManager.I2B_SyncTime;
import static com.inshow.watch.android.manager.BleManager.I2B_WatchTime;
import static com.inshow.watch.android.manager.BleManager.getTodayTimeSeconds;
import static com.inshow.watch.android.manager.BleManager.getWatchSysStartTimeSecs;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_CLOCK_DRIVER;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_STEP_DRIVER;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_SYNC_CURRENT_TIME;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_SYNC_WATCH_TIME;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_MAC;

/**
 * Created by chendong on 2017/1/22.
 */

public class TimeSyncAcitivity extends BasicAct {
    TextView subTitleView;
    TextView titleBarTitle;
    TimePicker timePicker;
    Button btnSync;
    Button btnClockDriver;
    Button btnStepDriver;
    Button tellWatchItsTime;
    private String mDeviceName;
    private String mDeviceAddress;
    private boolean isLongOnClick,isStepLongOnClick;

    @Override
    protected int getContentRes() {
        return R.layout.watch_activity_timesync;
    }

    @Override
    protected void initViewOrData() {
        mDeviceAddress = mDBHelper.getCache(SP_ARG_MAC);
        subTitleView = ((TextView) findViewById(R.id.sub_title_bar_title));
        titleBarTitle = ((TextView) findViewById(R.id.title_bar_title));
        btnSync = ((Button) findViewById(R.id.btnSync));
        btnClockDriver = ((Button) findViewById(R.id.clockDriver));
        btnStepDriver = ((Button) findViewById(R.id.stepDriver));
        tellWatchItsTime = ((Button) findViewById(R.id.tellWatchItsTime));
        timePicker = (TimePicker)findViewById(R.id.timePicker);
        titleBarTitle.setText("米家手表");
        subTitleView.setVisibility(View.VISIBLE);
        // 设置titlebar在顶部透明显示时的顶部padding
//        mHostActivity.setTitleBarPadding(findViewById(R.id.title_bar));
        findViewById(R.id.title_bar_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tellWatchItsTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour =  timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                int currentTime = getTodayTimeSeconds() - getWatchSysStartTimeSecs()+ 3600*hour +minute*60;
                int modeTime = currentTime%(12*3600);
                XmBluetoothManager.getInstance().write(mDeviceAddress, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_SYNC_WATCH_TIME), I2B_WatchTime(currentTime,modeTime), new Response.BleWriteResponse() {
                    @Override
                    public void onResponse(int code, Void data) {

                    }
                });
            }
        });
        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentTime = TimeUtil.getNowTimeSeconds()- getWatchSysStartTimeSecs();
                XmBluetoothManager.getInstance().write(mDeviceAddress, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_SYNC_CURRENT_TIME), I2B_SyncTime(currentTime), new Response.BleWriteResponse() {
                    @Override
                    public void onResponse(int code, Void data) {

                    }
                });
            }
        });

        btnStepDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                XmBluetoothManager.getInstance().writeNoRsp(mDeviceAddress, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_STEP_DRIVER), I2B_StepDriver(1), new Response.BleWriteResponse() {
                    @Override
                    public void onResponse(int code, Void data)
                    {

                    }
                });
            }
        });
        btnStepDriver.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                isStepLongOnClick = true;
                Thread t = new Thread(){
                    public void run() {
                        while (isStepLongOnClick){
                            try {
                                XmBluetoothManager.getInstance().writeNoRsp(mDeviceAddress, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_STEP_DRIVER), I2B_StepDriver(10), new Response.BleWriteResponse() {
                                    @Override
                                    public void onResponse(int code, Void data)
                                    {

                                    }
                                });
                                Thread.sleep(250);
                            }catch(InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    }
                };
                t.start();
                return true;
            }
        });

        btnStepDriver.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if( motionEvent.getAction() == MotionEvent.ACTION_UP ){
                    isStepLongOnClick = false;
                }
                return false;
            }
        });



        btnClockDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                XmBluetoothManager.getInstance().writeNoRsp(mDeviceAddress, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CLOCK_DRIVER), I2B_ClockDriver(1), new Response.BleWriteResponse() {
                    @Override
                    public void onResponse(int code, Void data) {

                    }
                });
            }
        });

        btnClockDriver.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                isLongOnClick = true;
                Thread t = new Thread(){
                    public void run() {
                        while (isLongOnClick){
                            try {
                                XmBluetoothManager.getInstance().writeNoRsp(mDeviceAddress, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CLOCK_DRIVER), I2B_StepDriver(10), new Response.BleWriteResponse() {
                                    @Override
                                    public void onResponse(int code, Void data)
                                    {

                                    }
                                });
                                Thread.sleep(250);
                            }catch(InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    }
                };
                t.start();
                return true;
            }
        });

        btnClockDriver.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if( motionEvent.getAction() == MotionEvent.ACTION_UP ){
                    isLongOnClick = false;
                }
                return false;
            }
        });


    }
}
