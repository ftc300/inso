package com.inso.plugin.act.more;

import android.view.View;

import com.inso.R;
import com.inso.plugin.act.more.adjust.AdjustMainAct;
import com.inso.plugin.act.more.order.FunOrderAct;
import com.inso.plugin.basic.BasicAct;
import com.inso.plugin.manager.SPManager;
import com.inso.watch.baselib.wigets.LabelTextRow;

import butterknife.BindView;
import butterknife.OnClick;

import static com.inso.plugin.tools.Constants.SystemConstant.SP_SCREEN_OFF_POSITION;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/1/16
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class MoreAct extends BasicAct {
    @BindView(R.id.duration)
    LabelTextRow mDuration;

    @Override
    protected int getContentRes() {
        return R.layout.watch_act_more;
    }

    @Override
    protected void initViewOrData() {
        setBtnOnBackPress();
        setTitleText("更多");
        setActStyle(ActStyle.BT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDuration.setText(getResources().getStringArray(R.array.screen_off)[(int)SPManager.get(mContext,SP_SCREEN_OFF_POSITION,0)]);
    }

    @OnClick({R.id.watch_device_info, R.id.order, R.id.duration, R.id.adjust, R.id.help, R.id.dfu, R.id.delete_device})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.watch_device_info:
                switchTo(DeviceInfoAct.class);
                break;
            case R.id.order:
                switchTo(FunOrderAct.class);
                break;
            case R.id.duration:
                switchTo(ScreenOffAct.class);
                break;
            case R.id.adjust:
                switchToWithEventBus(AdjustMainAct.class);
                break;
            case R.id.help:
                switchTo(InstructionAct.class);
                break;
            case R.id.dfu:
                break;
            case R.id.delete_device:
                break;
        }
    }

}
