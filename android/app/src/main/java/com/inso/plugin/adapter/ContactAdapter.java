package com.inso.plugin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.inso.R;
import com.inso.plugin.model.PickVipEntity;
import com.inso.plugin.view.indexable.IndexableAdapter;

/**
 * vip联系人
 */
public class ContactAdapter extends IndexableAdapter<PickVipEntity> {
    private LayoutInflater mInflater;
    private Context context;
    public ContactAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateTitleViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.watch_item_index_contact, parent, false);
        return new IndexVH(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.watch_item_contact, parent, false);
        return new ContentVH(view);
    }

    @Override
    public void onBindTitleViewHolder(RecyclerView.ViewHolder holder, String indexTitle) {
        IndexVH vh = (IndexVH) holder;
        vh.tv.setText(indexTitle);
    }

    @Override
    public void onBindContentViewHolder(RecyclerView.ViewHolder holder, PickVipEntity entity) {
        ContentVH vh = (ContentVH) holder;
        vh.tvName.setText(entity.name);
        vh.tvNum.setText(entity.number);
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
        TextView tvName,tvNum;
        CheckBox checkBox;

        public ContentVH(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvNum = (TextView) itemView.findViewById(R.id.tvNumber);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }
    }
}
