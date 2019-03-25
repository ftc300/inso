package com.inso.product;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.inso.R;
import com.inso.core.basic.RecycleRefreshFrg;
import com.inso.entity.http.DeviceList;
import com.inso.watch.baselib.base.CommonAct;
import com.inso.watch.baselib.wigets.recycler.CommonAdapter;
import com.inso.watch.baselib.wigets.recycler.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import static com.inso.watch.baselib.Constants.BASE_URL;

public class ProductFrg extends RecycleRefreshFrg<DeviceList> {

    public static ProductFrg getInstance(){
        return new ProductFrg();
    }
    private List<DeviceList.ResultBean> data = new ArrayList<>();

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

    @Override
    protected String getRequestUrl() {
        return BASE_URL + "device/list";
    }

    @Override
    protected String getTitle() {
        return "";
    }

    @Override
    protected void dealWithFetchData(DeviceList list) {
        data = list.getResult();
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new CommonAdapter<DeviceList.ResultBean>(mActivity, R.layout.item_user_products, data) {
            @Override
            protected void convert(ViewHolder holder,DeviceList.ResultBean item, int position) {
            }
        };
    }
}
