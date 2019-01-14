package com.inshow.watch.android.act.dfu;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.event.HomePageBus;
import com.inshow.watch.android.manager.SPManager;

import org.greenrobot.eventbus.EventBus;

import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_DEVICE_NAME;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_ARG_FIRMWARE_VERSION;

/**
 * Created by chendong on 2017/4/17.
 */

public class DfuFailAct extends BasicAct {

//    private TextView currentTv;
    private Button ok;


    @Override
    protected int getContentRes() {
        return R.layout.watch_act_dfu_fail;
    }

    @Override
    protected void initViewOrData() {
        setTitleText(mDBHelper.getCache(SP_ARG_DEVICE_NAME));
        setActStyle(ActStyle.DFU);
        findViewById(R.id.title_bar_return).setVisibility(View.GONE);
//        currentTv = (TextView) findViewById(R.id.currentTv);
        ok = (Button) findViewById(R.id.ok);
//        currentTv.setText(TextUtils.concat(getString(R.string.current_ver),(String)SPManager.get(mContext,SP_ARG_FIRMWARE_VERSION,"")));
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomePageBus bus = new HomePageBus();
                bus.forceUpgrade = true;
                EventBus.getDefault().post(bus);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

}
