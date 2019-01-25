package com.inso.mall;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.inso.R;
import com.inso.core.HttpAPI;
import com.inso.entity.http.Product;
import com.inso.plugin.tools.L;
import com.inso.watch.baselib.base.BaseFragment;
import com.inso.watch.baselib.base.CommonAct;
import com.inso.watch.baselib.base.WebFragment;
import com.inso.watch.baselib.wigets.recycler.CommonAdapter;
import com.inso.watch.baselib.wigets.recycler.base.ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.inso.watch.baselib.Constants.BASE_URL;

public class MallFrg extends BaseFragment {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private  List<Product.ItemsBean> data = new ArrayList<>();

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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        HttpAPI api = retrofit.create(HttpAPI.class);
        Call<Product> call = api.getProductList();
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                data = response.body().getItems();
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
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                L.d(t.toString());
            }
        });

    }

}
