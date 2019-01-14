package com.inshow.watch.android.act.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.act.datasheet.AccessBarData;
import com.inshow.watch.android.model.DataSheetEntity;
import com.inshow.watch.android.tools.Configuration;
import com.inshow.watch.android.view.AutoLocateHorizontalView;

import java.util.List;

/**
 * chendong 2017/03/22
 * 数据表适配器
 */
public class DataSheetBarAdapter extends RecyclerView.Adapter implements AutoLocateHorizontalView.IAutoLocateHorizontalView {
    List<DataSheetEntity> source;
    private Context context;
    private int maxValue = -1;
    private View itemView;
    private int maxHeight = 200;
    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        notifyDataSetChanged();
    }
    public DataSheetBarAdapter(Context context, List<DataSheetEntity> source ){
        this.source = source;
        this.context = context;
        for(DataSheetEntity item:source){
            if(item.step > maxValue){
                maxValue = item.step;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.watch_item_bar,parent,false);
        this.itemView = itemView;
        return  new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String content = source.get(position).dateString;
        TextView tv = ((ViewHolder) holder).tvDate;
        if(!TextUtils.isEmpty(content)) {
            tv.setText(content);
        }
    }

    @Override
    public int getItemCount() {
        return source.size();
    }

    @Override
    public View getItemView() {
        return itemView;
    }



    @Override
    public void onViewSelected(boolean isSelected, int pos, RecyclerView.ViewHolder holder, int itemWidth) {
        final ViewHolder holder1 = (ViewHolder) holder;
        ViewGroup.LayoutParams params = holder1.bar.getLayoutParams();
        params.height = (int) (source.get(pos).step *1f / maxValue * maxHeight);
        params.width = 5*itemWidth/6;
        holder1.bar.setLayoutParams(params);
        Configuration.getInstance().LocaleHandler(context, new Configuration.LocaleHandler() {
            @Override
            public void cnHandle() {
                Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/MIUI_EX_Bold.ttf");
                holder1.tvDate.setTypeface(typeFace);
            }

            @Override
            public void twHandle() {
                Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/MIUI_EX_Bold.ttf");
                holder1.tvDate.setTypeface(typeFace);
            }

            @Override
            public void hkHandle() {
                Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/MIUI_EX_Bold.ttf");
                holder1.tvDate.setTypeface(typeFace);
            }

            @Override
            public void enHandle() {

            }

            @Override
            public void defaultHandle() {

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            holder1.bar.setBackground(ContextCompat.getDrawable(context,isSelected?R.drawable.gradient_sel:R.drawable.gradient_unsel));
        }else {
            holder1.bar.setBackgroundDrawable(ContextCompat.getDrawable(context,isSelected?R.drawable.gradient_sel:R.drawable.gradient_unsel));
        }
        holder1.tvDate.setTextColor(ContextCompat.getColor(context,isSelected?R.color.black_60_transparent:R.color.black_20_transparent));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View bar;
        public TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            bar = (View)itemView.findViewById(R.id.view_bar);
            tvDate = (TextView) itemView.findViewById(R.id.tv_bar);
        }
    }
}
