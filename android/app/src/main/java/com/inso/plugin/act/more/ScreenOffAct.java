package com.inso.plugin.act.more;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.inso.R;
import com.inso.plugin.basic.BasicAct;
import com.inso.plugin.manager.SPManager;
import com.inso.watch.baselib.wigets.recycler.CommonAdapter;
import com.inso.watch.baselib.wigets.recycler.base.ViewHolder;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;

import static com.inso.plugin.tools.Constants.SystemConstant.SP_SCREEN_OFF_POSITION;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/1/16
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class ScreenOffAct extends BasicAct{
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    int selectPosition = 0;
    ArrayList<String> data;

    @Override
    protected int getContentRes() {
        return R.layout.watch_screen_off;
    }

    @Override
    protected void initViewOrData() {
        setBtnOnBackPress();
        setTitleText("超时息屏");
        setActStyle(ActStyle.BT);
        data = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.screen_off)));
        selectPosition = (int) SPManager.get(mContext,SP_SCREEN_OFF_POSITION,0);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new CommonAdapter<String>(this,R.layout.watch_item_screen_off,data) {
            protected void convert(final ViewHolder holder, String arg_s, final int position) {
                if(position == selectPosition){
                    holder.setVisible(R.id.img,true);
                }
                holder.setText(R.id.content, arg_s);
                holder.setOnClickListener(R.id.item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRecyclerView.getChildAt(selectPosition).findViewById(R.id.img).setVisibility(View.GONE);
                        selectPosition = position;
                        SPManager.put(mContext,SP_SCREEN_OFF_POSITION,position);
                        holder.setVisible(R.id.img,true);
                    }
                });
            }
        });
    }
}
