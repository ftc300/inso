package com.inso.core.basic;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.inso.R;
import com.inso.core.HttpMgr;
import com.inso.entity.http.FatherResponse;
import com.inso.plugin.tools.L;
import com.inso.watch.baselib.base.BaseFragment;
import com.inso.watch.baselib.wigets.ToastWidget;
import com.inso.watch.baselib.wigets.recycler.CommonAdapter;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import butterknife.BindView;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/2/13
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public abstract class RecycleRefreshFrg<T extends FatherResponse> extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;
    CommonAdapter mAdapter;
    private Class<T> cls = null;
    protected abstract String getRequestUrl();
    protected abstract String getTitle();
    protected abstract void dealWithFetchData(T t);


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

        setTitle(getTitle());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.black_50_transparent);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadRecyclerViewData();
            }
        });
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        // Showing refresh animation before making http call
        mSwipeRefreshLayout.setRefreshing(true);
        loadRecyclerViewData();
    }

    private void loadRecyclerViewData() {
        HttpMgr.getRequestQueue(mActivity).add(HttpMgr.getRequest(getRequestUrl(), new HttpMgr.IResponse<JSONObject>() {
            @Override
            public void onSuccess(JSONObject obj) {
                L.d("#######  getRequest onSuccess from " + getRequestUrl() + "\n" + obj.toString());
                T t = new Gson().fromJson(obj.toString(),cls);
                mSwipeRefreshLayout.setRefreshing(false);
                dealWithFetchData(t);
            }

            @Override
            public void onFail() {
                ToastWidget.showFail(mActivity, "Fetch Error!");
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }));
    }

}