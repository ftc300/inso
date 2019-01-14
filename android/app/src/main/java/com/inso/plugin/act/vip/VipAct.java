package com.inso.plugin.act.vip;

import android.content.DialogInterface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.inso.R;
import com.inso.plugin.adapter.VipListAdp;
import com.inso.plugin.basic.BasicListAct;
import com.inso.plugin.event.ChangeUI;
import com.inso.plugin.model.VipEntity;
import com.inso.plugin.tools.CheckPermission;
import com.inso.plugin.tools.L;
import com.inso.plugin.tools.Rom;
import com.inso.plugin.tools.TimeUtil;
import com.inso.plugin.tools.ToastUtil;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import static com.inso.plugin.event.ChangeUI.RENDER_AGAIN;
import static com.inso.plugin.tools.Constants.TimeStamp.VIP_KEY;

/**
 * Created by chendong on 2017/1/22.
 * vip主页面
 */
public class VipAct extends BasicListAct implements View.OnClickListener {

    private VipListAdp adp;
    private ListView listView;
    private FrameLayout flDelete, flSelectAll;
    private Button btnAdd, btnCancel, btnSelectAll;
    private TextView selectAllTitle, tvAlarmDelete;
    private int btnAllClickNum;
    private List<VipEntity> dataSource;
    private View footerView;
    private TextView footerTextView;

    @Override
    protected String getEmptyString() {
        return getString(R.string.vip_empty_tip);
    }

    @Override
    protected int getContentRes() {
        return R.layout.watch_activity_alarm_clock;
    }

    @Override
    protected void initViewOrData() {
        if (!Rom.isMIUI()) {
            new MLAlertDialog.Builder(mContext)
                    .setCancelable(false)
                    .setMessage(getString(R.string.sorry_not_support_vip))
                    .setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();
        }
        setBtnOnBackPress();
        setTitleText(getString(R.string.vip_alert));
        btnAllClickNum = 0;
        flSelectAll = (FrameLayout) findViewById(R.id.select_all_title_bar);
        tvAlarmDelete = (TextView) findViewById(R.id.tv_delete);
        listView = (ListView) findViewById(R.id.listView);
        flDelete = (FrameLayout) findViewById(R.id.flAlarmDelete);
        btnAdd = (Button) findViewById(R.id.add);
        selectAllTitle = (TextView) findViewById(R.id.select_all_title);
        btnCancel = (Button) findViewById(R.id.select_all_cancel);
        btnSelectAll = (Button) findViewById(R.id.select_all_select);
        selectAllTitle.setText(getString(R.string.please_select_item));
//        footerView = getLayoutInflater().inflate(R.layout.watch_list_footer, null);
//        footerTextView = (TextView) footerView.findViewById(R.id.tv_empty);
//        footerTextView.setVisibility(View.GONE);
        adp = new VipListAdp(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == adp.getCount()) return;
                View v = listView.getChildAt(i);
                CheckBox checkBox = ((CheckBox) v.findViewById(R.id.switchButton));
                checkBox.setChecked(!checkBox.isChecked());
                updateIfCheckedChanged(i, checkBox.isChecked());
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == adp.getCount()) return true;
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
        adp.setAlarmClockCheckChangedListener(new VipListAdp.onAlarmClockCheckChanged() {
            @Override
            public void onCheckedChanged(int position, boolean isChecked) {
                updateIfCheckedChanged(position, isChecked);
            }
        });
    }

    /**
     * 开关时 更新数据库和写设备
     */
    private void updateIfCheckedChanged(int position, boolean isChecked) {
        needPush = true;
        VipEntity entity = adp.getItem(position);
        mDBHelper.updateVip(new VipEntity(entity.contactId, entity.id, entity.number, entity.name, isChecked));
//        XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_VIP), setVip(entity.id, isChecked ? 2 : 1, isChecked ? entity.name.getBytes(Charset.forName("UTF-8")) : new byte[18]), new Response.BleWriteResponse() {
//            @Override
//            public void onResponse(int code, Void data) {
//
//            }
//        });
        EventBus.getDefault().post(new ChangeUI(RENDER_AGAIN));
    }

    @Override
    public void onResume() {
        super.onResume();
        dataSource = mDBHelper.getVipContact();
        adp.setData(dataSource);
        listView.setAdapter(adp);
        if (dataSource.size() > 0) {
//            LowPowerManager.getInstance().tipOnlyOnce(mContext);
        }
    }

    /**
     * 删除的弹出显示
     */
    private void setOperateDelete() {
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
        } else if (id == R.id.add) {
//            if (XmPluginHostApi.instance().getApiLevel() >= 75) {
//                try {
//                    XmPluginHostApi.instance().checkAndRequestPermisson(activity(), true, new Callback<List<String>>() {
//                        @Override
//                        public void onSuccess(List<String> strings) {
//                            L.e("checkAndRequestPermission success");
//                            if (mDBHelper.getVipContact().size() < 10) {
//                                needPush = true;
//                                L.e("VipAct:switchTo(PickVipContactAct)");
//                                switchTo(PickVipContactAct.class);
//                            } else {
//                                ToastUtil.showToastNoRepeat(mContext, getString(R.string.contact_most_tip));
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(int i, String s) {
//                            L.e("checkAndRequestPermisson fail");
//                        }
//                    }, READ_CONTACTS);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    customRequestPermission();
//                }
//            } else {
//                customRequestPermission();
//            }

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
            needPush = true;
            Map<Integer, Boolean> isCheck_delete = adp.getMap();
            int count = adp.getCount();
            for (int i = 0; i < count; i++) {
                int position = i - (count - adp.getCount());
                if (isCheck_delete.get(i) != null && isCheck_delete.get(i)) {
                    //清除
//                    XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_VIP), setVip(adp.getItem(position).id, 1, new byte[18]), new Response.BleWriteResponse() {
//                        @Override
//                        public void onResponse(int code, Void data) {
//
//                        }
//                    });
                    if (mDBHelper.deleteVipContact(adp.getItem(position))) {
                        dataSource = mDBHelper.getVipContact();
                        adp.setData(dataSource);
                        adp.notifyDataSetChanged();
                    }
                }
            }
            btnAdd.setEnabled(adp.getCount() < 10);
            cancelOperateDelete();
            EventBus.getDefault().post(new ChangeUI(RENDER_AGAIN));//删除后要更新长连接状态
        }
    }

    private void customRequestPermission() {
        L.e("checkAndRequestPermission success");
        if (mDBHelper.getVipContact().size() < 10) {
            needPush = true;

            if (CheckPermission.checkContact(mContext)) {
                L.e("VipAct:switchTo(PickVipContactAct)");
                switchTo(PickVipContactAct.class);
            } else {
                new MLAlertDialog.Builder(mContext)
                        .setCancelable(false)
                        .setMessage(getString(R.string.no_contact_permission))
                        .setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();
            }
        } else {
            ToastUtil.showToastNoRepeat(mContext, getString(R.string.contact_most_tip));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        if (needPush && mBackFlag) {
            mDBHelper.updateTimeStamp(VIP_KEY, TimeUtil.getNowTimeSeconds());
            pushVipInfoToMijia();
        }
        super.onPause();
    }

    /**
     * 上传Vip信息
     */
    private void pushVipInfoToMijia() {
//        List<HttpVip> list = new ArrayList<>();
//        List<VipEntity> mDbSource = mDBHelper.getVipContact();
//        for (VipEntity item : mDbSource) {
//            list.add(new HttpVip(item.id, item.name, item.number, item.status ? ON : OFF));
//        }
//        HttpSyncHelper.pushData(
//                new RequestParams(
//                        MODEL,
//                        UID,
//                        DID,
//                        TYPE_USER_INFO,
//                        VIP_KEY,
//                        AppController.getGson().toJson(list),
//                        mSyncHelper.getLocalVipTime()), new Callback<JSONArray>() {
//                    @Override
//                    public void onSuccess(JSONArray jsonArray) {
//                        L.e("pushVipInfoToMijia:" + jsonArray.toString());
//                    }
//
//                    @Override
//                    public void onFailure(int i, String s) {
//                        L.e("pushVipInfoToMijia onFailure:" + s);
//                    }
//                });
    }

//    private void testAddContact(){
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                L.e("testAddContact: start");
//                for(int i = 0;i<2000 ;i++){
//                    MessUtil.AddContact(mContext,"testName"+i,"18795958323");
//                    if(i== 1999) L.e("testAddContact: end");
//                }
//            }
//        },0);
//    }

}
