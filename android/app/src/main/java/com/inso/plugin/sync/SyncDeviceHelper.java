package com.inso.plugin.sync;


import com.inso.core.BleMgr;
import com.inso.plugin.dao.AlarmDao;
import com.inso.plugin.dao.IntervalDao;
import com.inso.plugin.dao.VibrationDao;
import com.inso.plugin.model.VipEntity;
import com.inso.plugin.provider.DBHelper;
import com.inso.plugin.tools.L;

import java.util.UUID;

import static com.inso.plugin.tools.Constants.GattUUIDConstant.CHARACTERISTIC_HISTORY_STEP;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;


/**
 * Created by chendong on 2017/5/5.
 * 同步手表信息帮助类
 */
public class SyncDeviceHelper {
    /**
     * 手表日志
     *
     * @param MAC
     * @param callback
     */
    public static void heartbeatTest(final String MAC, final BtCallback callback) {
//        XmBluetoothManager.getInstance().read(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_TODAY_STEP), new Response.BleReadResponse() {
//            @Override
//            public void onResponse(int code, byte[] bytes) {
//                if (code == XmBluetoothManager.Code.REQUEST_SUCCESS) {
//                    callback.onBtResponse(bytes);
//                } else {
//                    L.e("heartbeatTest:error");
//                }
//            }
//        });
    }

    /**
     * 手表日志
     *
     * @param MAC
     * @param callback
     */
    public static void syncWatchDebug(final String MAC, final BtCallback callback) {
//        XmBluetoothManager.getInstance().read(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_DEBUG_LOG), new Response.BleReadResponse() {
//            @Override
//            public void onResponse(int code, byte[] bytes) {
//                if (code == XmBluetoothManager.Code.REQUEST_SUCCESS) {
//                    callback.onBtResponse(bytes);
//                } else {
//                    L.e("syncWatchDebug:error");
//                }
//            }
//        });
    }


    /**
     * 设备电量
     *
     * @param MAC
     * @param callback
     */
    public static void syncDeviceBattery(final String MAC, final BtCallback callback) {
//        XmBluetoothManager.getInstance().read(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_POWER_CONSUMPTION), new Response.BleReadResponse() {
//            @Override
//            public void onResponse(int code, byte[] bytes) {
//                L.e("PowerConsumption:" + bytesToHexString(bytes));
//                if (code == XmBluetoothManager.Code.REQUEST_SUCCESS) {
//                    callback.onBtResponse(bytes);
//                } else {
//                    L.e("syncDeviceBattery:error");
//                }
//            }
//        });
    }


    /**
     * 计步数据
     *
     * @param MAC
     * @param callback
     */
    public static void syncDeviceStepHistory(final String MAC, final BtCallback callback) {
        BleMgr.getInstance().read(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_HISTORY_STEP), new BleMgr.IReadOnResponse() {
            @Override
            public void onSuccess(byte[] data) {
                callback.onBtResponse(data);
            }

            @Override
            public void onFail() {
                L.e("syncDeviceStepHistory:error");
            }
        });
    }

    /**
     * 控制位
     *
     * @param MAC
     * @param callback
     */
    public static void syncSetControlFlag(String MAC, final SyncDeviceHelper.BtCallback callback, int[] controlArr) {
//        XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CONTROL), I2B_Control(controlArr), new Response.BleWriteResponse() {
//            @Override
//            public void onResponse(int code, Void data) {
//                callback.onBtResponse(null);
//            }
//        });
    }


    /**
     * 同步闹钟
     *
     * @param item
     * @param MAC
     */
    public static void syncDeviceAlarm(String MAC, AlarmDao item) {
//        int[] args = new int[3];
//        args[0] = item.id;
//        args[1] = 2;
//        args[2] = 0;
//        XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_ALARM_CLOCK), setAlarm(args, item.seconds, getRepeatWriteBytes(item.repeatType)), new Response.BleWriteResponse() {
//            @Override
//            public void onResponse(int code, Void data) {
//
//            }
//        });
//        args[1] = item.status ? 4 : 3;
//        if (!item.status) {//if close need write
//            XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_ALARM_CLOCK), setAlarm(args, 0, new byte[2]), new Response.BleWriteResponse() {
//                @Override
//                public void onResponse(int code, Void data) {
//
//                }
//            });
//        }
    }

    /**
     * 全部清除闹钟
     *
     * @param MAC
     */
    public static void syncClearDeviceAlarm(String MAC) {
        int[] args = new int[3];
        args[0] = 0;
        args[1] = 5;
//        XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_ALARM_CLOCK), setAlarm(args, 0, new byte[2]), new Response.BleWriteResponse() {
//            @Override
//            public void onResponse(int code, Void data) {
//
//            }
//        });
    }


    /**
     * 清空设备联系人
     *
     * @param MAC
     */
    public static void syncClearDeviceVip(String MAC) {
//        XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_VIP), setVip(1, 3, new byte[18]), new Response.BleWriteResponse() {
//            @Override
//            public void onResponse(int code, Void data) {
//                L.e("清空设备联系人成功");
//            }
//        });
    }

    /**
     * 清空间隔提醒
     *
     * @param MAC
     */
    public static void syncClearInterval(String MAC) {
        int[] inputs = new int[4];
        inputs[0] = 3;
        inputs[1] = 0;
        inputs[2] = 0;
        inputs[3] = 0;
//        XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_INTERVAL_REMIND), setWriteIntervalByte(inputs), new Response.BleWriteResponse() {
//            @Override
//            public void onResponse(int code, Void data) {
//
//            }
//        });
    }

    /**
     * 清空振动提醒
     *
     * @param MAC
     */
    public static void syncClearVibration(String MAC) {
        int[] source = new int[5];
        source[0] = 0;
        source[1] = 0;
        source[2] = 0;
        source[3] = 0;
        source[4] = 0;
//        BleMgr.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_VIBRATION_SETTING), BleMgr.setWriteVibration(source), new Response.BleWriteResponse() {
//            @Override
//            public void onResponse(int code, Void data) {
//
//            }
//        });
    }


    /**
     * 同步联系人
     *
     * @param MAC
     * @param item
     */
    public static void syncDeviceVip(String MAC, VipEntity item) {
        // 3清空; 2设置; 1清除; 0:更新状态
//        XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_VIP), setVip(item.id, 2, item.name.getBytes(Charset.forName("UTF-8"))), new Response.BleWriteResponse() {
//            @Override
//            public void onResponse(int code, Void data) {
//
//            }
//        });

    }

    /**
     * 同步世界时间
     *
     * @param MAC
     */
    public static void syncDevicePreferTime(String MAC, int time, final BtCallback callback) {
//        XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_SYNC_CURRENT_TIME), I2B_SyncTime(time), new Response.BleWriteResponse() {
//            @Override
//            public void onResponse(int code, Void data) {
//                callback.onBtResponse(null);
//            }
//        });
    }

    /**
     * 同步间隔时间
     *
     * @param MAC
     * @param item
     */
    public static void syncDeviceInterval(String MAC, IntervalDao item, DBHelper dbHelper) {
        //interval time
//        if (ON.equals(item.status) && (item.time > 0) && (item.time % 60 == 0)) { //打开状态
//            int[] inputs = new int[4];
//            inputs[0] = 2;
//            inputs[1] = 0;
//            inputs[2] = item.time / 60;
//            inputs[3] = getCalcuOriginRemain(dbHelper, item);
//            XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_INTERVAL_REMIND), setWriteIntervalByte(inputs), new Response.BleWriteResponse() {
//                @Override
//                public void onResponse(int code, Void data) {
//                }
//            });
//        } else {
////        else if (OFF.equals(item.status)) {
//            int[] inputs = new int[4];
//            inputs[0] = 3;
//            inputs[1] = 0;
//            inputs[2] = 0;
//            inputs[3] = 0;
//            XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_INTERVAL_REMIND), setWriteIntervalByte(inputs), new Response.BleWriteResponse() {
//                @Override
//                public void onResponse(int code, Void data) {
//
//                }
//            });
//        }
    }

    /**
     * 同步振动提醒
     *
     * @param MAC
     * @param item
     */
    public static void syncDeviceVibration(String MAC, VibrationDao item) {
//        int[] source = new int[5];
//        source[0] = 0;//震动加强
//        source[1] = item.stronger ? 1 : 0;
//        source[2] = item.startTime;
//        source[3] = item.endTime;
//        source[4] = 0;
//        BleMgr.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_VIBRATION_SETTING), BleMgr.setWriteVibration(source), new Response.BleWriteResponse() {
//            @Override
//            public void onResponse(int code, Void data) {
//
//            }
//        });
//        source[0] = 1;//免打扰
//        source[1] = item.notdisturb ? 1 : 0;
//        source[2] = item.startTime;
//        source[3] = item.endTime;
//        source[4] = 0;
//        BleMgr.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_VIBRATION_SETTING), BleMgr.setWriteVibration(source), new Response.BleWriteResponse() {
//            @Override
//            public void onResponse(int code, Void data) {
//
//            }
//        });
    }

    //vip alert all
    public static void changeInComingAlertState(String MAC,boolean state){
//        XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_VIP), changeVipState(state?4:3), new Response.BleWriteResponse() {
//            @Override
//            public void onResponse(int code, Void data) {
//            }
//        });
    }

    public interface BtCallback {
        void onBtResponse(byte[] bytes);
    }
}
