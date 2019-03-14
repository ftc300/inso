package com.inso.product;

import android.view.View;

import com.inso.R;
import com.inso.core.bind.BindMgr;
import com.inso.core.bind.BindUiHandleImp;
import com.inso.watch.baselib.base.BaseFragment;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/1/11
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class BindFrg extends BaseFragment  {
    private BindMgr mBindMgr;
    private BindUiHandleImp mBindUiHandleImp;

    @Override
    protected int getContentRes() {
        return R.layout.frg_bind;
    }

    @Override
    protected void initViewOrData() {
        super.initViewOrData();
        setTitleL("取消", "米家石英表2", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBindUiHandleImp = new BindUiHandleImp(mActivity);
        mBindMgr = new BindMgr(mBindUiHandleImp);
        mBindMgr.startBind();
    }

}