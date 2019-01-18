package com.inso.plugin.act.more.order;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.inso.R;
import com.inso.plugin.act.more.order.drag.WatchDragItemCallBack;
import com.inso.plugin.act.more.order.drag.WatchRecycleCallBack;
import com.inso.plugin.basic.BasicAct;
import com.inso.plugin.manager.SPManager;
import com.inso.plugin.tools.L;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.inso.plugin.tools.Constants.SystemConstant.SP_FUNCTION_ORDER;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/1/16
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class FunOrderAct extends BasicAct implements WatchRecycleCallBack {
    private RecyclerView mRecyclerView;
    private WatchDragAdapter mAdapter;
    private ArrayList<String> data;
    private ArrayList<String> originData;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected int getContentRes() {
        return R.layout.watch_act_fun_order;
    }

    @Override
    protected void initViewOrData() {
        setBtnOnBackPress();
        setTitleText("屏幕顺序");
        setActStyle(ActStyle.BT);
        initRecyclerData();
        mRecyclerView = findViewById(R.id.recycle);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new WatchDragAdapter(this, data);
        mItemTouchHelper = new ItemTouchHelper(new WatchDragItemCallBack(this));
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onMove(int from, int to) {
        synchronized (this) {
            if (from > to) {
                int count = from - to;
                for (int i = 0; i < count; i++) {
                    Collections.swap(data, from - i, from - i - 1);
                }
            }
            if (from < to) {
                int count = to - from;
                for (int i = 0; i < count; i++) {
                    Collections.swap(data, from + i, from + i + 1);
                }
            }
            mAdapter.setData(data);
            mAdapter.notifyItemMoved(from, to);
            dealWithData();
        }
    }

    private void dealWithData() {
        String ret = "";
        for (int i = 0; i < data.size(); i++) {
            ret = ret.concat(data.get(i));
            if (i != data.size() - 1) {
                ret = ret.concat(",");
            }
        }
        L.d(ret);
        SPManager.put(mContext, SP_FUNCTION_ORDER, ret);
    }

    private void initRecyclerData() {
        String getData = (String) SPManager.get(mContext, SP_FUNCTION_ORDER, "");
        int l = getData.length();
        if(l>0){
            String [] ret = getData.split(",");
            data = new ArrayList<>(Arrays.asList(ret));
        }else {
            data = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.fun_order)));
        }
    }

}
