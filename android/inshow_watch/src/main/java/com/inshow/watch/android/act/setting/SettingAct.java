package com.inshow.watch.android.act.setting;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.act.debug.TimeSyncAcitivity;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.tools.ToastUtil;
import com.inshow.watch.android.view.LabelTextRow;
import com.xiaomi.smarthome.bluetooth.BleUpgrader;
import com.xiaomi.smarthome.bluetooth.Response;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;
import com.xiaomi.smarthome.device.api.BtFirmwareUpdateInfo;
import com.xiaomi.smarthome.device.api.Callback;
import com.xiaomi.smarthome.device.api.IXmPluginHostActivity;
import com.xiaomi.smarthome.device.api.XmPluginHostApi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.inshow.watch.android.manager.BleManager.I2B_OneBit;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_CONTROL;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;

/**
 * Created by chendong on 2017/1/22.
 */
public class SettingAct extends BasicAct {

    private  LabelTextRow bodyInfo,instructions,deviceInfo,upgrade,adjust;
    private  TextView unbind;
    private  String mCurrentV;
    private BtFirmwareUpdateInfo mUpdateInfo;

    @Override
    protected int getContentRes() {
        return R.layout.watch_activity_settings;
    }

    @Override
    protected void initViewOrData() {
        setTitleText(getString(R.string.settting));
        setBtnOnBackPress();
        bodyInfo  = (LabelTextRow) findViewById(R.id.body_info);
        instructions  = (LabelTextRow) findViewById(R.id.instructions);
        deviceInfo  = (LabelTextRow) findViewById(R.id.device_info);
        upgrade  = (LabelTextRow) findViewById(R.id.upgrade);
        adjust  = (LabelTextRow) findViewById(R.id.adjust);
        unbind  = (TextView) findViewById(R.id.unbind);
        bodyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchTo(BodyInfoAct.class);
            }
        });
        deviceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchTo(DeviceInfoAct.class);
            }
        });
        adjust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchTo(AdjustTimeSecAct.class);
            }
        });
//      读取设备固件版本
        XmBluetoothManager.getInstance().getBluetoothFirmwareVersion(MAC, new Response.BleReadFirmwareVersionResponse() {
            @Override
            public void onResponse(int code, String version) {
                // version类似1.0.3_2001
                mCurrentV = version;
//                ToastUtil.showToastNoRepeat(mContext,mCurrentV);
            }
        });
//        升级信息查询
        XmPluginHostApi.instance().getBluetoothFirmwareUpdateInfo("inshow.watch.w1", new Callback<BtFirmwareUpdateInfo>() {
            @Override
            public void onSuccess(BtFirmwareUpdateInfo btFirmwareUpdateInfo) {
                mUpdateInfo = btFirmwareUpdateInfo;
            }

            @Override
            public void onFailure(int error, String msg) {
                L.e("error="+error+",error msg="+msg);
            }
        });
        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<IXmPluginHostActivity.MenuItemBase> menus = new ArrayList<>();
                menus.add(IXmPluginHostActivity.BleMenuItem.newUpgraderItem(new InShowUpgrader()));
                hostActivity().openMoreMenu((ArrayList<IXmPluginHostActivity.MenuItemBase>) menus, true, 0);
        }
        });

        findViewById(R.id.debug_adjust).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchTo(TimeSyncAcitivity.class);
            }
        });
        findViewById(R.id.stepPoint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchTo(AdjustStepAct.class);
            }
        });
    }

    public class InShowUpgrader extends BleUpgrader {

        @Override
        public String getCurrentVersion() {
            // 返回当前固件版本
            return mCurrentV;
        }

        @Override
        public String getLatestVersion() {
            // 返回最新固件版本
//            return  mUpdateInfo.version;
            return  "1.2.3_1";

        }

        @Override
        public String getUpgradeDescription() {
            // 返回最新固件升级描述
//            return mUpdateInfo.changeLog;
            return "最新固件升级描述2017/04/20";
        }

        @Override
        public void startUpgrade() {
            ToastUtil.showToastNoRepeat(mContext,"开始升级了！");
            // 开始下载
//            XmPluginHostApi.instance().downloadBleFirmware(mUpdateInfo.url, new Response.BleUpgradeResponse() {
            XmPluginHostApi.instance().downloadBleFirmware("http://138.128.211.91/download/R02_update_wh0418.zip", new Response.BleUpgradeResponse() {
                @Override
                public void onProgress(int progress) {

                }

                @Override
                public void onResponse(int code, String filePath) {
                    L.e("filePath:"+filePath);
                }
            });

            XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CONTROL), I2B_OneBit(5), new Response.BleWriteResponse() {
                @Override
                public void onResponse(int code, Void data) {
//                    switchTo(DfuAct.class);
                }
            });
        }

        @Override
        public void onActivityCreated(Bundle bundle) throws RemoteException {
            // 固件升级页初始化完成
            showPage(XmBluetoothManager.PAGE_CURRENT_DEPRECATED, null);
        }

        @Override
        public boolean onPreEnterActivity(Bundle bundle) throws RemoteException {
            return false;
        }
    }
}

