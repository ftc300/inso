package com.inshow.watch.android.act.debug.DebugStepStatistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.dao.DebugStepDao;
import com.inshow.watch.android.tools.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/12/21
 * @ 描述:
 */


public class DebugStepAdp extends BaseAdapter {

    private Context mContext;
    private List<DebugStepDao> data = new ArrayList<>();
    private String[] type ;


    public DebugStepAdp(Context mContext, List<DebugStepDao> data) {
        this.mContext = mContext;
        this.data = data;
        type = mContext.getResources().getStringArray(R.array.debug_step);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public DebugStepDao getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        final View view;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.watch_debug_step_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv00 = (TextView) view.findViewById(R.id.tv00);
            viewHolder.tv01 = (TextView) view.findViewById(R.id.tv01);
            viewHolder.tv02 = (TextView) view.findViewById(R.id.tv02);
            viewHolder.tv03 = (TextView) view.findViewById(R.id.tv03);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        final DebugStepDao entity = getItem(position);
        viewHolder.tv00.setText(TimeUtil.convertLongMilliTimeToDate(entity.starttime)+"\n~\n"+TimeUtil.convertLongMilliTimeToDate(entity.endtime));
        viewHolder.tv01.setText(type[entity.type]);
        viewHolder.tv02.setText(String.valueOf(entity.goal));
        viewHolder.tv03.setText((entity.endstep - entity.startstep)+"\n(始:"+entity.startstep+"\n末:"+entity.endstep+")"  );
        return view;
    }

    static class ViewHolder {
        TextView tv00;
        TextView tv01;
        TextView tv02;
        TextView tv03;
        View line;
    }

}
