package com.inshow.watch.android.act.setting;

import android.view.View;

import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.view.IndicatorViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chendong on 2017/6/15.
 * 说明书
 */
public class InstructionAct extends BasicAct {

    private  IndicatorViewPager viewPager ;
    private List<Integer> mList = new ArrayList<>();
    @Override
    protected int getTitleRes() {
        return R.layout.title_bar_transparent_black;
    }

    @Override
    protected int getContentRes() {
        return R.layout.watch_activity_instruction;
    }

    @Override
    protected void initViewOrData() {
        setBtnOnBackPress();
        setActStyle(ActStyle.DT);
        findViewById(R.id.title_bar_more).setVisibility(View.GONE);
        initData();
    }

    private void initData() {
        mList.add(R.drawable.pageone);
        mList.add(R.drawable.pagetwo);
        mList.add(R.drawable.pagethree);
        mList.add(R.drawable.pagefour);
        mList.add(R.drawable.pagefive);
        mList.add(R.drawable.pagesix);
        mList.add(R.drawable.pageseven);
        viewPager = (IndicatorViewPager) findViewById(R.id.indicator);
        viewPager.setIndicators(R.drawable.ad_select, R.drawable.ad_unsel);
        viewPager.setData(mList, null);
        viewPager.setWheel(false);
    }
}
