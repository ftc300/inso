package com.inso.product;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.inso.R;
import com.inso.watch.baselib.base.BaseFragment;
import com.inso.watch.baselib.base.CommonAct;
import com.inso.watch.baselib.wigets.recycler.CommonAdapter;
import com.inso.watch.baselib.wigets.recycler.base.ViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/1/11
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class SelectDeviceFrg extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    List<String> data = new ArrayList<>(Arrays.asList("米家石英表","隐秀AI翻译机","IBONZ猪年生肖表","米家石英表2"));

    @Override
    protected int getContentRes() {
        return R.layout.frg_product_select_device;
    }

    @Override
    protected void initViewOrData() {
        super.initViewOrData();
        setTitleR(true, "选择要添加的产品", R.drawable.product_scan, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(new CommonAdapter<String>(mActivity,R.layout.select_device_item,data) {
            @Override
            protected void convert(ViewHolder holder, String arg_s, final int position) {
                holder.setText(R.id.tvContent, arg_s);
                holder.setOnClickListener(R.id.ll_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonAct.start(mContext,BindFrg.class);
                    }
                });
            }
        });
    }
}