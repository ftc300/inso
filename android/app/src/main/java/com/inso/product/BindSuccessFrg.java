package com.inso.product;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.inso.R;
import com.inso.core.Utils;
import com.inso.plugin.act.mainpagelogic.PluginMainAct;
import com.inso.watch.baselib.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/3/18
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class BindSuccessFrg extends BaseFragment {

    @BindView(R.id.box3)
    CheckBox mBox3;
    @BindView(R.id.btnSure)
    Button mBtnSure;

    @Override
    protected int getContentRes() {
        return R.layout.frg_bind_success;
    }

    @OnClick({R.id.box3, R.id.btnSure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.box3:
                break;
            case R.id.btnSure:
                if(mBox3.isChecked())
                Utils.addShortcut(mActivity,"米家石英表2");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switchToWithEventBus(PluginMainAct.class);
                    }
                },10);
                break;
        }
    }
}
