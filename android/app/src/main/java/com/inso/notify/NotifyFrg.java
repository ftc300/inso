package com.inso.notify;
import com.inso.R;
import com.inso.watch.baselib.base.BaseFragment;

public class NotifyFrg extends BaseFragment {

    public static NotifyFrg getInstance(){
        return new NotifyFrg();
    }

    @Override
    protected int getContentRes() {
        return R.layout.frg_lb;
    }

    @Override
    protected void initViewOrData() {
        super.initViewOrData();
        setTitle("小喇叭");
    }

}
