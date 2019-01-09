package com.inso;
import com.inso.watch.baselib.base.BaseFragment;

public class ShopFrg extends BaseFragment {
    public static ShopFrg getInstance(){
        return new ShopFrg();
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

}
