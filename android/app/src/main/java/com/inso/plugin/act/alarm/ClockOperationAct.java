package com.inso.plugin.act.alarm;

import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.inso.R;
import com.inso.plugin.basic.BasicAct;
import com.inso.plugin.event.AlarmClockBus;
import com.inso.plugin.event.AlarmClockOperateBus;
import com.inso.plugin.tools.Configuration;
import com.inso.plugin.tools.L;
import com.inso.plugin.tools.TimeUtil;
import com.inso.plugin.view.WatchTimePicker;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;
import com.xiaomi.smarthome.common.ui.widget.SettingsItemView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by chendong on 2017/1/22.
 * 设置闹钟
 * Tips：
 * 港台地区不提供工作日/节假日闹钟
 * 现在保存数据是还将港台的数组转换成大陆的保存
 * 如果解析数据时有误就丢弃（如港台出现2,3）
 */
public class ClockOperationAct extends BasicAct {

    private SettingsItemView repeatType;
    private MLAlertDialog.Builder singleChoiceBuilder;
    private MLAlertDialog.Builder MultiChoiceBuilder;
    private int singleCheckedItemPos = 0;
    private boolean[] checkedItems;
    private Button buttonOK;
    private TextView selectAllTitle;
    private FrameLayout flSelectAll;
    private WatchTimePicker timePicker;
    private StringBuffer strRepeatType;
    private int mID;
    private String[] RepeatTypeSource;
    private String[] WeekSource;
    private String[] SimpleWeekSource;
    private AlarmClockOperateBus mBus;
    private EditText editText;
    private int INDEX_OF_CUSTOM;// 自定义选项

    @Override
    protected int getContentRes() {
        return R.layout.watch_activity_alarm_operation;
    }

    @Override
    protected void initViewOrData() {
        setBtnOnBackPress(R.id.select_all_cancel);
        setActStyle(ActStyle.BT);
        mID = 1;
        flSelectAll = (FrameLayout) findViewById(R.id.select_all_title_bar);
        mTitleView.setVisibility(View.GONE);
        flSelectAll.setVisibility(View.VISIBLE);
        Configuration.ServerHandle(new Configuration.ServerHandler() {
            @Override
            public String defaultServer() {
                INDEX_OF_CUSTOM = 3;
                RepeatTypeSource = getResources().getStringArray(R.array.normal_repeat_type);
                return null;
            }

            @Override
            public String cnServer() {
                INDEX_OF_CUSTOM = 5;
                RepeatTypeSource = getResources().getStringArray(R.array.repeattype);
                return null;
            }

            @Override
            public String twServer() {
                INDEX_OF_CUSTOM = 3;
                RepeatTypeSource = getResources().getStringArray(R.array.normal_repeat_type);
                return null;
            }

            @Override
            public String hkServer() {
                INDEX_OF_CUSTOM = 3;
                RepeatTypeSource = getResources().getStringArray(R.array.normal_repeat_type);
                return null;
            }
        });

        WeekSource = getResources().getStringArray(R.array.week);
        SimpleWeekSource = getResources().getStringArray(R.array.simple_week);
        buttonOK = (Button) findViewById(R.id.select_all_select);
        selectAllTitle = (TextView) findViewById(R.id.select_all_title);
        timePicker = (WatchTimePicker) findViewById(R.id.timePicker);
//        timePicker.set
        repeatType = (SettingsItemView) findViewById(R.id.repeatType);
        editText = (EditText) findViewById(R.id.editText);
        singleChoiceBuilder = new MLAlertDialog.Builder(ClockOperationAct.this);
        MultiChoiceBuilder = new MLAlertDialog.Builder(ClockOperationAct.this);
        checkedItems = new boolean[WeekSource.length];
        final int checkLength = checkedItems.length;
        for (int i = 0; i < checkLength; i++) {
            checkedItems[i] = false;
        }
        buttonOK.setText(R.string.button_ok);
        selectAllTitle.setText(R.string.title_setalarm);
        repeatType.setSubTitle(RepeatTypeSource[0]);
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mBus) {
                    if (mBus.isAdd) {//新增就得重新生成ID
                        List<Integer> AlarmIDSource = getAlarmIDs();
                        List<Integer> tableSource = mDBHelper.getAllAlarmID();
                        if (tableSource != null) {
                            for (int i = 0; i < AlarmIDSource.size(); i++) {
                                for (int j = 0; j < tableSource.size(); j++) {
                                    if (AlarmIDSource.get(i) == tableSource.get(j)) {
                                        AlarmIDSource.remove(i);
                                    }
                                }
                            }
                        }
                        if (AlarmIDSource.size() > 0) {
                            mID = AlarmIDSource.get(0);
                        }
                    } else {
                        mID = mBus.id;
                    }
                    AlarmClockBus bus = new AlarmClockBus(mBus.isAdd, mID, getSeconds(), getRepeatString(), true, editText.getText().toString());
                    L.e(bus.toString());
                    EventBus.getDefault().post(bus);
                } else {
//                    ToastUtil.showToastNoRepeat(mContext,"操作失败，请稍后重试！");
                }
                finish();
            }
        });

        repeatType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleChoiceBuilder
                        .setCancelable(false)
                        .setSingleChoiceItems(RepeatTypeSource, singleCheckedItemPos, new MLAlertDialog.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                singleCheckedItemPos = which;
                                //
                                if (which == INDEX_OF_CUSTOM) {
                                    MultiChoiceBuilder.setCancelable(false)
                                            .setTitle(getString(R.string.button_repeat)).setMultiChoiceItems(WeekSource, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                            checkedItems[i] = b;
                                        }
                                    })
                                            .setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int which) {
                                                    strRepeatType = new StringBuffer();
                                                    for (int i = 0; i < checkLength; i++) {
                                                        if (checkedItems[i]) {
                                                            strRepeatType.append(SimpleWeekSource[i]).append(" ");
                                                        }
                                                    }
                                                    repeatType.setSubTitle(strRepeatType.toString());
                                                    if (isAllUnSelected()) {
                                                        singleCheckedItemPos = 0;
                                                        repeatType.setSubTitle(RepeatTypeSource[0]);
                                                    }

                                                }
                                            })
                                            .setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    singleCheckedItemPos = 0;
                                                }
                                            })
                                            .show();
                                }
                                if (singleCheckedItemPos != INDEX_OF_CUSTOM) {
                                    repeatType.setSubTitle(RepeatTypeSource[singleCheckedItemPos]);
                                }
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        // FIXME: 2017/8/15 取消的时候不用设置
//        MultiChoiceBuilder.setDismissCallBack(new MLAlertDialog.DismissCallBack() {
//
//            @Override
//            public void beforeDismissCallBack() {
//
//            }
//
//            @Override
//            public void afterDismissCallBack() {
//                strRepeatType = new StringBuffer();
//                for(int i=0;i<checkLength;i++)
//                {
//                    if(checkedItems[i])
//                    {
//                        strRepeatType.append(SimpleWeekSource[i]).append(" ");
//                    }
//                }
//                repeatType.setSubTitle(strRepeatType.toString());
//                if(isAllUnSelected()) {
//                    singleCheckedItemPos = 0 ;
//                    repeatType.setSubTitle(RepeatTypeSource[0]);
//                }
//
//            }
//        });
        initPicker();
        L.e("initFinish INDEX_OF_CUSTOM: " + INDEX_OF_CUSTOM);
    }

    public void initPicker() {
        AlarmClockOperateBus event = getIntent().getParcelableExtra("alarm");
        mBus = event;
        L.e("getParcelableExtra:" + event.toString());
        if (!mBus.isAdd) {
            timePicker.setCurrentHour(event.seconds / 3600);
            timePicker.setCurrentMinute((event.seconds % 3600) / 60);
            timePicker.setIs24HourView(false);
            final String strRepeatType = mBus.repeatType;
            repeatType.setSubTitle(AlarmHelper.getDisplayRepeatType(mContext, strRepeatType));
            editText.setText(mDBHelper.getAlarmDescByID(mBus.id));
            if (mBus.repeatType.length() == 1) {
                final int localInt = Integer.parseInt(strRepeatType);
                L.e("localInt:" + localInt);
                Configuration.ServerHandle(new Configuration.ServerHandler() {
                    @Override
                    public String defaultServer() {
                        if (localInt == 2 || localInt == 3) {
                            L.e("非大陆地区不提供节假日闹钟功能");
                        } else {
                            singleCheckedItemPos = localInt == 4 ? 2 : singleCheckedItemPos;
                        }
                        return null;
                    }

                    @Override
                    public String cnServer() {
                        singleCheckedItemPos = localInt;
                        return null;
                    }

                    @Override
                    public String twServer() {
                        if (localInt == 2 || localInt == 3) {
                            L.e("非大陆地区不提供节假日闹钟功能");
                        } else {
                            singleCheckedItemPos = localInt == 4 ? 2 : singleCheckedItemPos;
                        }
                        return null;
                    }

                    @Override
                    public String hkServer() {
                        if (localInt == 2 || localInt == 3) {
                            L.e("非大陆地区不提供节假日闹钟功能");
                        } else {
                            singleCheckedItemPos = localInt == 4 ? 2 : singleCheckedItemPos;
                        }
                        return null;
                    }
                });
            } else {
                singleCheckedItemPos = INDEX_OF_CUSTOM;
                String[] arrRepeatType = strRepeatType.split(",");
                for (int i = 1; i < arrRepeatType.length; i++) {
                    checkedItems[Integer.parseInt(arrRepeatType[i])] = true;
                }
            }
        } else {//新增使用手表时间
            int[] time = TimeUtil.getWatchTime(mDBHelper.getSettingZone(), Calendar.HOUR_OF_DAY);
            timePicker.setCurrentHour(time[0]);
            timePicker.setCurrentMinute(time[1]);
            timePicker.setIs24HourView(false);
        }
    }
//
//    @Subscribe
//    public void onEventMainThread(AlarmClockOperateBus event) {
//        L.e("onEventMainThread INDEX_OF_CUSTOM:" + INDEX_OF_CUSTOM);
//        mBus = event;
//        if (!mBus.isAdd) {
//            timePicker.setCurrentHour(event.seconds / 3600);
//            timePicker.setCurrentMinute((event.seconds % 3600) / 60);
//            timePicker.setIs24HourView(false);
//            final String strRepeatType = mBus.repeatType;
//            repeatType.setSubTitle(AlarmHelper.getDisplayRepeatType(mContext, strRepeatType));
//            editText.setText(mDBHelper.getAlarmDescByID(mBus.id));
//            if (mBus.repeatType.length() == 1) {
//                final int localInt = Integer.parseInt(strRepeatType);
//                Configuration.ServerHandle(new Configuration.ServerHandler() {
//                    @Override
//                    public String defaultServer() {
//                        if(localInt==2|| localInt ==3 ){
//                            L.e("非大陆地区不提供节假日闹钟功能");
//                        }else {
//                            singleCheckedItemPos = localInt == 4 ? singleCheckedItemPos : singleCheckedItemPos - 2;
//                        }
//                        return null;
//                    }
//
//                    @Override
//                    public String cnServer() {
//                        singleCheckedItemPos = localInt;
//                        return null;
//                    }
//
//                    @Override
//                    public String twServer() {
//                        if(localInt==2|| localInt ==3 ){
//                            L.e("非大陆地区不提供节假日闹钟功能");
//                        }else {
//                            singleCheckedItemPos = localInt == 4 ? singleCheckedItemPos : singleCheckedItemPos - 2;
//                        }
//                        return null;
//                    }
//                });
//            } else {
//                singleCheckedItemPos = INDEX_OF_CUSTOM;
//                String[] arrRepeatType = strRepeatType.split(",");
//                for (int i = 1; i < arrRepeatType.length; i++) {
//                    checkedItems[Integer.parseInt(arrRepeatType[i])] = true;
//                }
//            }
//        } else {//新增使用手表时间
//            int[] time = TimeUtil.getWatchTime(mDBHelper.getSettingZone(), Calendar.HOUR_OF_DAY);
//            timePicker.setCurrentHour(time[0]);
//            timePicker.setCurrentMinute(time[1]);
//            timePicker.setIs24HourView(false);
//        }
//    }

    @Override
    protected ActStyle getActStyle() {
        return ActStyle.GT;
    }

    /**
     * 时间
     *
     * @return
     */
    private int getSeconds() {
        return timePicker.getCurrentHour() * 3600 + timePicker.getCurrentMinute() * 60;
    }

    /**
     * 获取重复方式 入库之用
     * 如2
     * 5,0,1
     *
     * @return
     */
    private String getRepeatString() {
        L.e("singleCheckedItemPos  ：" + singleCheckedItemPos);
        if (singleCheckedItemPos != INDEX_OF_CUSTOM) {
            final String ret = String.valueOf(singleCheckedItemPos);
            return Configuration.ServerHandle(new Configuration.ServerHandler() {
                @Override
                public String defaultServer() {
                    if (singleCheckedItemPos == 2) {
                        return "4";
                    }
                    return ret;
                }

                @Override
                public String cnServer() {
                    return ret;
                }

                @Override
                public String twServer() {
                    if (singleCheckedItemPos == 2) {
                        return "4";
                    }
                    return ret;
                }

                @Override
                public String hkServer() {
                    if (singleCheckedItemPos == 2) {
                        return "4";
                    }
                    return ret;
                }


            });
        }
        StringBuffer ret = new StringBuffer();
        ret.append("5").append(",");
        for (int i = 0; i < checkedItems.length; i++) {
            if (checkedItems[i]) {
                ret.append(i);
                if (i != checkedItems.length - 1) {
                    ret.append(",");
                }
            }
        }
        return ret.toString();
    }

    /**
     * 判断自定义是否一天也没有选
     *
     * @return
     */
    private boolean isAllUnSelected() {
        for (int i = 0; i < checkedItems.length; i++) {
            if (checkedItems[i])
                return false;//一旦有选择的就返回false
        }
        return true;
    }

    /**
     * 初始化原始10个闹钟
     *
     * @return
     */
    private List<Integer> getAlarmIDs() {
        List<Integer> AlarmIDSource = new ArrayList<>();
        for (int i = 1; i < 11; i++) {//1到10号闹钟
            AlarmIDSource.add(i);
        }
        return AlarmIDSource;
    }
}
