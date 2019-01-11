package com.inso.product;
import com.inso.R;
import com.inso.watch.baselib.base.BaseFragment;

public class ProductFrg extends BaseFragment {

    public static ProductFrg getInstance(){
        return new ProductFrg();
    }

    @Override
    protected int getContentRes() {
        return R.layout.frg_cp;
    }

    @Override
    protected void initViewOrData() {
        super.initViewOrData();
        setTitle("产品");
    }

}
