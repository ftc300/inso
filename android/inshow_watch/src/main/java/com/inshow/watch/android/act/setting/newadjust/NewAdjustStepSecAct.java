package com.inshow.watch.android.act.setting.newadjust;


import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicSingleButtonAct;
import com.inshow.watch.android.event.AdjustStepBus;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by chendong on 2018/8/1.
 */

public class NewAdjustStepSecAct extends BasicSingleButtonAct {
    TextView contentTip;

    @Override
    protected void btnOnClick() {
        EventBus.getDefault().post(new AdjustStepBus(true));
        finish();
    }

    @Override
    protected String getTipText() {
        return getString(R.string.xx_00);
    }

    @Override
    protected String getBtnText() {
        return getString(R.string.calibrate);
    }

    protected int getContentViewLayout() {
        return R.layout.watch_content_step_sec;
    }

    @Override
    protected void initViewOrData() {
        super.initViewOrData();
        contentTip = (TextView)contentView.findViewById(R.id.tvContentTip);
        contentTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MLAlertDialog.Builder(mContext)
                        .setCancelable(false)
                        .setMessage(getString(R.string.reference_step_hand))
                        .setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

    }
}
