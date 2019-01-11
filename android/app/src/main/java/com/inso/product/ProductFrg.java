package com.inso.product;
import android.view.View;

import com.inso.R;
import com.inso.watch.baselib.base.BaseFragment;
import com.inso.watch.baselib.base.CommonAct;

public class ProductFrg extends BaseFragment {

    public static ProductFrg getInstance(){
        return new ProductFrg();
    }

    @Override
    protected int getContentRes() {
        return R.layout.frg_product;
    }

    @Override
    protected void initViewOrData() {
        super.initViewOrData();
        setTitleR(false, "产品", R.drawable.add, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonAct.start(mActivity,SelectDeviceFrg.class);
            }
        });
    }

}
