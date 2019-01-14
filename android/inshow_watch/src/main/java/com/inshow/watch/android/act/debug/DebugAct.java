package com.inshow.watch.android.act.debug;

import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.act.debug.debugadjust.DebugAdjustMainAct;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.manager.BleManager;
import com.inshow.watch.android.manager.SPManager;
import com.inshow.watch.android.sync.SyncDeviceHelper;
import com.inshow.watch.android.tools.FileUtil;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.ToastUtil;
import com.inshow.watch.android.view.InShowProgressDialog;
import com.inshow.watch.android.view.LabelTextRow;
import com.xiaomi.smarthome.bluetooth.Response;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;

import java.util.UUID;

import static com.inshow.watch.android.manager.BleManager.bytesToHexString;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_CONTROL;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_DEBUG_LOG;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_DEBUG_ARG_BLE_SECURITY;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_DEBUG_ARG_LOCAL_TIME;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_DEBUG_FLAG;

/**
 * Created by chendong on 2017/1/22.
 */
public class DebugAct extends BasicAct {

    private LabelTextRow debug00,debug01, debug02,debug03,debug04,debug05,adjust;
    private LabelTextRow stepAdjust;
    private CheckBox switchB, switchB02;
    private CheckBox switchButton;
    private InShowProgressDialog dialogInstance;
    private boolean continueRead = true;
    StringBuffer gsensor = new StringBuffer();
    @Override
    protected int getTitleRes() {
        return R.layout.watch_title_transparent_white_remind;
    }

    @Override
    protected int getContentRes() {
        return R.layout.watch_act_debug;
    }

    @Override
    protected void initViewOrData() {
        setBtnOnBackPress();
        ((TextView) mTitleView.findViewById(R.id.title_bar_title)).setText("调试菜单");
        debug00 = (LabelTextRow) findViewById(R.id.debug00);
        stepAdjust = (LabelTextRow) findViewById(R.id.stepAdjust);
        debug00 = (LabelTextRow) findViewById(R.id.debug00);
        debug01 = (LabelTextRow) findViewById(R.id.debug01);
        debug02 = (LabelTextRow) findViewById(R.id.debug02);
        debug03 = (LabelTextRow) findViewById(R.id.debug03);
        debug04 = (LabelTextRow) findViewById(R.id.debug04);
        adjust = (LabelTextRow) findViewById(R.id.adjust);
        debug05 = (LabelTextRow) findViewById(R.id.debug05);
        switchB = (CheckBox) findViewById(R.id.switchButton1);
        switchB02 = (CheckBox) findViewById(R.id.switchButton2);
        switchButton = (CheckBox) findViewById(R.id.switchButton);
        debug00.setText(String.valueOf(mPluginPackage.packageVersion));
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MLAlertDialog.Builder(mContext)
                        .setCancelable(false)
                        .setMessage("是否隐藏调试菜单功能并返回首页重新加载设置页？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SPManager.put(mContext, SP_DEBUG_FLAG, false);
                                finish();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switchButton.setChecked(true);
                    }
                }).show();
            }
        });
        switchB.setChecked((Boolean) SPManager.get(mContext, SP_DEBUG_ARG_LOCAL_TIME, false));
        switchB02.setChecked((Boolean) SPManager.get(mContext, SP_DEBUG_ARG_BLE_SECURITY, false));
        stepAdjust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchTo(StepAdjustAct.class);
            }
        });
        debug01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchTo(TimeSyncAcitivity.class);
            }
        });
        debug02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MLAlertDialog.Builder(mContext)
                        .setCancelable(false)
                        .setMessage("是否清空用户数据？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switchTo(ClearAct.class);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();

            }
        });
        debug03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchTo(WatchLogAct.class);
            }
        });
        switchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = !(boolean)SPManager.get(mContext,SP_DEBUG_ARG_LOCAL_TIME,false);
                new MLAlertDialog.Builder(mContext)
                        .setCancelable(false)
                        .setMessage(b?"调整为使用本地时间？": "调整为使用服务端时间？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SPManager.put(mContext, SP_DEBUG_ARG_LOCAL_TIME, switchB.isChecked());
                                ToastUtil.showToastNoRepeat(mContext, "设置成功，请重新进入插件，系统会自动同步!");
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switchB.setChecked(!switchB.isChecked());
                    }
                }).show();

            }
        });

        switchB02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final boolean checked = switchB02.isChecked();
                boolean b = !(boolean)SPManager.get(mContext,SP_DEBUG_ARG_BLE_SECURITY,false);
                new MLAlertDialog.Builder(mContext)
                        .setCancelable(false)
                        .setMessage(b?"关闭蓝牙安全认证，允许其他App连接手表？": "打开蓝牙安全认证，禁止其他App连接手表？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SPManager.put(mContext, SP_DEBUG_ARG_BLE_SECURITY, checked);
                                SyncDeviceHelper.syncSetControlFlag(MAC, new SyncDeviceHelper.BtCallback() {
                                    @Override
                                    public void onBtResponse(byte[] bytes) {

                                    }
                                }, new int[]{3, checked ? 1 : 0, 0, 0});//打开写1，关闭写2
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switchB02.setChecked(!checked);
                    }
                }).show();
            }
        });

        debug04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_DEBUG_LOG), new byte[]{-1, -1, -1, -1}, new Response.BleWriteResponse() {
                    @Override
                    public void onResponse(int code, Void data) {
                        if (code == XmBluetoothManager.Code.REQUEST_SUCCESS) {
                            dialogInstance = new InShowProgressDialog(mContext);
                            dialogInstance.setCanceledOnTouchOutside(false);
                            dialogInstance.setmIsCancelable(true);
                            dialogInstance.setIndeterminate(true);
                            dialogInstance.setMessage("数据处理中，请稍候...");
                            dialogInstance.setCancelIntercepter(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    continueRead = false;
                                    dialogInstance.dismiss();
                                }
                            });
                            dialogInstance.show();
                            SyncDeviceHelper.syncWatchDebug(MAC, callback);
                        }else{
                            ToastUtil.showToastNoRepeat(mContext,"读取数据失败!");
                        }
                    }
                });

            }
        });
        debug05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CONTROL), new byte[]{9,0,0,0}, new Response.BleWriteResponse() {
                    @Override
                    public void onResponse(int code, Void data) {
                        if (code == XmBluetoothManager.Code.REQUEST_SUCCESS) {
                            ToastUtil.showToastNoRepeat(mContext,"操作成功!");
                        }else{
                            ToastUtil.showToastNoRepeat(mContext,"操作失败!");
                        }
                    }
                });
            }
        });
        adjust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToWithEventBus(DebugAdjustMainAct.class);
            }
        });
    }

    private SyncDeviceHelper.BtCallback callback = new SyncDeviceHelper.BtCallback() {
        @Override
        public void onBtResponse(byte[] bytes)  {
            try {
                int[] i = BleManager.B2I_getStepSteam(bytes);
                if (i[0] != -1 && continueRead) {
                    gsensor.append(bytesToHexString(bytes) + "\n");
                    SyncDeviceHelper.syncWatchDebug(MAC, callback);
                } else {
                    gsensor.append(bytesToHexString(bytes) + "\n");
                    ToastUtil.showToastNoRepeat(mContext,"读写成功!");
                    FileUtil.writeGsensorFile(gsensor.toString(), FileUtil.getGsensorFilePath());
                    dialogInstance.dismiss();
                }
            }catch (Exception e){
                L.e("Write Gsensor:" + e.getMessage());
                e.printStackTrace();
            }
        }
    };
}
