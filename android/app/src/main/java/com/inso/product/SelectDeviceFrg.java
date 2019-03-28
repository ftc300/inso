package com.inso.product;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.inso.R;
import com.inso.core.Utils;
import com.inso.entity.http.Product;
import com.inso.watch.baselib.base.BaseFragment;
import com.inso.watch.baselib.base.CommonAct;
import com.inso.watch.baselib.wigets.recycler.CommonAdapter;
import com.inso.watch.baselib.wigets.recycler.base.ViewHolder;

import java.util.ArrayList;
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
    String dataString  = "{\"errcode\":0,\"errmsg\":\"ok\",\"items\":[{\"logo\":\"http:\\/\\/106.14.205.6\\/storage\\/upload\\/20190326\\/pEZR17WpEuN2ZNag1YlcO-Zk5YEm2pCpU-bZni6H.png\",\"name\":\"隐秀石英表二代\",\"model\":\"inso_watch2\",\"description\":\"隐秀石英表二代，艺术与科技的结合\"},{\"logo\":\"http:\\/\\/106.14.205.6\\/storage\\/upload\\/20190326\\/NVXhrGaTc0t7VEi0AaQoBocjSGR1BeUi1IvkOca5.png\",\"name\":\"IBONZ\",\"model\":\"inso_ibonz\",\"description\":\"艺术与时尚相融合\"},{\"logo\":\"http:\\/\\/106.14.205.6\\/storage\\/upload\\/20190326\\/b-sTFnOndZ0zLFVTD5koij8wHvofW_2BYulFq7L6.png\",\"name\":\"隐秀AI翻译机\",\"model\":\"inso_translate_robot\",\"description\":\"您的随身翻译助理，想去哪儿就去哪儿！\"},{\"logo\":\"http:\\/\\/106.14.205.6\\/storage\\/upload\\/20190326\\/uTamOTJXoqZ3HuflYF0ZSAP47UOGupwuA6j6XWv8.png\",\"name\":\"隐秀石英表\",\"model\":\"inso_watch\",\"description\":\"隐秀石英，艺术与科技的结合\"}],\"_links\":{\"self\":{\"href\":\"http:\\/\\/api.inshowlife.cn\\/v1\\/product\\/list?access_token=VyU7MmlnEH-In4YpCOiFBzwNfIVA5c4f&page=1\"}},\"_meta\":{\"totalCount\":4,\"pageCount\":1,\"currentPage\":1,\"perPage\":20}}";
    List<Product> data = new ArrayList<>();


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
        Product p = new Gson().fromJson(dataString,Product.class);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(new CommonAdapter<Product.ItemsBean>(mActivity,R.layout.select_device_item,p.getItems()) {
            @Override
            protected void convert(ViewHolder holder, final Product.ItemsBean item, final int position) {
                holder.setText(R.id.tvContent, item.getName());
                Utils.showWebIcon(item.getLogo(),holder.getView(R.id.imgLogo),R.drawable.pic_product_default);
                holder.setOnClickListener(R.id.ll_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(item.getModel().equals("inso_watch2")) {
                            CommonAct.start(mContext, BindFrg.class);
                        }
                    }
                });
            }
        });
    }
}