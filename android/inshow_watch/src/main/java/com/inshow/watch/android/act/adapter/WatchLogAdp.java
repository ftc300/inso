package com.inshow.watch.android.act.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.model.DebugLogEntity;
import com.inshow.watch.android.tools.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/9/29
 * @ 描述:
 */


public class WatchLogAdp extends BaseAdapter {

    private Context mContext;
    private List<DebugLogEntity> data = new ArrayList<>();
    private ITextViewClick listener;

    public void setListener(ITextViewClick listener) {
        this.listener = listener;
    }

    public WatchLogAdp(Context mContext, List<DebugLogEntity> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public DebugLogEntity getItem(int position) {
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
            view = LayoutInflater.from(mContext).inflate(R.layout.watch_debug_log_item, null);
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
        final DebugLogEntity entity = getItem(position);
        viewHolder.tv00.setText(String.valueOf(entity.eventID));
        viewHolder.tv01.setText(TimeUtil.convertLongTimeToDate((TimeUtil.getWatchSysStartTimeSecs() + entity.modifyTime)));
        viewHolder.tv02.setText(String.valueOf(entity.argmentOne));
        viewHolder.tv03.setText(String.valueOf(entity.argmentTwo));

        viewHolder.tv00.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=listener) listener.onClick1(viewHolder.tv00,getEventIdMeanings(entity.eventID));
            }
        });
        viewHolder.tv01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        viewHolder.tv02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=listener) listener.onClick3(viewHolder.tv02,getArgment(entity.eventID));

            }
        });
        viewHolder.tv03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=listener) listener.onClick4(viewHolder.tv03,getArgment2());
            }
        });
        return view;
    }

    static class ViewHolder {
        TextView tv00;
        TextView tv01;
        TextView tv02;
        TextView tv03;
        View line;
    }


    public interface  ITextViewClick{
        void onClick1(View v,String i);
        void onClick2(View v,String i);
        void onClick3(View v,String i);
        void onClick4(View v,String i);
    }


    /**
     *  EventID
     * @param i
     * @return
     */
    private String  getEventIdMeanings(int i){
        switch (i){
            case 0:
                return "空";
            case 1:
                return  "开机";
            case 2:
                return  "UICR ADJUST";
            case 3:
                return  "文件系统初始化失败";
            case 4:
                return  "文件系统写失败";
            case 5:
                return  "文件系统读失败";
            case 6:
                return  "文件系统更新失败";
            case 7:
                return  "文件系统删除失败";
            case 8:
                return  "硬件初始化失败";
            case 9:
                return  "开盖";
            case 10:
                return "合盖";
            case 11:
                return "电池采样";
            case 12:
                return "低电";
            case 13:
                return "长按键8s";
            case 14:
                return "长按键<12s,恢复走针";
            case 15:
                return "UICR BIAS";
            case 16:
                return "soft reset error code";
            case 17:
                return  "soft reset line num";
            case 18:
                return  "soft reset file name";
            case 19:
                return  "Connect Parameters error";
            default:
                return String.valueOf(i);
        }

    }


    private String getArgment(int i0 ){
//        参数：（1开机：开机原因；2UICR：补偿值；11电池采样：电压值 16 错误码；17 行号；18 文件名前四个字母的ASCII码；19 出错行号）
        if(i0==1){
            return "开机：开机原因";
        }else if(i0==2){
            return "UICR：补偿值";
        }else  if(i0==11){
            return "电池采样：电压值";
        }else if(i0==16){
            return "错误码";
        }else if(i0==17){
            return "行号";
        } else if(i0==18){
            return "文件名前四个字母的ASCII码";
        }else if(i0==19){
            return "出错行号";
        }
        return  "我也不知道是啥";
    }


    private String getArgment2( ){
         return "参数Ⅰ小于0时为1，否则为0";
    }




}
