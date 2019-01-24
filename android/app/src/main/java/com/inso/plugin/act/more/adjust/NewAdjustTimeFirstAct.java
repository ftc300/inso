package com.inso.plugin.act.more.adjust;

import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.inso.R;
import com.inso.core.BleMgr;
import com.inso.plugin.basic.BasicSingleButtonAct;
import com.inso.plugin.event.AdjustTimeBus;
import com.inso.plugin.sync.SyncDeviceHelper;
import com.inso.plugin.tools.L;
import com.inso.plugin.tools.TimeUtil;
import com.inso.plugin.view.LabelTextRow;
import com.inso.plugin.view.WatchNumberPicker;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;

import org.greenrobot.eventbus.Subscribe;

import java.util.UUID;

import static com.inso.plugin.manager.BleManager.I2B_WatchTime;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.CHARACTERISTIC_SYNC_WATCH_TIME;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;


/**
 * Created by chendong on 2018/7/27.
 */

public class NewAdjustTimeFirstAct extends BasicSingleButtonAct {
    private LabelTextRow minLabelTextRow;
    private LabelTextRow hourLabelTextRow;
    private TextView tvAccurate;
    private int selectH = -1;
    private int selectM = -1;
    private int settingTime;
    private void pushAdjustTime( long serverTime, long pointTime) {
//        HttpAdjustTime bean = new HttpAdjustTime(serverTime, pointTime);
//        HttpSyncHelper.pushData(new RequestParams(
//                MODEL,
//                UID,
//                DID,
//                Constants.HttpConstant.TYPE_USER_INFO,
//                POINTER_ADJUST,
//                AppController.getGson().toJson(bean),
//                TimeUtil.getNowTimeSeconds()
//        ), new Callback<JSONArray>() {
//            @Override
//            public void onSuccess(JSONArray jsonArray) {
//                L.e("pushAdjustTime onSuccess:" + jsonArray.toString());
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//                L.e("pushTimeStampInfo onFailure:" + s);
//            }
//        });
    }

    @Subscribe
    public void onEventMainThread(AdjustTimeBus event) {
        if (event.finish) {
            if (hasSelected()) {
                generateSettingTime();
                pushAdjustTime(TimeUtil.getNowTimeSeconds(),settingTime);
                BleMgr.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_SYNC_WATCH_TIME), I2B_WatchTime(settingTime));
            }
            finish();
        }
    }

    @Override
    protected String getTipText() {
        return getString(R.string.pls_set_pos);
    }

    @Override
    protected String getBtnText() {
        return getString(R.string.next_step);
    }

    protected int getContentViewLayout() {
        return R.layout.watch_content_time_first;
    }

    protected void initViewOrData() {
        super.initViewOrData();
        btn.setEnabled(false);
        minLabelTextRow = (LabelTextRow) contentView.findViewById(R.id.minLocation);
        hourLabelTextRow = (LabelTextRow) contentView.findViewById(R.id.hourLocation);
        tvAccurate = (TextView) contentView.findViewById(R.id.tv_accurate);
        minLabelTextRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMinutePositionDialog();
            }
        });
        hourLabelTextRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHourPositionDialog();
            }
        });
        tvAccurate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTo(NewAdjustTimeThirdAct.class);
            }
        });
        //停表
        SyncDeviceHelper.syncSetControlFlag(MAC, new SyncDeviceHelper.BtCallback() {
            @Override
            public void onBtResponse(byte[] bytes) {

            }
        }, new int[]{1, 0, 0, 0});
    }

    @Override
    protected void btnOnClick() {
        switchTo(NewAdjustTimeSecAct.class);
    }

    public void showHourPositionDialog() {
        View v = View.inflate(mContext, R.layout.watch_dialog_line, null);
        final WatchNumberPicker lp = (WatchNumberPicker) v.findViewById(R.id.lp);
        lp.setMinValue(0);
        lp.setMaxValue(23);
        lp.setDisplayedValues(getHourDisplayStrings());
        lp.setLabel("");
        lp.setValue(selectH >-1?selectH:0);
        new MLAlertDialog.Builder(mContext).setTitle(getString(R.string.h_hand_position))
                .setView(v).
                setPositiveButton(mContext.getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        selectH = lp.getValue() / 2 + 1;
                        selectH = lp.getValue();
                        hourLabelTextRow.setText(getHourDisplayStrings()[selectH]);
                        if (selectH % 2 == 0) {
                            minLabelTextRow.setText("0");
                            selectM = 0;
                        }
                        if (hasSelected()) {
                            btn.setEnabled(true);
                        }
                    }
                }).setNegativeButton(mContext.getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).setCancelable(false).show();
    }

    public void showMinutePositionDialog() {
        View v = View.inflate(mContext, R.layout.watch_dialog_line, null);
        final WatchNumberPicker lp = (WatchNumberPicker) v.findViewById(R.id.lp);
        lp.setMinValue(0);
        lp.setMaxValue(59);
        lp.setLabel("");
        lp.setValue(selectM >-1?selectM:0);
        lp.setDisplayedValues(getMinDisplayStrings());
        new MLAlertDialog.Builder(mContext).setTitle(getString(R.string.m_hand_position))
                .setView(v).
                setPositiveButton(mContext.getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectM = lp.getValue();
                        minLabelTextRow.setText(getMinDisplayStrings()[selectM]);
                        if (selectH > -1) {
                            if (selectM == 0 && selectH % 2 == 1) {
                                L.d("selectM == 0 && selectH % 2 == 1");
                                selectH = selectH - 1;
                                hourLabelTextRow.setText(getHourDisplayStrings()[selectH]);
                            } else if (selectM != 0 && selectH % 2 == 0) {
                                L.d("selectM != 0 && selectH % 2 == 0");
                                selectH = selectH + 1;
                                hourLabelTextRow.setText(getHourDisplayStrings()[selectH]);
                            }
                            if (hasSelected()) {
                                btn.setEnabled(true);
                            }
                        }
                    }
                }).setNegativeButton(mContext.getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).setCancelable(false).show();
    }

    private String[] getHourDisplayStrings() {
        return getResources().getStringArray(R.array.hour_positon);
    }

    private String[] getMinDisplayStrings() {
        String[] ret = new String[60];
        for (int i = 0; i < 60; i++) {
            ret[i] = String.valueOf(i);
        }
        return ret;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //恢复时针走针
        SyncDeviceHelper.syncSetControlFlag(MAC, new SyncDeviceHelper.BtCallback() {
            @Override
            public void onBtResponse(byte[] bytes) {

            }
        }, new int[]{2, 0, 0, 0});
    }

    private void generateSettingTime() {
        settingTime = (selectH / 2) * 3600 + selectM * 60;
    }

    private boolean hasSelected() {
        return selectH >= 0 && selectM >= 0;
    }
}
