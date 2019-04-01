package com.inso.core.basic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.inso.LoginAct;
import com.inso.R;
import com.inso.core.CacheMgr;
import com.inso.core.HttpMgr;
import com.inso.watch.baselib.base.BaseFragment;
import com.inso.watch.baselib.wigets.LoadStatusBox;
import com.inso.watch.baselib.wigets.ToastWidget;
import com.inso.watch.baselib.wigets.recycler.CommonAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/2/13
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public abstract class RecycleRefreshFrg<T> extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    protected RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected CommonAdapter mAdapter;
    @BindView(R.id.loadStatusBox)
    protected LoadStatusBox mLoadStatusBox;
    @BindView(R.id.tvNoData)
    protected TextView mTvNoData;
    Unbinder unbinder;
    private Class<T> cls = null;
    protected Handler mHandler = new Handler();
    private CacheMgr mCache;

    protected abstract String getRequestUrl();

    protected abstract String getTitle();

    protected abstract void dealWithFetchData(T t);

    /**
     * 设置LoadingView点击事件（重新请求）
     */
    private void setLoadingClickListener() {
        mLoadStatusBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadStatusBox.loading();
                loadRecyclerViewData();
            }
        });
    }

    @Override
    protected int getContentRes() {
        return R.layout.base_refresh_recycle;
    }

    @Override
    protected void initViewOrData() {
        // SwipeRefreshLayout
        Class clz = this.getClass();
        ParameterizedType type = (ParameterizedType) clz.getGenericSuperclass();
        Type[] types = type.getActualTypeArguments();
        cls = (Class<T>) types[0];
        mCache = CacheMgr.get(mContext);
        setTitle(getTitle());
        setLoadingClickListener();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.black_50_transparent);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        String cache = mCache.getAsString(getRequestUrl());
        if (null != cache) {
            fillAdapter(cache);
            mLoadStatusBox.success();
        } else {
            loadRecyclerViewData();
        }
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        // Showing refresh animation before making http call
        try {
            if (null != mSwipeRefreshLayout) {
                mSwipeRefreshLayout.setRefreshing(true);
            }
            loadRecyclerViewData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRecyclerViewData() {
        HttpMgr.getJsonObjectRequest(mContext, getRequestUrl(), new HttpMgr.IResponse<JSONObject>() {
            @Override
            public void onSuccess(final JSONObject obj) {
                try {
                    if (obj.getInt("errcode") == 401) { //invalid credentials &&  expired
                        ToastWidget.showWarn(mContext, "登录已经过期，请重新登录");
                        mContext.startActivity(new Intent(mContext, LoginAct.class));
                        ((Activity) mContext).finish();
                        ((Activity) mContext).overridePendingTransition(0, 0);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (null != mHandler) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (null != mSwipeRefreshLayout && null != mLoadStatusBox && null != recyclerView) {
                                mLoadStatusBox.success();
                                mTvNoData.setVisibility(View.GONE);
                                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                                mSwipeRefreshLayout.setRefreshing(false);
                                fillAdapter(obj.toString());
                                mCache.put(getRequestUrl(), obj.toString(), CacheMgr.TIME_HOUR);
                            }
                        }
                    }, 800);
                }

            }

            @Override
            public void onFail() {
                if (null != mHandler) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (null != mSwipeRefreshLayout && null != mLoadStatusBox && null != recyclerView) {
                                mSwipeRefreshLayout.setRefreshing(false);
                                mLoadStatusBox.failed();
                                mTvNoData.setVisibility(View.GONE);
                                mSwipeRefreshLayout.setVisibility(View.GONE);
                                mCache.remove(getRequestUrl());
                            }
                        }
                    }, 800);
                }
            }
        });
    }

    private void fillAdapter(String obj) {
        T t = new Gson().fromJson(obj, cls);
        dealWithFetchData(t);
        recyclerView.setAdapter(mAdapter);
        showNoData();
    }

    protected void showNoData(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}