package com.inshow.watch.android.act.user;

import android.view.View;

import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.manager.SPManager;
import com.inshow.watch.android.view.LineOnePicker;

import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_HEIGHT;
import static com.inshow.watch.android.view.LineOnePicker.TWO_DIGIT_FORMATTER;

/**
 * Created by chendong on 2017/4/17.
 */

public class HeightAct extends BasicAct {

    private LineOnePicker height;

    @Override
    protected int getContentRes() {
        return R.layout.watch_act_height;
    }

    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    @Override
    protected void initViewOrData() {
        mHostActivity.enableBlackTranslucentStatus();
        height = (LineOnePicker) findViewById(R.id.height);
        height.setMaxValue(242);
        height.setMinValue(30);
        height.setValue(170);
        height.setFormatter(TWO_DIGIT_FORMATTER);
        findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchTo(SexAct.class);
                finish();
            }
        });
        findViewById(R.id.right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SPManager.put(mContext,SP_ARG_HEIGHT,height.getValue());
                switchTo(WeightAct.class);
                finish();
            }
        });
    }
}
