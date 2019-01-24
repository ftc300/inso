package com.inso.notify;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.inso.R;
import com.inso.core.HttpAPI;
import com.inso.entity.http.Information;
import com.inso.plugin.tools.L;
import com.inso.watch.baselib.base.BaseFragment;
import com.inso.watch.baselib.base.CommonAct;
import com.inso.watch.baselib.base.WebFragment;
import com.inso.watch.baselib.wigets.recycler.CommonAdapter;
import com.inso.watch.baselib.wigets.recycler.base.ViewHolder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.inso.watch.baselib.Constants.BASE_URL;

public class NotifyFrg extends BaseFragment {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private List<Information.ItemsBean> data = new ArrayList<>();

    public static NotifyFrg getInstance(){
        return new NotifyFrg();
    }

    @Override
    protected int getContentRes() {
        return R.layout.frg_mall;
    }

    @Override
    protected void initViewOrData() {
        super.initViewOrData();
        setTitle("小喇叭");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        HttpAPI api = retrofit.create(HttpAPI.class);
        Call<Information> call = api.getInformation();
        call.enqueue(new Callback<Information>() {
            @Override
            public void onResponse(Call<Information> call, Response<Information> response) {
                data = response.body().getItems();
                mRecyclerView.setAdapter(new CommonAdapter<Information.ItemsBean>(mActivity, R.layout.item_xiaolaba, data) {
                    @Override
                    protected void convert(ViewHolder holder, final Information.ItemsBean item, int position) {
                        holder.setText(R.id.title, item.getTitle());
                        holder.setText(R.id.desc, item.getDescription());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(item.getCreated_at()*1000);
                        holder.setText(R.id.date, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
                        final ImageView img =  holder.getView(R.id.img);
                        Picasso.get()
                                .load(item.getCover())
                                .placeholder(R.drawable.pic_default)
                                .error(R.drawable.pic_error)
                                .transform(new Transformation() {
                                    @Override
                                    public Bitmap transform(Bitmap source) {
                                        int targetWidth = img.getWidth();
                                        if (source.getWidth() == 0) {
                                            return source;
                                        }
                                        //如果图片大小大于等于设置的宽度，则按照设置的宽度比例来缩放
                                        double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                                        int targetHeight = (int) (targetWidth * aspectRatio);
                                        if (targetHeight != 0 && targetWidth != 0) {
                                            Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                                            if (result != source) {
                                                // Same bitmap is returned if sizes are the same
                                                source.recycle();
                                            }
                                            return result;
                                        } else {
                                            return source;
                                        }
                                    }

                                    @Override
                                    public String key() {
                                        return "transformation" + " desiredWidth";                                    }
                                })
                                .into(img);
                        holder.setOnClickListener(R.id.ll_item, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle args = WebFragment.configArgs(item.getTitle(), item.getLink(), null);
                                CommonAct.start(mActivity, WebFragment.class, args);
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(Call<Information> call, Throwable t) {
                L.d(t.toString());
            }
        });
    }
}
