package com.inso.mall;


import android.os.Bundle;

import com.inso.R;
import com.inso.watch.baselib.base.BaseFragment;
import com.inso.watch.baselib.base.CommonAct;
import com.inso.watch.baselib.base.WebFragment;

import butterknife.OnClick;

public class MallFrg extends BaseFragment {

    public static MallFrg getInstance() {
        return new MallFrg();
    }

    @Override
    protected int getContentRes() {
        return R.layout.frg_sc;
    }

    @Override
    protected void initViewOrData() {
        super.initViewOrData();
        setTitle("商城");
    }

    @OnClick(R.id.text)
    public void onViewClicked() {
        Bundle args = WebFragment.configArgs("米家石英表","https://www.mi.com/mj-watch/", null);
        CommonAct.start(mActivity, WebFragment.class, args);
    }
}
