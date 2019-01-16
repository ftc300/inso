package com.inso.plugin.act.more.order;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.inso.R;
import com.inso.plugin.act.more.order.drag.WatchDragItemCallBack;
import com.inso.plugin.act.more.order.drag.WatchRecycleCallBack;
import com.inso.plugin.basic.BasicAct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
    private ArrayList<String> mList;
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
        mList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.fun_order)));
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new WatchDragAdapter(this, mList);
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
                    Collections.swap(mList, from - i, from - i - 1);
                }
            }
            if (from < to) {
                int count = to - from;
                for (int i = 0; i < count; i++) {
                    Collections.swap(mList, from + i, from + i + 1);
                }
            }
            mAdapter.setData(mList);
            mAdapter.notifyItemMoved(from, to);
            mAdapter.show.clear();
            mAdapter.show.put(to, to);
        }
    }
}
