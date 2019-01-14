package com.inshow.watch.android.act.debug.DebugStepStatistics;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.dao.DebugStepDao;
import com.inshow.watch.android.provider.DebugDBHelper;

import java.util.ArrayList;
import java.util.List;

import static com.inshow.watch.android.act.debug.DebugStepStatistics.DebugStepNumberAct.ARG_CATCH_TYPE;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/12/21
 * @ 描述:
 */
public class DebugHistoryStepAct extends BasicAct {
    private List<DebugStepDao> data = new ArrayList<>();
    private DebugStepAdp adp;
    private Button btnAdd;
    private View headView;
    private DebugDBHelper debugDBHelper;
    private ListView mListView;
    private int catchType;

    private void addListHead() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        headView = inflater.inflate(R.layout.watch_debug_step_item, null);
        ((TextView) headView.findViewById(R.id.tv00)).setText("测试时间");
        ((TextView) headView.findViewById(R.id.tv01)).setText("测试类型");
        ((TextView) headView.findViewById(R.id.tv02)).setText("目标");
        ((TextView) headView.findViewById(R.id.tv03)).setText("采集数据");
        mListView.addHeaderView(headView);
    }

    @Override
    protected int getContentRes() {
        return R.layout.watch_act_debug_step_history;
    }

    @Override
    protected void initViewOrData() {
        catchType = getIntent().getIntExtra(ARG_CATCH_TYPE, 0);
        setTitleText(catchType == 0 ? "设备采样数据历史记录" : "用户采样历史记录");
        setBtnOnBackPress();
        btnAdd = (Button) findViewById(R.id.add);
        btnAdd.setVisibility(View.GONE);
        mListView = (ListView) findViewById(R.id.listView);
        debugDBHelper = new DebugDBHelper(mContext);
        data = debugDBHelper.getDebugStepDao(catchType == 0 ? MAC : null);
        btnAdd = (Button) findViewById(R.id.add);
        btnAdd.setVisibility(View.GONE);
        adp = new DebugStepAdp(mContext, data);
        mListView.setAdapter(adp);
        addListHead();
    }

    @Override
    public void onPause() {
        super.onPause();
        debugDBHelper.close();
    }
}
