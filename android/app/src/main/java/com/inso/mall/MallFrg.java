package com.inso.mall;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.inso.R;
import com.inso.core.Utils;
import com.inso.core.basic.RecycleRefreshFrg;
import com.inso.entity.http.Product;
import com.inso.watch.baselib.base.CommonAct;
import com.inso.watch.baselib.base.WebFragment;
import com.inso.watch.baselib.wigets.recycler.CommonAdapter;
import com.inso.watch.baselib.wigets.recycler.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import static com.inso.watch.baselib.Constants.BASE_URL;

public class MallFrg extends RecycleRefreshFrg<Product> {
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
    protected void initViewOrData() {
        super.initViewOrData();
        recyclerView.setBackgroundColor(getResources().getColor(R.color.white));
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 13;
                int i = parent.getChildLayoutPosition(view) % 2;
                switch (i) {
                    case 0:
                        outRect.left = Utils.dpToPx(mContext, 13);
                        break;
                    case 1:
                        outRect.left = Utils.dpToPx(mContext, 10);
                        outRect.right = Utils.dpToPx(mContext, 13);
                        break;
                }
            }
        });
    }

    @Override
    protected void dealWithFetchData(Product product) {
        data = product.getItems();
        mAdapter = new CommonAdapter<Product.ItemsBean>(mContext, R.layout.item_mall, data) {
            @Override
            protected void convert(ViewHolder holder, Product.ItemsBean item, int position) {
                holder.setText(R.id.tvProductName, item.getName());
                holder.setText(R.id.tvDesc, item.getDescription());
                Utils.showWebIcon(item.getLogo(), holder.getView(R.id.imgLogo),R.color.white);
                holder.setOnClickListener(R.id.ll_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle args = WebFragment.configArgs("米家石英表", "https://www.mi.com/mj-watch/", null);
                        CommonAct.start(MallFrg.this.mContext, WebFragment.class, args);
                    }
                });
            }
        };
        if (null != data && data.size() == 0) mLoadStatusBox.empty();
    }

}
