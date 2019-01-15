package com.inso.plugin.act.alarm;

import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.inso.R;
import com.inso.core.XmBluetoothManager;
import com.inso.plugin.adapter.AlarmClockListAdp;
import com.inso.plugin.basic.BasicListAct;
import com.inso.plugin.dao.AlarmDao;
import com.inso.plugin.event.AlarmClockBus;
import com.inso.plugin.event.AlarmClockOperateBus;
import com.inso.plugin.tools.L;
import com.inso.plugin.tools.MessUtil;
import com.inso.plugin.tools.TimeUtil;
import com.inso.plugin.tools.ToastUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.inso.plugin.act.alarm.AlarmHelper.getRepeatWriteBytes;
import static com.inso.plugin.manager.BleManager.setAlarm;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.CHARACTERISTIC_ALARM_CLOCK;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;

/**
 * Created by chendong on 2017/1/22.
 * 闹钟主页面
 */
public class AlarmClockAct extends BasicListAct implements View.OnClickListener,IOnceAlarm {

    private AlarmClockListAdp adp;
    private ListView listView;
    private FrameLayout flDelete, flSelectAll;
    private Button btnAdd, btnCancel, btnSelectAll;
    private TextView selectAllTitle, tvAlarmDelete;
    private int btnAllClickNum;
    private int[] intArray = new int[9];
    private List<AlarmDao> dataSource;
    private AlarmDao clickItem;
    private View footerView;
    private Timer timer;
    private boolean intoSelectModeFlag = false;

    @Override
    protected String getEmptyString() {
        return getString(R.string.alarm_empty_tip);
    }

    @Override
    protected int getContentRes() {
        return R.layout.watch_activity_alarm_clock;
    }

    @Override
    protected void initViewOrData() {
        setBtnOnBackPress();
        setTitleText(getString(R.string.alarm));
        btnAllClickNum = 0;
        flSelectAll = (FrameLayout) findViewById(R.id.select_all_title_bar);
        tvAlarmDelete = (TextView) findViewById(R.id.tv_delete);
        listView = (ListView) findViewById(R.id.listView);
        flDelete = (FrameLayout) findViewById(R.id.flAlarmDelete);
        btnAdd = (Button) findViewById(R.id.add);
        selectAllTitle = (TextView) findViewById(R.id.select_all_title);
        btnCancel = (Button) findViewById(R.id.select_all_cancel);
        btnSelectAll = (Button) findViewById(R.id.select_all_select);
        footerView = getLayoutInflater().inflate(R.layout.watch_list_footer, null);
        selectAllTitle.setText(R.string.please_select_item);
        renderListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                needPush = true;
                clickItem = adp.getItem(i);
//                Intent it = new Intent();
//                it.putExtra("alarm", new AlarmClockOperateBus(false, clickItem.id, clickItem.seconds, clickItem.repeatType, clickItem.status));
//                startActivity(it, ClockOperationAct.class.getName());
             Intent it = new Intent(mContext,ClockOperationAct.class);
             it.putExtra("alarm", new AlarmClockOperateBus(false, clickItem.id, clickItem.seconds, clickItem.repeatType, clickItem.status));
             startActivity(it);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                needPush = true;
                adp.setCheck(false);
                adp.getMap().put(i, true);
                setOperateDelete();
                return true;
            }
        });
        setClickListeners();
    }

    private void setClickListeners() {
        tvAlarmDelete.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSelectAll.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
//        btnAdd.setEnabled(dataSource.size()<10);
        if (dataSource.size() > 0) {
//            LowPowerManager.getInstance().tipOnlyOnce(mContext);
        }
    }

    @Override
    public void onDestroy() {
        gcTimer();
        super.onDestroy();
    }

    @Subscribe
    public void onEventMainThread(AlarmClockBus event) {
        L.e("onEventMainThread 000:" + event.toString());
        needPush = true;
        AlarmDao item = null;
        int onceNextAlarmTimeStamp = TimeUtil.getNowTimeSeconds(mDBHelper.getSettingZone()) + AlarmHelper.getNextAlarmRestSeconds(mDBHelper, event.repeatType, mDBHelper.getSettingZone(), event.seconds);
        //sqlite 操作成功载更新UI
        L.e("onceNextAlarmTimeStamp:"+ onceNextAlarmTimeStamp);
        if (event.repeatType.length() == 1 && Integer.parseInt(event.repeatType) == 0) {
            item = new AlarmDao(event.id, event.seconds, onceNextAlarmTimeStamp, event.repeatType, event.isOn, event.desc);
        } else {
            item = new AlarmDao(event.id, event.seconds, TimeUtil.getTodayDeltaSeconds(mDBHelper.getSettingZone()), event.repeatType, event.isOn, event.desc);
        }
        L.e("onEventMainThread 001");
        if (event.isAdd) {//add -> insert
            if (mDBHelper.addAlarmClock(item)) {
                renderListView();
            }
        } else {//update
            if (mDBHelper.updateAlarmClock(item)) {
                L.e("onEventMainThread 002");
                renderListView();
                L.e("onEventMainThread 003");
            }
        }
        //设置相关
        setBTArg(event.id, 2, 0);
        XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_ALARM_CLOCK), setAlarm(intArray, event.seconds, getRepeatWriteBytes(event.repeatType)));
    }

    /**
     * 删除的弹出显示
     */
    private void setOperateDelete() {
        intoSelectModeFlag = true;
        Animation inAnimaInTop = AnimationUtils.loadAnimation(this, R.anim.slide_in_top);
        Animation inAnimaInB = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
        btnAdd.setVisibility(View.GONE);
        flDelete.setVisibility(View.VISIBLE);
        flDelete.startAnimation(inAnimaInB);
        flSelectAll.setVisibility(View.VISIBLE);
        mTitleView.setVisibility(View.INVISIBLE);
        flSelectAll.startAnimation(inAnimaInTop);
        adp.setSelectState();
    }

    /**
     * 隐藏删除弹窗
     */
    private void cancelOperateDelete() {
        intoSelectModeFlag = false;
        Animation outAnimaTop = AnimationUtils.loadAnimation(this, R.anim.slide_out_top);
        Animation outAnimaB = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);
        btnAdd.setVisibility(View.VISIBLE);
        flDelete.setAnimation(outAnimaB);
        flDelete.setVisibility(View.GONE);
        flSelectAll.startAnimation(outAnimaTop);
        flSelectAll.setVisibility(View.GONE);
        mTitleView.setVisibility(View.VISIBLE);
        adp.setCommonState();
        adp.setCheck(false);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.select_all_cancel) {
            cancelOperateDelete();
            renderListView();
        } else if (id == R.id.add) {
            if (mDBHelper.getAllAlarm().size() < 10) {
//                Intent i = new Intent();
//                i.putExtra("alarm", new AlarmClockOperateBus(true));
//                startActivity(i, ClockOperationAct.class.getName());
                Intent i = new Intent(mContext,ClockOperationAct.class);
                i.putExtra("alarm", new AlarmClockOperateBus(true));
                startActivity(i);
            } else {
                ToastUtil.showToastNoRepeat(mContext, getString(R.string.alarm_most_tip));
            }
        } else if (id == R.id.select_all_select) {
            if (btnAllClickNum % 2 == 0) {
                adp.setCheck(true);
                btnSelectAll.setText(getString(R.string.watch_unselect_all));
            } else {
                adp.setCheck(false);
                btnSelectAll.setText(getString(R.string.watch_select_all));
            }
            btnAllClickNum++;
        } else if (id == R.id.tv_delete) {
            btnAllClickNum = 0;
            needPush = true;
            Map<Integer, Boolean> isCheck_delete = adp.getMap();
            int count = adp.getCount();
            for (int i = 0; i < count; i++) {
                int position = i - (count - adp.getCount());
                if (isCheck_delete.get(i) != null && isCheck_delete.get(i)) {
                    //清除
                    setBTArg(adp.getItem(position).id, 1);
                    XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_ALARM_CLOCK), setAlarm(intArray, 0, new byte[2]));
                    if (mDBHelper.deleteAlarmByID(adp.getItem(position).id)) {
                        renderListView();
                    }
                }
            }
            btnAdd.setEnabled(true);
            cancelOperateDelete();
        }
    }

    @Override
    public void onPause() {
        if (needPush && mBackFlag) {
            L.e("onPause : pushAlarmInfoToMijia");
            pushAlarmInfoToMijia();
        }
        super.onPause();
    }


    /**
     * 上传闹钟信息
     */
    private void pushAlarmInfoToMijia() {
        L.e("AlarmClockAct pushAlarmInfoToMijia");
//        mDBHelper.updateTimeStamp(NORMAL_ALARM_KEY, TimeUtil.getNowTimeSeconds());
//        List<HttpAlarm> list = new ArrayList<>();
//        List<AlarmDao> mDbSource = mDBHelper.getAllAlarm();
//        for (AlarmDao item : mDbSource) {
//            list.add(new HttpAlarm(item.id, item.status ? ON : OFF, item.seconds, item.extend, getListType(item.repeatType), item.desc));
//        }
//        HttpSyncHelper.pushData(
//                new RequestParams(
//                        MODEL,
//                        UID,
//                        DID,
//                        TYPE_USER_INFO,
//                        NORMAL_ALARM_KEY,
//                        AppController.getGson().toJson(list),
//                        mSyncHelper.getLocalAlarmKeyTime()), new Callback<JSONArray>() {
//                    @Override
//                    public void onSuccess(JSONArray jsonArray) {
//                        L.e("pushAlarmInfoToMijia:" + jsonArray.toString());
//                        needPush = false;
//                    }
//
//                    @Override
//                    public void onFailure(int i, String s) {
//                        L.e("pushAlarmInfoToMijiaError:" + s);
//                    }
//                });
    }


    /**
     * 设置蓝牙（BT）请求参数
     * if 0000 setBTArg（）
     * if 001 setBTArg（0,0,1）
     *
     * @param arg
     */
    private void setBTArg(int... arg) {
        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = 0;
        }
        try {
            for (int i = 0; i < intArray.length; i++) {
                intArray[i] = arg[i];
            }
        } catch (Exception e) {

        }
    }

    private void renderListView() {
        dataSource = MessUtil.checkOnceAlarm(mDBHelper,AlarmClockAct.this);
        adp = new AlarmClockListAdp(this, mDBHelper);
        adp.setData(dataSource);
        listView.setAdapter(adp);
        adp.setAlarmClockCheckChangedListener(new AlarmClockListAdp.onAlarmClockCheckChanged() {
            @Override
            public void onCheckedChanged(int position, boolean isChecked) {
                needPush = true;
                AlarmDao item = adp.getItem(position);
                AlarmDao insertItem = null;
                int onceNextAlarmTimeStamp = TimeUtil.getNowTimeSeconds(mDBHelper.getSettingZone()) + AlarmHelper.getNextAlarmRestSeconds(mDBHelper, item.repeatType, mDBHelper.getSettingZone(), item.seconds);
                //sqlite 操作成功载更新UI
                if (item.repeatType.length() == 1 && Integer.parseInt(item.repeatType) == 0) {
                    insertItem = new AlarmDao(item.id, item.seconds, onceNextAlarmTimeStamp, item.repeatType, isChecked, item.desc);
                } else {
                    insertItem = new AlarmDao(item.id, item.seconds, TimeUtil.getTodayDeltaSeconds(mDBHelper.getSettingZone()), item.repeatType, isChecked, item.desc);
                }
                if (mDBHelper.updateAlarmClock(insertItem)) {
                    setBTArg(insertItem.id, isChecked ? 4 : 3);
                    XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_ALARM_CLOCK), setAlarm(intArray, 0, new byte[2]));
                }
            }
        });
        startTimer();
    }


    private void startTimer(){
        if (null == timer) {
            timer = new Timer();
            invalidateList();
        }
    }

    private void gcTimer(){
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    private void invalidateList() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        L.e("invalidateList");
                        if (!intoSelectModeFlag && (System.currentTimeMillis() / 1000) % 60 == 1) {//60秒更新一次
                            L.e("invalidateList System.currentTimeMillis() / 1000) % 60 == 0");
                            dataSource = MessUtil.checkOnceAlarm(mDBHelper, AlarmClockAct.this);
                            adp.notifyDataChanged(dataSource);
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    @Override
    public void onStateChanged() {
        pushAlarmInfoToMijia();
    }
}
