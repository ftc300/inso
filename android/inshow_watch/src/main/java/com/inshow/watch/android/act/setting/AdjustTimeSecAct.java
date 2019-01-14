package com.inshow.watch.android.act.setting;

import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.event.AdjustStepPageOneBus;
import com.inshow.watch.android.event.AdjustTimeBus;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.TextStyle;
import com.inshow.watch.android.tools.TimeUtil;
import com.inshow.watch.android.view.LineOnePicker;
import com.xiaomi.smarthome.bluetooth.Response;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static com.inshow.watch.android.manager.BleManager.I2B_WatchTime;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_SYNC_WATCH_TIME;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;
import static com.inshow.watch.android.view.LineOnePicker.TWO_DIGIT_FORMATTER;

/**
 * Created by chendong on 2017/3/16.
 * 指针校准(手动)
 */
public class AdjustTimeSecAct extends BasicAct {
    private LineOnePicker hourPicker, minPicker;
    private TextView tvTip;
    int[] time;
    TextStyle ts;

    @Override
    protected int getContentRes() {
        return R.layout.watch_activity_adjust_time;
    }

    @Override
    protected int getTitleRes() {
        return R.layout.title_bar_transparent_black;
    }

    @Override
    protected void initViewOrData() {
        setBtnOnBackPress();
        setActStyle(ActStyle.DT);
        findViewById(R.id.title_bar_more).setVisibility(View.GONE);
        time = TimeUtil.getWatchTime(mDBHelper.getSettingZone(), Calendar.HOUR);
        tvTip = (TextView) findViewById(R.id.tv_tip);
        hourPicker = (LineOnePicker) findViewById(R.id.timeHourPicker);
        minPicker = (LineOnePicker) findViewById(R.id.timeMinPicker);
        hourPicker.setMinValue(1);
        hourPicker.setMaxValue(12);
        hourPicker.setValue(time[0]);
        hourPicker.setFormatter(TWO_DIGIT_FORMATTER);
        minPicker.setMaxValue(11);
        minPicker.setMinValue(0);
        minPicker.setValue(time[1] / 5);
        minPicker.setDisplayedValues(getDisplayMinTime());
        minPicker.setFormatter(TWO_DIGIT_FORMATTER);
        ts = new TextStyle(ContextCompat.getColor(mContext, R.color.black_60_transparent), 20);
        findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = hourPicker.getValue();
                int minuteIndex = minPicker.getValue();
                int minute = Integer.parseInt(minPicker.getDisplayedValues()[minuteIndex]);
                L.e("minPicker.getValue():" + minute);
                final int selectTime = minute * 60 + hour * 3600;
                time = TimeUtil.getWatchTime(mDBHelper.getSettingZone(), Calendar.HOUR);
                L.e("time:" + time[0] + "," + time[1]  + ",time[0] * 3600 + time[1] * 60 :" + (time[0] * 3600 + time[1] * 60) + ",selecttime:" + selectTime );
                if (Math.abs(time[0] * 3600 + time[1] * 60 - selectTime) == 0) {
                    View customTitle = View.inflate(mContext, R.layout.wacth_dialog_custom_title, null);
                    TextView customTextView = (TextView) customTitle.findViewById(R.id.title);
                    customTextView.setText(ts.clear().spanColorAndSize(TimeUtil.getAjustTime(hour, minute) + "\n").span(getString(R.string.adjust_warning_tip)).getText());
                    new MLAlertDialog.Builder(mContext)
                            .setCustomTitle(customTitle)
                            .setPositiveButton(getString(R.string.time_still_adjust), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    writeToWatch(selectTime);
                                }
                            }).setNegativeButton(getString(R.string.retype), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).setCancelable(true).show();
                } else {
                    writeToWatch(selectTime);
                }
            }
        });

    }

    private void writeToWatch(int time) {
        XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_SYNC_WATCH_TIME), I2B_WatchTime(time), new Response.BleWriteResponse() {
            @Override
            public void onResponse(int code, Void data) {

            }
        });
        EventBus.getDefault().post(new AdjustTimeBus(true));
        EventBus.getDefault().post(new AdjustStepPageOneBus(true));
        finish();
    }

    private String[] getDisplayMinTime() {
        List<String> ret = new ArrayList<>();
        for (int i = 0; i < 56; i++) {
            if (i % 5 == 0) ret.add(String.valueOf(i));
        }
        return ret.toArray(new String[ret.size()]);
    }
}
