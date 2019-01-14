package com.inshow.watch.android.act.interval;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.inshow.watch.android.R;
import com.inshow.watch.android.dao.IntervalDao;
import com.inshow.watch.android.provider.DBHelper;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.TimeUtil;
import com.inshow.watch.android.view.WatchNumberPicker;
import com.xiaomi.smarthome.bluetooth.Response;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;

import java.util.UUID;

import static com.inshow.watch.android.manager.BleManager.setWriteIntervalByte;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_INTERVAL_REMIND;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/9/15
 * @ 描述:     Note:(界面上设置间隔和打开间隔都调用2，重新计时调用5，关闭调用3)
 * Read/write	1Byte	命令: 5复位；4打开；3关闭；2设置；1清除；0仅更新状态
 * 1Byte	状态：2:打开；1关闭；0无效(查询)
 * 1Byte	间隔分钟：[1..60]；61:整点提醒，设置整点提醒时，不用计算剩余时间
 * 2Byte	本次提醒剩余时间：[1..3600]
 */
public class IntervalHelper {

    private String MAC;
    private ISaveOperation listener;
    private Context context;
    public static final int INTEGRAL_CONST = 61;//整点

    public IntervalHelper(Context context, String MAC, ISaveOperation listener) {
        this.MAC = MAC;
        this.context = context;
        this.listener = listener;
    }

    /**
     * 【新】设置间隔提醒剩余和间隔一样
     * 设备同步时剩余和间隔不一样的
     */
    public void setWatchInterval() {
        if (null != listener) {
            listener.saveData();
            IntervalDao dao = listener.getItem();
            int[] inputs = new int[4];
            inputs[0] = 2;
            inputs[1] = 0;
            inputs[2] = dao.time / 60; //min
            inputs[3] = dao.time; //s 整点就是61*60
            XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_INTERVAL_REMIND), setWriteIntervalByte(inputs), new Response.BleWriteResponse() {
                @Override
                public void onResponse(int code, Void data) {
                }
            });
        }
    }

    /**
     * 重置手表间隔
     */
    public void resetWatchInterval() {
        if (null != listener) {
            listener.saveData();
            int[] inputs = new int[4];
            inputs[0] = 5;
            inputs[1] = 0;
            inputs[2] = 0;
            inputs[3] = 0;
            XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_INTERVAL_REMIND), setWriteIntervalByte(inputs), new Response.BleWriteResponse() {
                @Override
                public void onResponse(int code, Void data) {
                }
            });
        }
    }


    /**
     * 关闭
     */
    public void closeWatchInterval() {
        if (null != listener) {
            listener.saveData();
            int[] inputs = new int[4];
            inputs[0] = 3;
            inputs[1] = 0;
            inputs[2] = 0;
            inputs[3] = 0;
            XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_INTERVAL_REMIND), setWriteIntervalByte(inputs), new Response.BleWriteResponse() {
                @Override
                public void onResponse(int code, Void data) {
                }
            });
        }
    }


    /**
     * 显示设置间隔的dialog
     *
     * @param showDefaultIntegral 显示整点
     * @param dialogClick         点击确定
     */
    public void showSetIntervalDialog(boolean showDefaultIntegral, int interval, final IDialogClick dialogClick) {
        if (null != listener) {
            View v = View.inflate(context, R.layout.watch_dialog_line, null);
            final WatchNumberPicker lp = (WatchNumberPicker) v.findViewById(R.id.lp);
            lp.setMinValue(5);
            lp.setMaxValue(61);
            lp.setFormatter(WatchNumberPicker.TWO_DIGIT_FORMATTER);
            final int selectV = (interval/60) >=5 ?(interval/60): 5;
            lp.setValue(showDefaultIntegral ? INTEGRAL_CONST : selectV);
            lp.setLabel((showDefaultIntegral || interval / 60 == INTEGRAL_CONST) ? "" : context.getString(R.string.unit_fenzhong));
            lp.setDisplayedValues(getDisplayStrings());
            lp.setOnValueChangedListener(new WatchNumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(WatchNumberPicker picker, int oldVal, int newVal) {
                    lp.setLabel(newVal == 61 ? "" : context.getString(R.string.unit_fenzhong));
                }
            });
            new MLAlertDialog.Builder(context).setTitle(context.getString(R.string.set_interval_duration))
                    .setView(v).
                    setPositiveButton(context.getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogClick.OK(lp.getValue() * 60);
                        }
                    }).setNegativeButton(context.getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogClick.Cancel();
                }
            }).setCancelable(false).show();
        }
    }

    /**
     * dialog显示的文字
     *
     * @return
     */
    private String[] getDisplayStrings() {
        String[] ret = new String[57];
        for (int i = 0; i < 56; i++) {
            ret[i] = String.valueOf(i+5);
        }
        ret[56] = context.getString(R.string.integral);
        return ret;
    }

    /**
     * 计算剩余时间
     * 起始剩余时间,默认打开时
     *
     * @return
     */
    public static int getCalcuOriginRemain(DBHelper dbHelper, IntervalDao originDao) {
        if (isIntegral(originDao.time)) {
            L.d("getCalcuOriginRemain isIntegral");
            return TimeUtil.getIntegralDeltaTime(dbHelper);
        } else {
//            L.e("calcuOriginRemain非整点:"+
//                    TimeUtil.getNowTimeSeconds()+
//                    ","+
//                    originDao.time +
//                    ","+
//                    TimeUtil.getNowTimeSeconds()+
//                    ","+
//                    originDao.start +
//                    ","+
//                    (originDao.time - (TimeUtil.getNowTimeSeconds() - originDao.start) % originDao.time)
//            );
            L.d("getCalcuOriginRemain isNotIntegral");
            return originDao.time - (TimeUtil.getNowTimeSeconds() - originDao.start) % originDao.time;
        }
    }

    /**
     * 整点
     *
     * @return
     */
    public static boolean isIntegral(int interval) {
        return interval / 60 == INTEGRAL_CONST;
    }

    interface IDialogClick {
        void OK(int pickInterval);

        void Cancel();
    }
}
