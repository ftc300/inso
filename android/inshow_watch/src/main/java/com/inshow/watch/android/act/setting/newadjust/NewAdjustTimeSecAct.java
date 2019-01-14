package com.inshow.watch.android.act.setting.newadjust;

import android.view.View;

import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicSingleButtonAct;
import com.inshow.watch.android.event.AdjustTimeBus;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by chendong on 2018/8/1.
 */

public class NewAdjustTimeSecAct extends BasicSingleButtonAct {

    @Override
    protected void btnOnClick() {
        EventBus.getDefault().post(new AdjustTimeBus(true));
        finish();
    }

    @Override
    protected String getTipText() {
        return getString(R.string.tap_calibrate);
    }

    @Override
    protected String getBtnText() {
        return getString(R.string.calibrate);
    }



}
