package com.inshow.watch.android.act.setting.newadjust;

import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicMultiButtonAct;
import com.xiaomi.smarthome.bluetooth.Response;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;

import java.util.UUID;

import static com.inshow.watch.android.manager.BleManager.I2B_ClockDriver;
import static com.inshow.watch.android.manager.BleManager.I2B_StepDriver;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_CLOCK_DRIVER;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;

/**
 * Created by chendong on 2018/8/1.
 */

public class NewAdjustTimeThirdAct extends BasicMultiButtonAct {

    protected String getTipText() {
        return getString(R.string.tap_to_exact_mark);
    }

    @Override
    protected String getLeftBtnText() {
        return getString(R.string.move_hand);
    }

    @Override
    protected String getRightBtnText() {
        return getString(R.string.button_ok);
    }

    protected int getContentViewLayout() {
        return R.layout.watch_content_time_third;
    }

    @Override
    protected void onRightClick() {
        finish();
    }

    @Override
    protected void onLeftClick() {
        onStartDriver();
    }

    @Override
    public void onStartDriver() {
        XmBluetoothManager.getInstance().writeNoRsp(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CLOCK_DRIVER), I2B_ClockDriver(1), new Response.BleWriteResponse() {
            @Override
            public void onResponse(int code, Void data) {

            }
        });
    }

    @Override
    public boolean hasChangedDriver() {
        return false;
    }
}
