package com.inso.plugin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.inso.R;
import com.inso.plugin.alarm.AlarmHelper;
import com.inso.plugin.dao.AlarmDao;
import com.inso.plugin.provider.DBHelper;
import com.inso.plugin.tools.TimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.inso.plugin.tools.MessUtil.setMarginsZero;


/**
 * Created by chendong on 2017/2/14.
 */

public class AlarmClockListAdp extends BaseAdapter {

    private List<AlarmDao> list = new ArrayList<AlarmDao>();
    private Context mContext;
    private Map<Integer, Boolean> isCheck = new HashMap<Integer, Boolean>();
    private boolean isSelectState;
    private Animation inAnimation, outAnimation;
    private onAlarmClockCheckChanged listener;
    private DBHelper dbHelper;

    public void setAlarmClockCheckChangedListener(onAlarmClockCheckChanged listener) {
        this.listener = listener;
    }

    public AlarmClockListAdp(Context mContext, DBHelper dbHelper) {
        super();
        this.mContext = mContext;
        this.dbHelper = dbHelper;
        setCheck(false);
        isSelectState = false;
        inAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_check_in);
        outAnimation = AnimationUtils.loadAnimation(mContext, R.anim.anim_check_out);
    }

    public void setCheck(boolean flag) {
        for (int i = 0; i < list.size(); i++) {
            isCheck.put(i, flag);
        }
        this.notifyDataSetChanged();
    }

    public void setData(List<AlarmDao> data) {
        this.list = data;
    }

    public void notifyDataChanged(List<AlarmDao> data) {
        this.list = data;
        this.notifyDataSetChanged();
    }

    public void addData(AlarmDao bean) {
        list.add(0, bean);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public AlarmDao getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.watch_alarmclock_item, null);
            viewHolder = new ViewHolder();
            viewHolder.dayperiod = (TextView) view.findViewById(R.id.tv_day_period);
            viewHolder.time = (TextView) view.findViewById(R.id.time);
            viewHolder.weekNum = (TextView) view.findViewById(R.id.weekNum);
            viewHolder.timeLeft = (TextView) view.findViewById(R.id.timeLeft);
            viewHolder.switchButton = (CheckBox) view.findViewById(R.id.switchButton);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            viewHolder.line = (View) view.findViewById(R.id.divider_line);
            viewHolder.footer = (View) view.findViewById(R.id.footer);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if (position == getCount() - 1) {
            setMarginsZero(viewHolder.line);
            viewHolder.footer.setVisibility(View.VISIBLE);
        } else {
            viewHolder.footer.setVisibility(View.GONE);
        }
        final AlarmDao bean = list.get(position);
        viewHolder.dayperiod.setText(AlarmHelper.getDayPeriod(mContext, bean.seconds));
        viewHolder.time.setText(AlarmHelper.getDisplayClock(bean.seconds));
        viewHolder.weekNum.setText(AlarmHelper.getDisplayRepeatType(mContext, bean.repeatType));
        if (bean.repeatType.length() == 1 && Integer.parseInt(bean.repeatType) == 0) {
            viewHolder.timeLeft.setText(bean.status ? AlarmHelper.getDisplayRemainTime(mContext, bean.extend - TimeUtil.getNowTimeSeconds(dbHelper.getSettingZone())) : mContext.getString(R.string.off));
        } else {
            viewHolder.timeLeft.setText(bean.status ? AlarmHelper.getDisplayRemainTime(mContext, AlarmHelper.getNextAlarmRestSeconds(dbHelper, bean.repeatType, dbHelper.getSettingZone(), bean.seconds)) : mContext.getString(R.string.off));
        }
        viewHolder.switchButton.setChecked(bean.status);
        viewHolder.switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.timeLeft.setText(viewHolder.switchButton.isChecked() ? AlarmHelper.getDisplayRemainTime(mContext, AlarmHelper.getNextAlarmRestSeconds(dbHelper, bean.repeatType, dbHelper.getSettingZone(), bean.seconds)) : mContext.getString(R.string.off));
                if (listener != null)
                    listener.onCheckedChanged(position, viewHolder.switchButton.isChecked());
            }
        });

        if (isSelectState) {
            viewHolder.switchButton.setVisibility(View.GONE);
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            viewHolder.checkBox.startAnimation(inAnimation);
        } else {
            viewHolder.switchButton.setVisibility(View.VISIBLE);
            viewHolder.checkBox.startAnimation(outAnimation);
            viewHolder.checkBox.setVisibility(View.GONE);
        }
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCheck.put(position, isChecked);
            }
        });
        if (isCheck.get(position) == null) {
            isCheck.put(position, false);
        }
        viewHolder.checkBox.setChecked(isCheck.get(position));
        return view;
    }


    /**
     * 默认的item显示状态
     */
    public void setCommonState() {
        isSelectState = false;
        notifyDataSetChanged();
    }

    /**
     * 弹出弹窗时显示选择按钮
     */
    public void setSelectState() {
        isSelectState = true;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView dayperiod;
        TextView time;
        TextView weekNum;
        TextView timeLeft;
        CheckBox switchButton;
        CheckBox checkBox;
        View footer;
        View line;
    }

    public Map<Integer, Boolean> getMap() {
        return isCheck;
    }

    public void removeData(int position) {
        list.remove(position);
        this.notifyDataSetChanged();
    }

    public interface onAlarmClockCheckChanged {
        void onCheckedChanged(int position, boolean isChecked);
    }

}
