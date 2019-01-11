package com.inso.mall;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.inso.R;
import com.inso.watch.baselib.base.BaseFragment;
import com.inso.watch.baselib.base.CommonAct;
import com.inso.watch.baselib.base.WebFragment;
import com.inso.watch.baselib.wigets.recycler.CommonAdapter;
import com.inso.watch.baselib.wigets.recycler.base.ViewHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

public class MallFrg extends BaseFragment {
    List<String> data = new ArrayList<>(Arrays.asList("米家石英表", "隐秀AI翻译机", "IBONZ猪年生肖表", "米家石英表2"));
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    public static MallFrg getInstance() {
        return new MallFrg();
    }

    @Override
    protected int getContentRes() {
        return R.layout.frg_mall;
    }

    @Override
    protected void initViewOrData() {
        super.initViewOrData();
        setTitle("商城");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(new CommonAdapter<String>(mActivity, R.layout.select_device_item, data) {
            @Override
            protected void convert(ViewHolder holder, String arg_s, int position) {
                holder.setText(R.id.tvContent, arg_s);
                holder.setOnClickListener(R.id.ll_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle args = WebFragment.configArgs("米家石英表", "https://www.mi.com/mj-watch/", null);
                        CommonAct.start(mActivity, WebFragment.class, args);
                    }
                });
            }
        });
    }

}
