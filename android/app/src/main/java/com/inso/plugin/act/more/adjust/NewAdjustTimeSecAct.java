package com.inso.plugin.act.more.adjust;


import com.inso.R;
import com.inso.plugin.basic.BasicSingleButtonAct;
import com.inso.plugin.event.AdjustTimeBus;

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
