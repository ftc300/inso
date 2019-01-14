package com.inshow.watch.android.act.setting.newadjust;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicMultiButtonAct;
import com.inshow.watch.android.event.AdjustStepBus;
import com.inshow.watch.android.manager.BleManager;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.view.LabelTextRow;
import com.xiaomi.smarthome.bluetooth.Response;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.UUID;

import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_STEP_DRIVER;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_STEP_DRIVER_COMPLETE;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;

/**
 * Created by chendong on 2018/8/1.
 */

public class NewAdjustStepFirstAct extends BasicMultiButtonAct {

    boolean hasChanged = false;
    @Subscribe
    public void onEventMainThread(AdjustStepBus event) {
        if(event.finish) {
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
        if(!hasChanged) hasChanged = true;
        XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_STEP_DRIVER), BleManager.I2B_StepDriver(1), new Response.BleWriteResponse() {
            @Override
            public void onResponse(int code, Void data) {
            }
        });
    }

    @Override
    public boolean hasChangedDriver() {
        return hasChanged;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(hasChanged) {
            L.e("adjust step write 00000" );
            XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_STEP_DRIVER_COMPLETE), new byte[]{0, 0, 0, 0}, new Response.BleWriteResponse() {
                @Override
                public void onResponse(int code, Void data) {

                }
            });
//            EventBus.getDefault().post(new AdjustStepBus(true));
        }
    }
}
