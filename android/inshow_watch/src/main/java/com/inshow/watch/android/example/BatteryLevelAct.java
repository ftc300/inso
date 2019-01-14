package com.inshow.watch.android.example;

import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.manager.BleManager;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.TextStyle;
import com.inshow.watch.android.view.CircleProgressView;
import com.inshow.watch.android.view.SolidDot;
import com.xiaomi.smarthome.bluetooth.Response;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;

import java.util.UUID;

import static com.inshow.watch.android.manager.BleManager.bytesToHexString;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_POWER_CONSUMPTION;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;

/**
 * Created by chendong on 2017/1/22.
 * 电池电量
 */
public class BatteryLevelAct extends BasicAct {

    private int mLevel;//电池电量
    private int mMins;//
    private int color;//显示的颜色
    private CircleProgressView circleProgressView;
    private TextView tvProgress;
    private TextStyle mTs;
    private int[] mPowerConsumption = new int[6];
    private String[] arrConsumption ;
    private TextView[] arrTv = new TextView[4];

    @Override
    protected int getContentRes() {
        return R.layout.watch_activity_battery_level;
    }

    @Override
    protected void initViewOrData() {
        setBtnOnBackPress();
        setTitleText(getString(R.string.B01));
        arrConsumption =  getResources().getStringArray(R.array.power_consumption);
        arrTv[0] = (TextView) findViewById(R.id.tv_time);
        arrTv[1] = (TextView) findViewById(R.id.tv_vibration);
        arrTv[2] = (TextView) findViewById(R.id.tv_step);
        arrTv[3] = (TextView) findViewById(R.id.tv_datatrans);
        XmBluetoothManager.getInstance().read(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_POWER_CONSUMPTION), new Response.BleReadResponse() {
                @Override
            public void onResponse(int code, byte[] bytes) {
                L.e("PowerConsumption:" + bytesToHexString(bytes));
                if (code == XmBluetoothManager.Code.REQUEST_SUCCESS) {
                    mPowerConsumption = BleManager.getPowerConsumption(bytes);
                    mLevel = mPowerConsumption[0];
                    mMins = mPowerConsumption[1];
                    if(mLevel>=50)
                        color = R.color.watch_blue;
                    else
                        color = R.color.watch_red;
                    mTs = new TextStyle(getResources().getColor(color), 60);
                    arrTv[0].setText(mTs.clear().span(arrConsumption[0]).span("\t"+mPowerConsumption[2]+"%").getText());
                    arrTv[1].setText(mTs.clear().span(arrConsumption[1]).span("\t"+mPowerConsumption[3]+"%").getText());
                    arrTv[2].setText(mTs.clear().span(arrConsumption[2]).span("\t"+mPowerConsumption[4]+"%").getText());
                    arrTv[3].setText(mTs.clear().span(arrConsumption[3]).span("\t"+mPowerConsumption[5]+"%").getText());
                    ((SolidDot)findViewById(R.id.dot1)).setColor(color);
                    ((SolidDot)findViewById(R.id.dot2)).setColor(color);
                    ((SolidDot)findViewById(R.id.dot3)).setColor(color);
                    ((SolidDot)findViewById(R.id.dot4)).setColor(color);
                    tvProgress = (TextView) findViewById(R.id.tvProgress);
                    circleProgressView = (CircleProgressView) findViewById(R.id.circleProgressView);
                    circleProgressView.setmTxtHint1(getString(R.string.B08));
                    circleProgressView.setmTxtHint2(getUsedTime(mMins));
                    circleProgressView.setProgress(mLevel);
                    circleProgressView.setHightLightColor(color);
                    tvProgress.setText(mTs.clear().spanColorAndSize(mLevel+"").spanColor("\t%").getText());
                } else {
                    L.e("PowerConsumption:error");
                }
            }
        });

    }

    private String getUsedTime(int min) {
        int day = min / (12 * 60);
        int hour = (min % (12 * 60)) / 60;
        int minute = (min % (12 * 60) % 60);
        return mTs.clear().
                span(getString(R.string.have_used)).
                span(day+getString(R.string.time_day)).
                span(hour+getString(R.string.time_hour)).
                span(minute+getString(R.string.time_min))
                .getText().toString();
    }


}
