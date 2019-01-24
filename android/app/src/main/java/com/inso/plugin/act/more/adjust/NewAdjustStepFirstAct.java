package com.inso.plugin.act.more.adjust;


import com.inso.R;
import com.inso.core.BleMgr;
import com.inso.plugin.basic.BasicMultiButtonAct;
import com.inso.plugin.event.AdjustStepBus;
import com.inso.plugin.manager.BleManager;
import com.inso.plugin.tools.L;

import org.greenrobot.eventbus.Subscribe;

import java.util.UUID;

import static com.inso.plugin.tools.Constants.GattUUIDConstant.CHARACTERISTIC_STEP_DRIVER;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.CHARACTERISTIC_STEP_DRIVER_COMPLETE;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;


/**
 * Created by chendong on 2018/8/1.
 */

public class NewAdjustStepFirstAct extends BasicMultiButtonAct {

    boolean hasChanged = false;

    @Subscribe
    public void onEventMainThread(AdjustStepBus event) {
        if (event.finish) {
            finish();
        }
    }

    @Override
    protected String getTipText() {
        return getString(R.string.xx_01);
    }

    @Override
    protected String getLeftBtnText() {
        return getString(R.string.move_hand);
    }

    @Override
    protected String getRightBtnText() {
        return getString(R.string.next_step);
    }

    protected int getContentViewLayout() {
        return R.layout.watch_content_step_first;
    }

    @Override
    protected void onRightClick() {
        switchTo(NewAdjustStepSecAct.class);
    }

    @Override
    protected void onLeftClick() {
        onStartDriver();
    }

    @Override
    public void onStartDriver() {
        if (!hasChanged) hasChanged = true;
        BleMgr.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_STEP_DRIVER), BleManager.I2B_StepDriver(1));
    }

    @Override
    public boolean hasChangedDriver() {
        return hasChanged;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (hasChanged) {
            L.e("adjust step write 00000");
            BleMgr.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_STEP_DRIVER_COMPLETE), new byte[]{0, 0, 0, 0});
//            EventBus.getDefault().post(new AdjustStepBus(true));
        }
    }
}
