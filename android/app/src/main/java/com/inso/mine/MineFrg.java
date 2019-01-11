package com.inso.mine;
import com.inso.R;
import com.inso.watch.baselib.base.BaseFragment;

public class MineFrg extends BaseFragment {

    public static MineFrg getInstance(){
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

}
