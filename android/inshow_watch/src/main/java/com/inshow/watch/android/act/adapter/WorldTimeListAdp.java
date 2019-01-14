package com.inshow.watch.android.act.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.dao.PreferCitiesDao;
import com.inshow.watch.android.tools.Configuration;
import com.inshow.watch.android.tools.TimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.inshow.watch.android.tools.MessUtil.setMarginsZero;

/**
 * Created by chendong on 2017/2/14.
 */

public class WorldTimeListAdp extends BaseAdapter {
    private List<PreferCitiesDao> list = new ArrayList<>();
    private Context mContext;
    private Map<Integer, Boolean> isCheck = new HashMap<Integer, Boolean>();

    public WorldTimeListAdp(Context mContext) {
        super();
        this.mContext = mContext;
        setCheck(false);
    }

    public void setCheck(boolean flag) {
        for (int i = 0; i < list.size(); i++) {
            isCheck.put(i, flag);
        }
        this.notifyDataSetChanged();
    }

    public void setData(List<PreferCitiesDao> data) {
        this.list = data;
        for (int i = 0; i < data.size() ; i++) {
            if(data.get(i).isSel)
                isCheck.put(i,true);
        }
    }

    public void addData(PreferCitiesDao bean) {
        list.add(0, bean);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public PreferCitiesDao getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder ;
        View view ;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.watch_worldtime_item, null);
            viewHolder = new ViewHolder();
            viewHolder.img = (ImageView) view.findViewById(R.id.img_world_time_sel);
            viewHolder.time = (TextView) view.findViewById(R.id.time);
            viewHolder.location = (TextView) view.findViewById(R.id.location);
            viewHolder.date = (TextView) view.findViewById(R.id.date);
            viewHolder.line = (View) view.findViewById(R.id.divider_line);
            viewHolder.footer = (View) view.findViewById(R.id.footer);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if(position == getCount()-1) {
            setMarginsZero(viewHolder.line);
            viewHolder.footer.setVisibility(View.VISIBLE);
        }else{
            viewHolder.footer.setVisibility(View.GONE);
        }
        setTimeStyle(position,viewHolder.time,viewHolder.img);
        final PreferCitiesDao bean = list.get(position);
        viewHolder.time.setText(TimeUtil.getHHMM(bean.zone));
        viewHolder.date.setText(TimeUtil.getWeekOfDate(mContext,bean.zone)+"\t\t"+TimeUtil.getMonDay(mContext,bean.zone));
        Configuration.getInstance().LocaleHandler(mContext, new Configuration.LocaleHandler() {
            @Override
            public void cnHandle() {
                viewHolder.location.setText(bean.zh_cn);
            }

            @Override
            public void twHandle() {
                viewHolder.location.setText(bean.zh_tw);
            }

            @Override
            public void hkHandle() {
                viewHolder.location.setText(bean.zh_hk);
            }

            @Override
            public void enHandle() {
                viewHolder.location.setText(bean.en);
            }

            @Override
            public void defaultHandle() {
                viewHolder.location.setText(bean.zh_cn);
            }
        });
        return view;
    }

    /**
     *时间显示选中与否显示不同
     */
    public void setTimeStyle(int pos,TextView textView,ImageView img) {
        if (isCheck.get(pos) == null) {
            isCheck.put(pos, false);
        }
        if(isCheck.get(pos)) {
            textView.setTextColor(mContext.getResources().getColor(R.color.primaryColor));
            img.setVisibility(View.VISIBLE);
        }else{
            textView.setTextColor(mContext.getResources().getColor(R.color.black_80_transparent));
            img.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 点击后全部设置为false
     * @param pos
     */
    public void setPosSelected(int pos) {
        setCheck(false);
        isCheck.put(pos,true);
        notifyDataSetChanged();
    }

    // 优化
     static class ViewHolder {
         TextView time;
         TextView location;
         TextView date;
         View line;
         View footer;
         ImageView img;
    }

    // 全选按钮获取状态
    public Map<Integer, Boolean> getMap() {
        return isCheck;
    }

    // 删除一个数据
    public void removeData(int position) {
        list.remove(position);
        this.notifyDataSetChanged();
    }


}
