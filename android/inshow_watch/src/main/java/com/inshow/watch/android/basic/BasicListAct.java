package com.inshow.watch.android.basic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.inshow.watch.android.R;

/**
 * Created by chendong on 2017/3/28.
 */
public class BasicListAct extends BasicAct {
    protected ListView mListView;

    @Override
    protected int getContentRes() {
        return R.layout.watch_list_and_add;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInShowEmptyView();
    }

    /**
     *设置无数据时的文字
     * @return
     */
    protected String getEmptyString()
    {
        return "";
    }

    protected  void setInShowEmptyView() {
        LayoutInflater inflater = getLayoutInflater();
        View emptyView = inflater.inflate(R.layout.watch_list_empty_item, null);
        TextView textView = (TextView) emptyView.findViewById(R.id.tv_empty);
        textView.setText(getEmptyString());
        mListView  = (ListView) findViewById(R.id.listView);
        addContentView(emptyView, mListView.getLayoutParams());
        mListView.setEmptyView(emptyView);
    }

}
