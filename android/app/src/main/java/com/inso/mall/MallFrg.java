package com.inso.mall;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.inso.R;
import com.inso.core.basic.RecycleRefreshFrg;
import com.inso.entity.http.Product;
import com.inso.watch.baselib.base.CommonAct;
import com.inso.watch.baselib.base.WebFragment;
import com.inso.watch.baselib.wigets.recycler.CommonAdapter;
import com.inso.watch.baselib.wigets.recycler.base.ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.inso.watch.baselib.Constants.BASE_URL;

public class MallFrg extends RecycleRefreshFrg<Product> {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private List<Product.ItemsBean> data = new ArrayList<>();

    public static MallFrg getInstance() {
        return new MallFrg();
    }

    @Override
    protected String getRequestUrl() {
        return BASE_URL + "product/list";
    }

    @Override
    protected String getTitle() {
        return "商城";
    }

    @Override
    protected void dealWithFetchData(Product product) {
        data = product.getItems();
        mRecyclerView.setAdapter(new CommonAdapter<Product.ItemsBean>(mActivity, R.layout.select_device_item, data) {
                                     @Override
                                     protected void convert(ViewHolder holder, Product.ItemsBean item, int position) {
                                         holder.setText(R.id.tvContent, item.getName());
                                         Picasso.get()
                                                 .load(item.getLogo())
                                                 .placeholder(R.drawable.pic_default)
                                                 .error(R.drawable.pic_error)
                                                 .into((ImageView) holder.getView(R.id.img_head));
                                         holder.setOnClickListener(R.id.ll_item, new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 Bundle args = WebFragment.configArgs("米家石英表", "https://www.mi.com/mj-watch/", null);
                                                 CommonAct.start(mActivity, WebFragment.class, args);
                                             }
                                         });
                                     }
                                 });
    };

//    @Override
//    protected int getContentRes() {
//        return R.layout.frg_mall;
//    }

        @Override
        protected void initViewOrData () {
            super.initViewOrData();
        }

    }
