package com.inso.notify;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.inso.R;
import com.inso.core.basic.RecycleRefreshFrg;
import com.inso.core.transformation.RoundedCornersTransformation;
import com.inso.entity.http.Information;
import com.inso.plugin.tools.DensityUtils;
import com.inso.watch.baselib.base.CommonAct;
import com.inso.watch.baselib.base.WebFragment;
import com.inso.watch.baselib.wigets.recycler.CommonAdapter;
import com.inso.watch.baselib.wigets.recycler.base.ViewHolder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.inso.watch.baselib.Constants.BASE_URL;

public class NotifyFrg extends RecycleRefreshFrg<Information> {

    private List<Information.ItemsBean> data = new ArrayList<>();

    public static NotifyFrg getInstance(){
        return new NotifyFrg();
    }

    @Override
    protected String getRequestUrl() {
        return BASE_URL + "data/information";
    }

    @Override
    protected String getTitle() {
        return "小喇叭";
    }

    @Override
    protected void dealWithFetchData(Information information) {
        data = information.getItems();
        mAdapter = new  CommonAdapter<Information.ItemsBean>(mActivity, R.layout.item_xiaolaba, data) {
            @Override
            protected void convert(ViewHolder holder, final Information.ItemsBean item, int position) {
                holder.setText(R.id.title, item.getTitle());
                holder.setText(R.id.desc, item.getDescription());
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(item.getCreated_at()*1000);
                holder.setText(R.id.date, new SimpleDateFormat("MM月dd日 HH:mm").format(calendar.getTime()));
                final ImageView img =  holder.getView(R.id.img);
                Picasso.get()
                        .load(item.getCover())
                        .placeholder(R.drawable.pic_default_large)
                        .error(R.drawable.pic_error)
                        .transform(new RoundedCornersTransformation(DensityUtils.dp2px(mContext,8),0, RoundedCornersTransformation.CornerType.TOP))
                        .fit()
                        .centerCrop()
                        .into(img);
                holder.setOnClickListener(R.id.ll_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle args = WebFragment.configArgs(item.getTitle(), item.getLink(), null);
                        CommonAct.start(mActivity, WebFragment.class, args);
                    }
                });
            }
        };
        if(null!= data && data.size() == 0)  mLoadStatusBox.empty();
    }


}
