package com.inso.plugin.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import com.inso.R;
import com.inso.plugin.basic.BasicFragment;
import com.inso.plugin.model.PickVipEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 联系人搜索结果显示Fragment
 */
public class ContactSearchFragment extends BasicFragment {
    private RecyclerView mRecyclerView;
    private TextView mTvNoResult;
    private SearchAdapter mAdapter;
    private List<PickVipEntity> mDatas;
    private String mQueryText;
    public  List<PickVipEntity> mSelDatas = new ArrayList<>();
    private ICheckOnClick<PickVipEntity> listener;

    public static ContactSearchFragment getInstance(){
        return new ContactSearchFragment();
    }

    public void setListener(ICheckOnClick<PickVipEntity> listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.watch_fragment_search, container, false);
        mTvNoResult = (TextView) view.findViewById(R.id.tv_no_result);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recy);
        return view;
    }

    public void bindDatas(List<PickVipEntity> datas) {
        this.mDatas = datas;
        mAdapter = new SearchAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        if (mQueryText != null) {
            mAdapter.getFilter().filter(mQueryText);
        }
    }


    public void notifyDataChanged(){
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 根据newText 进行查找, 显示
     */
    public void bindQueryText(String newText) {
        if (mDatas == null) {
            mQueryText = newText;
//            mQueryText = newText.toLowerCase();
        } else if (!TextUtils.isEmpty(newText)) {
            mAdapter.getFilter().filter(newText);
//            mAdapter.getFilter().filter(newText.toLowerCase());
        }
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.VH> implements Filterable {
        private List<PickVipEntity> items = new ArrayList<>();

        public SearchAdapter() {
            items.clear();
            items.addAll(mDatas);
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            final VH holder = new VH(LayoutInflater.from(getActivity()).inflate(R.layout.watch_item_contact, parent, false));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (position >= 0) {
                        PickVipEntity entity = items.get(position);
                        if (entity.status && null != listener ) {//选中时
                            entity.status = false;
                            if (mSelDatas.contains(entity)) {
                                mSelDatas.remove(entity);
                            }
                            listener.ifItemOriginChecked(entity);
                        } else if (null != listener && listener.onClickedAllowed()) {//不是选中时
                            entity.status = true;
                            if (!mSelDatas.contains(entity)) {
                                mSelDatas.add(entity);
                            }
                        }
                        if(null!=listener){
                            listener.onItemClick(entity);
                        }
                        notifyDataSetChanged();
                    }
                }
            });
            return holder;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            holder.tvName.setText(items.get(position).name);
            holder.tvNum.setText(items.get(position).number);
            holder.checkBox.setChecked(items.get(position).status);
        }
        /***
         * 是否包含指定字符串,不区分大小写
         * @return
         */
        public  boolean containsIgnoreCase(String str, String searchStr)     {
            if(str == null || searchStr == null) return false;

            final int length = searchStr.length();
            if (length == 0)
                return true;

            for (int i = str.length() - length; i >= 0; i--) {
                if (str.regionMatches(true, i, searchStr, 0, length))
                    return true;
            }
            return false;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    ArrayList<PickVipEntity> list = new ArrayList<>();
                    for (PickVipEntity item : mDatas) {
                        if ( containsIgnoreCase(item.name,constraint.toString())|| item.number.contains(constraint)) {
                            list.add(item);
                        }
                    }
                    FilterResults results = new FilterResults();
                    results.count = list.size();
                    results.values = list;
                    return results;
                }

                @Override
                @SuppressWarnings("unchecked")
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    ArrayList<PickVipEntity> list = (ArrayList<PickVipEntity>) results.values;
                    items.clear();
                    if(null!=list) {
                        items.addAll(list);
                    }
                    if (results.count == 0) {
                        mTvNoResult.setVisibility(View.VISIBLE);
                    } else {
                        mTvNoResult.setVisibility(View.INVISIBLE);
                    }
                    notifyDataSetChanged();
                }
            };
        }

        class VH extends RecyclerView.ViewHolder {
            private TextView tvName,tvNum;
            private CheckBox checkBox;

            public VH(View itemView) {
                super(itemView);
                tvName = (TextView) itemView.findViewById(R.id.tvName);
                tvNum = (TextView) itemView.findViewById(R.id.tvNumber);
                checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            }
        }
    }
}
