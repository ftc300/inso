package com.inso.product;

import android.view.View;

import com.inso.R;
import com.inso.watch.baselib.base.BaseFragment;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/1/11
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class SelectDeviceFrg  extends BaseFragment {

    @Override
    protected int getContentRes() {
        return R.layout.frg_product;
    }

    @Override
    protected void initViewOrData() {
        super.initViewOrData();
        setTitleR(true, "选择要添加的产品", R.drawable.product_scan, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}