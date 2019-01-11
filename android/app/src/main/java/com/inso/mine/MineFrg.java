package com.inso.mine;

import android.view.View;

import com.inso.R;
import com.inso.watch.baselib.base.BaseFragment;
import com.inso.watch.baselib.base.CommonAct;

import butterknife.OnClick;

public class MineFrg extends BaseFragment {


    public static MineFrg getInstance() {
        return new MineFrg();
    }

    @Override
    protected int getContentRes() {
        return R.layout.frg_mine;
    }

    @Override
    protected void initViewOrData() {
        super.initViewOrData();
        setTitle("我的");
    }


    @OnClick({R.id.userIconLayout, R.id.mine_setting, R.id.mine_feedback, R.id.mine_about, R.id.mine_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.userIconLayout:
                CommonAct.start(mActivity,MineInfoFrg.class);
                break;
            case R.id.mine_setting:
                CommonAct.start(mActivity,MineInfoFrg.class);
                break;
            case R.id.mine_feedback:
                CommonAct.start(mActivity,MineInfoFrg.class);
                break;
            case R.id.mine_about:
                CommonAct.start(mActivity,MineInfoFrg.class);
                break;
            case R.id.mine_logout:
                CommonAct.start(mActivity,MineInfoFrg.class);
                break;
        }
    }
}
