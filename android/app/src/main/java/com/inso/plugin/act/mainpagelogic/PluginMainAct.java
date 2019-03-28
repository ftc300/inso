package com.inso.plugin.act.mainpagelogic;

import android.content.DialogInterface;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.inso.R;
import com.inso.core.BleMgr;
import com.inso.core.pressed.CsdMgr;
import com.inso.core.pressed.ICheckDevicePressed;
import com.inso.plugin.act.more.MoreAct;
import com.inso.plugin.basic.BasicAct;
import com.inso.plugin.basic.BasicFragment;
import com.inso.plugin.event.HomePageBus;
import com.inso.plugin.fragment.FragmentBottom;
import com.inso.plugin.fragment.FragmentTop;
import com.inso.plugin.manager.SPManager;
import com.inso.plugin.tools.Constants;
import com.inso.plugin.tools.L;
import com.inso.plugin.view.MainDragLayout;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Timer;
import java.util.UUID;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;

import static com.inso.core.pressed.CsdMgr.startScan;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.CHARACTERISTIC_CONTROL;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;
import static com.inso.plugin.tools.Constants.SystemConstant.SP_ARG_BLUETOOTH_CONNECTED;
import static com.inso.plugin.tools.Constants.SystemConstant.SP_ARG_DEVICE_NAME;
import static com.inso.plugin.tools.Constants.SystemConstant.SP_ARG_MAC;
import static com.inuker.bluetooth.library.Code.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED;

/**
 * Created by chendong on 2017/2/17.
 * 首页Act
 *
 * @author chendong
 */
public class PluginMainAct extends BasicAct {
    private BasicFragment firstF, secondF;
    private TextView title, subTitle;
    private ImageView barReturn, barMore;
    private MainDragLayout dragLayout;
    private Timer timer;
    final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
    final ScanCallback callback = new ScanCallback() {
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            L.d("onScanResult" + result.toString());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            CsdMgr.getInstance().checkDevice(results);
        }
    };

    final BleConnectStatusListener mBleConnectStatusListener = new BleConnectStatusListener() {

        @Override
        public void onConnectStatusChanged(String mac, int status) {
            if (status == STATUS_CONNECTED) {
            } else if (status == STATUS_DISCONNECTED) {
                BleMgr.getInstance().unRegister(mac, mBleConnectStatusListener);
                connectFailCallback();
            }
        }
    };
    @Override
    protected int getTitleRes() {
        return R.layout.watch_title_bar_transparent_black;
    }

    @Override
    protected int getContentRes() {
        return R.layout.watch_act_main_new;
    }

    @Override
    protected void initViewOrData() {
        //性能问题 主线程中网络请求并且下载文件
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        timer = new Timer();
        barReturn = (ImageView) findViewById(R.id.title_bar_return);
        barReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackFlag = true;
            }
        });
        barMore = (ImageView) findViewById(R.id.title_bar_more);
        barMore.setEnabled(false);
        title = (TextView) findViewById(R.id.title_bar_title);
        subTitle = (TextView) findViewById(R.id.sub_title_bar_title);
        dragLayout = (MainDragLayout) findViewById(R.id.dragLayout);
        setTitleText(mDBHelper.getCacheWithDefault(SP_ARG_DEVICE_NAME, getString(R.string.A01)));
        setPageStyle();
        setBtnOnBackPress();
        barMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchTo(MoreAct.class);
            }
        });
        firstF = FragmentTop.getInstance();
        secondF = FragmentBottom.getInstance(100);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.firstF, firstF)
                .add(R.id.secondF, secondF)
                .commitAllowingStateLoss();
        AndPermission.with(this)
                .runtime()
                .permission(Permission.ACCESS_COARSE_LOCATION, Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        checkBleAndConn2();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        finish();
                    }
                })
                .start();
    }
    private void checkBleAndConn2() {
        final String mac = (String) SPManager.get(mContext,SP_ARG_MAC,"");
        BleMgr.getInstance().connect(mac, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                scanner.stopScan(callback);
                if (code == REQUEST_SUCCESS) {
                    SPManager.put(mContext, SP_ARG_MAC, mac);
                    BleMgr.getInstance().write(mac, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CONTROL), new byte[]{3, 1, 0, 0});
                    connectSuccessCallback();
                }else {
                    connectFailCallback();
                }
            }
        });
        BleMgr.getInstance().register(mac, mBleConnectStatusListener);
    }

    private void checkBleAndConn() {
        if (BleMgr.getInstance().isBluetoothOpen()) {
            startScan(scanner, callback);
            CsdMgr.getInstance().setCheckDevicePressed(new ICheckDevicePressed() {
                @Override
                public void miWatchPressed(final String mac) {
//                L.d("PressedMiWatchAct miWatchPressed mac:" + mac);
                    try {
                            BleMgr.getInstance().connect(mac, new BleConnectResponse() {
                                @Override
                                public void onResponse(int code, BleGattProfile data) {
                                    scanner.stopScan(callback);
                                    if (code == REQUEST_SUCCESS) {
                                        SPManager.put(mContext, SP_ARG_MAC, mac);
                                        BleMgr.getInstance().write(mac, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CONTROL), new byte[]{3, 1, 0, 0});
                                        connectSuccessCallback();
                                    }else {
                                        connectFailCallback();
                                    }
                                }
                            });
                            BleMgr.getInstance().register(mac, mBleConnectStatusListener);
                    } catch (Exception arg_e) {
                        arg_e.printStackTrace();
                    }
                }
            });

        } else {
            try {
                new MLAlertDialog.Builder(mContext)
                        .setMessage(getString(R.string.open_ble_tip))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.allow), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                BleMgr.getInstance().openBluetoothSilently();
                            }
                        })
                        .setNegativeButton(getString(R.string.reject), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                connectFailCallback();
                            }
                        })
                        .show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 连接成功回调
     */
    private void connectSuccessCallback() {
        dragLayout.setAllowMove(true);
        barMore.setEnabled(true);
        testIndicate();
        if (firstF.isAdded()) {
            ((FragmentTop) firstF).renderSuccess();
        }
        if (secondF.isAdded()) {
            ((FragmentBottom) secondF).renderSuccess();
        }
    }


    void testIndicate(){
        BleMgr.getInstance().indicate(MAC, UUID.fromString(Constants.GattUUIDConstant.IN_SHOW_SERVICE), UUID.fromString(Constants.GattUUIDConstant.CHARACTERISTIC_TODAY_STEP), new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
                L.d(String.format("onNotify service %s character %s value %s",service.toString(),character.toString(),BleMgr.bytes2HexString(value)));
            }

            @Override
            public void onResponse(int code) {
               L.d("onResponse code " +code);

            }
        });
        BleMgr.getInstance().write(MAC, UUID.fromString(Constants.GattUUIDConstant.IN_SHOW_SERVICE), UUID.fromString(Constants.GattUUIDConstant.CHARACTERISTIC_TODAY_STEP),new byte[]{0,0,0,0});
    }
    /**
     * 连接失败
     */
    private void connectFailCallback() {
        SPManager.put(mContext, SP_ARG_BLUETOOTH_CONNECTED, false);
        ((FragmentTop) firstF).renderFail();
        ((FragmentBottom) secondF).renderFail();
    }


    /**
     * dfu set null
     */
    private void setOnClickListenersNull() {
        findViewById(R.id.sub_title_bar_title).setOnClickListener(null);
        findViewById(R.id.title_bar_more).setOnClickListener(null);
    }


    private void setPageStyle() {
        title.setTextColor(ContextCompat.getColor(mContext, R.color.white_90_transparent));
        subTitle.setTextColor(ContextCompat.getColor(mContext, R.color.white_50_transparent));
        barReturn.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.std_tittlebar_main_device_back_white));
        barMore.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.std_tittlebar_main_device_more_unable));
        llBasic.setBackgroundColor(ContextCompat.getColor(mContext, R.color.main_bg));
        flContent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.main_bg));
    }

    @Override
    public void onDestroy() {
        scanner.stopScan(callback);
        if (null != timer) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        super.onDestroy();
        if(BleMgr.getInstance().isConnected(MAC)) {
            BleMgr.getInstance().disConnect(MAC);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        scanner.stopScan(callback);
    }
    @Subscribe
    public void onEventMainThread(HomePageBus event) {
        if (event.clickTryAgain) {
            L.e("onEventMainThread =>clickTryAgain 重新连接");
            checkBleAndConn2();
        }
    }
}
