package com.inso.plugin.act.more.adjust;


import com.inso.R;
import com.inso.core.BleMgr;
import com.inso.plugin.basic.BasicMultiButtonAct;

import java.util.UUID;

import static com.inso.plugin.manager.BleManager.I2B_ClockDriver;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.CHARACTERISTIC_CLOCK_DRIVER;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;

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
        BleMgr.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CLOCK_DRIVER), I2B_ClockDriver(1));
    }

    @Override
    public boolean hasChangedDriver() {
        return false;
    }
}
