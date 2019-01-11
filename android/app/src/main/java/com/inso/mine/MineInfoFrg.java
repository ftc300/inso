package com.inso.mine;

import com.inso.R;
import com.inso.watch.baselib.base.BaseFragment;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/1/11
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class MineInfoFrg extends BaseFragment {
    @Override
    protected int getContentRes() {
        return R.layout.frg_mine_info;
    }

    @Override
    protected void initViewOrData() {
        super.initViewOrData();
        setTitle(true,"我的信息");
    }

}
