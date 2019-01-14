package com.inshow.watch.android.act.setting;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.manager.AppController;

/**
 * Created by chendong on 2017/4/17.
 */
public class DfuErrorAct extends BasicAct {
    private TextView version;
    private Button exit;

    @Override
    protected int getContentRes() {
        return R.layout.watch_act_dfu_error;
    }

    @Override
    protected boolean isNeedTitle() {
        return false;
    }

    @Override
    protected void initViewOrData() {
        version = (TextView) findViewById(R.id.tv_version);
        exit = (Button) findViewById(R.id.exit);
        version.setText(TextUtils.concat("固件版本V",getIntent().getStringExtra("dfu_version")));
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                AppController.getInstance().exit();
            }
        });
    }

    @Override
    protected boolean isNeedBackPress() {
        return false;
    }
}

