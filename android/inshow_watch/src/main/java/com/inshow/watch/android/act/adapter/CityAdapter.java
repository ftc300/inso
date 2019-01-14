package com.inshow.watch.android.act.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.model.CityEntity;
import com.inshow.watch.android.tools.Configuration;
import com.inshow.watch.android.view.indexable.IndexableAdapter;

/**
 *世界城市
 */
public class CityAdapter extends IndexableAdapter<CityEntity> {
    private LayoutInflater mInflater;
    private Context context;

    public CityAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateTitleViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.watch_item_index_city, parent, false);
        return new IndexVH(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.watch_item_city, parent, false);
        return new ContentVH(view);
    }

    @Override
    public void onBindTitleViewHolder(RecyclerView.ViewHolder holder, String indexTitle) {
        IndexVH vh = (IndexVH) holder;
        vh.tv.setText(indexTitle);
    }

    @Override
    public void onBindContentViewHolder(RecyclerView.ViewHolder holder, final CityEntity entity) {
        final ContentVH vh = (ContentVH) holder;
        Configuration.getInstance().LocaleHandler(context, new Configuration.LocaleHandler() {
            @Override
            public void cnHandle() {
                vh.tv.setText(entity.getName());
            }

            @Override
            public void twHandle() {
                vh.tv.setText(entity.zh_tw);
            }

            @Override
            public void hkHandle() {
                vh.tv.setText(entity.zh_hk);
            }

            @Override
            public void enHandle() {
                vh.tv.setText(entity.en);
            }

            @Override
            public void defaultHandle() {
                vh.tv.setText(entity.getName());
            }
        });
        vh.checkBox.setChecked(entity.status);
    }

    private class IndexVH extends RecyclerView.ViewHolder {
        TextView tv;

        public IndexVH(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv_index);
        }
    }

    private class ContentVH extends RecyclerView.ViewHolder {
        TextView tv;
        CheckBox checkBox;

        public ContentVH(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv_name);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }
    }
}
