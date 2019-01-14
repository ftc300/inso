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
import com.inso.plugin.model.CityEntity;
import com.inso.plugin.tools.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 世界城市搜索结果显示Fragment
 */
public class CitySearchFragment extends BasicFragment {
    private RecyclerView mRecyclerView;
    private TextView mTvNoResult;
    public SearchAdapter mAdapter;
    private List<CityEntity> mDatas;
    private String mQueryText;
    public List<CityEntity> mSelDatas = new ArrayList<>();
    private ICheckOnClick listener;

    public static CitySearchFragment getInstance() {
        return  new CitySearchFragment();
    }

    public void setListener(ICheckOnClick listener) {
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

    public void bindDatas(List<CityEntity> datas) {
        this.mDatas = datas;
        mAdapter = new SearchAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        if (mQueryText != null) {
            mAdapter.getFilter().filter(mQueryText);
        }
    }

    /**
     * 根据newText 进行查找, 显示
     */
    public void bindQueryText(String newText) {
        if (mDatas == null) {
            mQueryText = newText.toLowerCase();
        } else if (!TextUtils.isEmpty(newText)) {
            mAdapter.getFilter().filter(newText.toLowerCase());
        }
    }

    public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.VH> implements Filterable {
        public List<CityEntity> items = new ArrayList<>();

        public SearchAdapter() {
            items.clear();
            items.addAll(mDatas);
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            final VH holder = new VH(LayoutInflater.from(getActivity()).inflate(R.layout.watch_item_city, parent, false));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (position >= 0) {
                        CityEntity entity = items.get(position);
                        if (entity.status && null != listener ) {
                            entity.status = false;
                            if (mSelDatas.contains(entity)) {
                                mSelDatas.remove(entity);
                            }
                            listener.ifItemOriginChecked(entity);
                        } else if (null != listener && listener.onClickedAllowed()) {
                            entity.status = true;
                            if (!mSelDatas.contains(entity)) {
                                mSelDatas.add(entity);
                            }
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
        public void onBindViewHolder(final VH holder, final int position) {
            Configuration.getInstance().LocaleHandler(mContext, new Configuration.LocaleHandler() {
                @Override
                public void cnHandle() {
                    holder.tvName.setText(items.get(position).getName());
                }

                @Override
                public void twHandle() {
                    holder.tvName.setText(items.get(position).zh_tw);
                }

                @Override
                public void hkHandle() {
                    holder.tvName.setText(items.get(position).zh_hk);
                }

                @Override
                public void enHandle() {
                    holder.tvName.setText(items.get(position).en);
                }

                @Override
                public void defaultHandle() {
                    holder.tvName.setText(items.get(position).getName());
                }
            });
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
                    ArrayList<CityEntity> list = new ArrayList<>();
                    for (CityEntity item : mDatas) {
                        if (item.getPinyin().startsWith(constraint.toString()) || containsIgnoreCase(item.getName(),constraint.toString())) {
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
                    ArrayList<CityEntity> list = (ArrayList<CityEntity>) results.values;
                    items.clear();
                    items.addAll(list);
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
            private TextView tvName;
            private CheckBox checkBox;

            public VH(View itemView) {
                super(itemView);
                tvName = (TextView) itemView.findViewById(R.id.tv_name);
                checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            }
        }
    }
}
