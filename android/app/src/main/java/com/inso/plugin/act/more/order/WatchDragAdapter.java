package com.inso.plugin.act.more.order;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inso.R;
import com.inso.plugin.act.more.order.drag.WatchDragHolderCallBack;
import com.inso.plugin.act.more.order.drag.WatchRecycleCallBack;

import java.util.List;


public class WatchDragAdapter extends RecyclerView.Adapter<WatchDragAdapter.DragHolder> {

    private List<String> list;

    private WatchRecycleCallBack mRecycleClick;
//    public SparseArray<Integer> show = new SparseArray<>();

    public WatchDragAdapter(WatchRecycleCallBack click, List<String> data) {
        this.list = data;
        this.mRecycleClick = click;
    }

    public void setData(List<String> data) {
        this.list = data;
    }

    @Override
    public DragHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.watch_item_drag, parent, false);
        return new DragHolder(view, mRecycleClick);
    }

    @Override
    public void onBindViewHolder(final DragHolder holder, final int position) {
        holder.text.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class DragHolder extends RecyclerView.ViewHolder implements WatchDragHolderCallBack {

        public TextView text;
        public RelativeLayout item;

        public DragHolder(View itemView, WatchRecycleCallBack click) {
            super(itemView);
            item = (RelativeLayout) itemView.findViewById(R.id.item);
            text = (TextView) itemView.findViewById(R.id.text);
        }

        @Override
        public void onSelect() {
//            show.clear();
//            show.put(getAdapterPosition(), getAdapterPosition());
            itemView.setBackgroundColor(Color.parseColor("#80ffffff"));
        }

        @Override
        public void onClear() {
            itemView.setBackgroundResource(R.drawable.right_bottom_view);
            notifyDataSetChanged();
        }

    }
}
