package com.inshow.watch.android.act.setting;

import android.view.View;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.act.setting.newadjust.NewAdjustStepFirstAct;
import com.inshow.watch.android.act.setting.newadjust.NewAdjustTimeFirstAct;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.event.AdjustStepBus;
import com.inshow.watch.android.event.AdjustTimeBus;

import org.greenrobot.eventbus.Subscribe;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/8/25
 * @ 描述:
 */

public class AdjustMainAct extends BasicAct {

    private TextView tvTime,tvTimeStatus,tvStep,tvStepStatus;

    @Subscribe
    public void onEventMainThread(AdjustTimeBus event) {
        if(event.finish) {
            tvTimeStatus.setVisibility(View.VISIBLE);
            tvTimeStatus.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvTimeStatus.setVisibility(View.INVISIBLE);
                }
            },2000);
        }
    }

    @Subscribe
    public void onEventMainThread(AdjustStepBus event) {
        if(event.finish) {
            tvStepStatus.setVisibility(View.VISIBLE);
            tvStepStatus.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvStepStatus.setVisibility(View.INVISIBLE);
                }
            }, 2000);
        }
    }

    @Override
    protected int getContentRes() {
        return R.layout.watch_act_adjust_main;
    }

    @Override
    protected void initViewOrData() {
        setBtnOnBackPress();
        setTitleText(getString(R.string.adjust_ponit));
        setActStyle(ActStyle.WT);
        tvTime = (TextView) findViewById(R.id.tv_adjust_time);
        tvTimeStatus = (TextView) findViewById(R.id.tv_adjust_time_status);
        tvStep= (TextView) findViewById(R.id.tv_adjust_step);
        tvStepStatus = (TextView) findViewById(R.id.tv_adjust_step_status);

        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToWithEventBus(NewAdjustTimeFirstAct.class);
            }
        });

        tvStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToWithEventBus(NewAdjustStepFirstAct.class);
            }
        });
    }
}
