package com.inshow.watch.android.act.setting;

import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;

import com.inshow.watch.android.R;
import com.inshow.watch.android.act.mainpagelogic.LowPowerManager;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.dao.VibrationDao;
import com.inshow.watch.android.manager.AppController;
import com.inshow.watch.android.manager.BleManager;
import com.inshow.watch.android.sync.http.HttpSyncHelper;
import com.inshow.watch.android.sync.http.RequestParams;
import com.inshow.watch.android.sync.http.bean.HttpVibrate;
import com.inshow.watch.android.tools.Configuration;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.TimeUtil;
import com.inshow.watch.android.view.LabelTextRow;
import com.inshow.watch.android.view.WatchNumberPicker;
import com.xiaomi.smarthome.bluetooth.Response;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;
import com.xiaomi.smarthome.device.api.Callback;

import org.json.JSONArray;

import java.util.Locale;
import java.util.UUID;

import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_VIBRATION_SETTING;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;
import static com.inshow.watch.android.tools.Constants.HttpConstant.TYPE_USER_INFO;
import static com.inshow.watch.android.tools.Constants.TimeStamp.VIBRATE_SETTING_KEY;
import static com.inshow.watch.android.view.WatchNumberPicker.TWO_DIGIT_FORMATTER;

/**
 * 2017/10/20
 * 振动设置页
 *
 * @author chendong
 */
public class VibrationSettingAct extends BasicAct {
    private CheckBox switchStronger;
    private CheckBox switchNotDisturb;
    private CheckBox switchCloseAuto;
    private LabelTextRow lbStart, lbEnd;
    private VibrationDao mOriginDao;
    private VibrationDao nowDao;
    private boolean auto;
    private LabelTextRow ltAuto;

    @Override
    protected int getContentRes() {
        return R.layout.watch_activity_alert_setting;
    }

    @Override
    protected void initViewOrData() {
        setBtnOnBackPress();
        setActStyle(ActStyle.WT);
        setTitleText(getString(R.string.vibrationsetting));
        ltAuto = (LabelTextRow) findViewById(R.id.lt_auto);
        switchStronger = (CheckBox) findViewById(R.id.switch_double);
        switchNotDisturb = (CheckBox) findViewById(R.id.switch_notdisturb);
        switchCloseAuto = (CheckBox) findViewById(R.id.switch_close_auto);
        lbStart = (LabelTextRow) findViewById(R.id.lt_start);
        lbEnd = (LabelTextRow) findViewById(R.id.lt_end);
        mOriginDao = mDBHelper.getVibrationInfo();
        nowDao = mOriginDao;
        switchStronger.setChecked(mOriginDao.stronger);
        switchNotDisturb.setChecked(mOriginDao.notdisturb);
        switchCloseAuto.setChecked(LowPowerManager.getInstance().getAutoSwitchState(mContext));
        displayTimeLabelByState(mOriginDao.notdisturb);
        Configuration.getInstance().LocaleHandler2(mContext, new Configuration.LocaleHandler2() {
            @Override
            public String enHandle() {
                ltAuto.setTextSize(13);
                return null;
            }

            @Override
            public String defaultHandle() {
                return null;
            }
        });
        switchStronger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                needPush = true;
                boolean checked = switchStronger.isChecked();
                setBleVibrationStronger(checked);
                nowDao.stronger = checked;
                mDBHelper.updateVibration(nowDao);
            }
        });

        switchNotDisturb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                needPush = true;
                boolean checked = switchNotDisturb.isChecked();
                nowDao.notdisturb = checked;
                displayTimeLabelByState(checked);
                setBleVibrationDisturb();
                displayTimeLabelByState(checked);
                mDBHelper.updateVibration(nowDao);
            }
        });


        switchCloseAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                needPush = true;
                boolean checked = switchCloseAuto.isChecked();
                LowPowerManager.getInstance().setAutoSwitchState(mContext, checked);
                setCloseVibAuto(checked);
            }
        });

        lbStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = View.inflate(mContext, R.layout.watch_dialog_twoline, null);
                final WatchNumberPicker hour = (WatchNumberPicker) v.findViewById(R.id.lp1);
                final WatchNumberPicker min = (WatchNumberPicker) v.findViewById(R.id.lp2);
                hour.setMaxValue(23);
                hour.setMinValue(0);
                hour.setFormatter(TWO_DIGIT_FORMATTER);
                hour.setValue(mOriginDao.startTime / 60);
                hour.setLabel(getString(R.string.watch_pick_hour));
                min.setMaxValue(59);
                min.setMinValue(0);
                min.setFormatter(TWO_DIGIT_FORMATTER);
                min.setValue(mOriginDao.endTime % 60);
                min.setLabel(getString(R.string.watch_picker_minute));

                new MLAlertDialog.Builder(mContext).setTitle(getString(R.string.start_time)).setView(v).setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        needPush = true;
                        nowDao.startTime = hour.getValue() * 60 + min.getValue();
                        setBleVibrationDisturb();
                        displayTimeLabelByState(true);
                        mDBHelper.updateVibration(nowDao);
                    }
                }).setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
            }
        });


        lbEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = View.inflate(mContext, R.layout.watch_dialog_twoline, null);
                final WatchNumberPicker hour = (WatchNumberPicker) v.findViewById(R.id.lp1);
                final WatchNumberPicker min = (WatchNumberPicker) v.findViewById(R.id.lp2);
                hour.setMaxValue(23);
                hour.setMinValue(0);
                hour.setFormatter(TWO_DIGIT_FORMATTER);
                hour.setValue(mOriginDao.endTime / 60);
                hour.setLabel(getString(R.string.watch_pick_hour));
                min.setMaxValue(59);
                min.setMinValue(0);
                min.setFormatter(TWO_DIGIT_FORMATTER);
                min.setValue(mOriginDao.endTime % 60);
                min.setLabel(getString(R.string.watch_picker_minute));
                new MLAlertDialog.Builder(mContext).setTitle(getString(R.string.end_time)).setView(v).setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        needPush = true;
                        nowDao.endTime = hour.getValue() * 60 + min.getValue();
                        setBleVibrationDisturb();
                        displayTimeLabelByState(true);
                        mDBHelper.updateVibration(nowDao);
                    }
                }).setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
            }
        });

    }

    @Override
    public void onPause() {
        if (needPush && mBackFlag) {
            mDBHelper.updateTimeStamp(VIBRATE_SETTING_KEY, TimeUtil.getNowTimeSeconds());
            VibrationDao dao = mDBHelper.getVibrationInfo();
            HttpVibrate bean = new HttpVibrate(dao.stronger ? 1 : 0, dao.notdisturb ? 1 : 0, getDisplayTime(dao.startTime), getDisplayTime(dao.endTime), LowPowerManager.getInstance().getAutoSwitchState(mContext)?1:0);
            HttpSyncHelper.pushData(
                    new RequestParams(
                            MODEL,
                            UID,
                            DID,
                            TYPE_USER_INFO,
                            VIBRATE_SETTING_KEY,
                            AppController.getGson().toJson(bean),
                            mSyncHelper.getLocalVibrationKeyTime()), new Callback<JSONArray>() {
                        @Override
                        public void onSuccess(JSONArray jsonArray) {
                            L.e("VibrationSettingAct pushVibrationToMijia Success:" + jsonArray.toString());
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            L.e("VibrationSettingAct pushVibrationToMijia Error:" + s);
                        }
                    });
        }
        super.onPause();
    }

    /**
     * 根据是否开启免打扰
     * 更改开启时间和结束时间状态
     */
    private void displayTimeLabelByState(boolean notdisturb) {
        lbStart.getLabelView().setTextColor(ContextCompat.getColor(mContext, notdisturb ? R.color.black_90_transparent : R.color.black_50_transparent));
        lbEnd.getLabelView().setTextColor(ContextCompat.getColor(mContext, notdisturb ? R.color.black_90_transparent : R.color.black_50_transparent));
        lbStart.setEnabled(notdisturb);
        lbEnd.setEnabled(notdisturb);
        lbStart.setText(notdisturb ? getDisplayTime(nowDao.startTime) : "");
        lbEnd.setText(notdisturb ? getDisplayTime(nowDao.endTime) : "");
    }
//
// 	1Byte	0：设置振动加倍（byte[1]：0不加倍，1加倍）
//           1：设置免打扰（byte[1]：0关闭，1打开）"
//  1Byte	设置参数
//  2Byte	开始振动时间（从00:00开始计算的时间，单位：分钟）
//  2Byte	停止振动时间（从00:00开始计算的时间，单位：分钟）
//  2Byte	Reserved

    /**
     * 振动加强打开
     *
     * @param stronger
     */
    private void setBleVibrationStronger(boolean stronger) {
        int[] source = new int[5];
        source[0] = 0;
        source[1] = stronger ? 1 : 0;
        source[2] = 0;
        source[3] = 0;
        source[4] = 0;
        XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_VIBRATION_SETTING), BleManager.setWriteVibration(source), new Response.BleWriteResponse() {
            @Override
            public void onResponse(int code, Void data) {

            }
        });
    }

    /**
     * 免打扰
     */
    private void setBleVibrationDisturb() {
        int[] source = new int[5];
        source[0] = 1;
        source[1] = nowDao.notdisturb ? 1 : 0;
        source[2] = nowDao.startTime;
        source[3] = nowDao.endTime;
        source[4] = 0;
        XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_VIBRATION_SETTING), BleManager.setWriteVibration(source), new Response.BleWriteResponse() {
            @Override
            public void onResponse(int code, Void data) {

            }
        });
    }

    /**
     * 低电自动清除
     */
    private void setCloseVibAuto(boolean b) {
        int[] source = new int[5];
        source[0] = 1;
        source[1] = 0;
        source[2] = b ? 0 : 1;
        source[3] = 0;
        source[4] = 0;
        XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_VIBRATION_SETTING), BleManager.setWriteVibration(source), new Response.BleWriteResponse() {
            @Override
            public void onResponse(int code, Void data) {

            }
        });
    }


    /**
     * 将数据库的开始结束时间int->string
     *
     * @param min
     * @return
     */
    private String getDisplayTime(int min) {
        return String.format(Locale.getDefault(), "%02d:%02d", min / 60, min % 60);
    }

}
