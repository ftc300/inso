package com.inso.core;

import android.os.Handler;

import com.inso.plugin.tools.L;
import com.inso.product.IBindUiHandle;
import com.inso.watch.commonlib.utils.NetworkUtils;
import com.inso.watch.commonlib.utils.PermissionUtils;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.inso.plugin.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;
import static com.inuker.bluetooth.library.Code.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;


/**
 * Comment:
 * Author: ftc300
 * Date: 2019/3/13
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class BindMgr {
    private static final String CHARACTERISTIC_BIND = "c99a3106-7f3c-4e85-bde2-92f2037bfd42";
    private static final int TYPE_SECURITY_VALIDATE = 1;
    private static final int TYPE_REQUEST_BIND = 2;
    private static final int TYPE_REQUEST_UNBIND = 3;
    private static final int NOTIFY_SUCCESS = 1;
    private static final int NOTIFY_FAIL = 2;
    private static final int NOTIFY_EVENT = 3;
    private AtomicBoolean hasAcceptNotify = new AtomicBoolean(false);
    Set<String> hasCheckedBondSet = new HashSet<>();
    private Handler mHandler ;
    private IBindUiHandle mUiHandle;
    private BleConnectStatusListener mStatusListener =  new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            if (status == STATUS_CONNECTED) {
                notifyWatch(mac);
                securityValidate(mac);
            }
        }
    };
    private boolean foundTarget =false;


    public BindMgr(IBindUiHandle uiHandle) {
        mUiHandle = uiHandle;
        mHandler = new Handler();
    }

    public void startBind() {
        //ble 和 wifi 权限
        L.d("bind :: start check permissions");
        if (!PermissionUtils.isGranted(android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
            mUiHandle.showNoPermison();
            return;
        }
        L.d("bind :: start check ble and net");
        if (!checkBleAndNet()) return;
        L.d("bind :: startScan");
        startScan();
    }

    private boolean checkBleAndNet() {
        if (!BleMgr.getInstance().isBluetoothOpen()) {
            mUiHandle.showBleError();
            return false;
        }
        if (!NetworkUtils.isConnected()) {
            mUiHandle.showNetError();
            return false;
        }
        return true;
    }

    private void startScan() {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(10000) //
                .build();
        BleMgr.getInstance().search(request, new SearchResponse() {
            @Override
            public void onSearchStarted() {
                foundTarget =false;
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                String mac = device.getAddress();
                synchronized (BindMgr.class) {
                    if (isMiWatch2(device)) {
                        L.d("bind :: found MiWatch2");
                        if(hasCheckedBondSet.contains(mac)) return;
                        if (!hasBond(mac) && !foundTarget) { //未绑定
                            L.d("bind :: found never bond MiWatch2");
                            foundTarget = true;
                            BleMgr.getInstance().stopSearch();
                            registerBleConStatusListener(mac,mStatusListener);
                            connectWatch(mac);
                        } else { //已经绑定
                            L.d("have bond" + mac);
                            mUiHandle.showHasBond();
                        }
                    }
                }

            }

            @Override
            public void onSearchStopped() {
            }

            @Override
            public void onSearchCanceled() {

            }
        });
    }

    private boolean isMiWatch2(SearchResult result) {
        return result.rssi > -100 && bytesToHexString(result.scanRecord).contains("1695FE3030CDAB");
    }


    //request server
    private boolean hasBond(String mac) {
        //todo
        hasCheckedBondSet.add(mac);
        return false;
    }

    private void connectWatch(final String mac) {
        L.d("bind :: connectWatch");
        BleMgr.getInstance().connect(mac, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                if (REQUEST_SUCCESS == code) {

                } else {
                    L.d("connect fail");
                }
            }
        });
    }


    public void registerBleConStatusListener(String mac,BleConnectStatusListener mBleConnectStatusListener){
        BleMgr.getInstance().register(mac, mBleConnectStatusListener);
    }


    public void unRegisterBleConStatusListener(String MAC,BleConnectStatusListener mBleConnectStatusListener ){
        BleMgr.getInstance().unRegister(MAC, mBleConnectStatusListener);
    }

    private void notifyWatch(final String mac) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!hasAcceptNotify.get()){
                    unRegisterBleConStatusListener(mac,mStatusListener);
                    mUiHandle.showBindTimeout();
                    BleMgr.getInstance().disConnect(mac);
                }
            }
        },1000);// 10s 若还没有按键就显示绑定失败

        BleMgr.getInstance().notify(mac, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_BIND), new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
                int response = value[0] & 0x0FF;
                unRegisterBleConStatusListener(mac,mStatusListener);
                if (response == NOTIFY_SUCCESS) {
                    L.d("bind ::receive notify security validate success");
                    requestBind(mac);
                } else if (response == NOTIFY_FAIL) {
                    L.d("bind ::receive notify security validate fail");

                    mUiHandle.showBindFail();
                } else if (response == NOTIFY_EVENT) {
                    L.d("bind ::receive notify event ");
                    hasAcceptNotify.set(true);
                    mUiHandle.showBindSuccess();
                }else {
                    L.d("bind :: receive unknown");
//                    mUiHandle.showBindFail();
                }
            }

            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    L.d("bind :: set notifyWatch success");
                }else {
                    L.d("bind :: set notifyWatch fail");
                }
            }
        });
    }

    private void securityValidate(String mac) {
        BleMgr.getInstance().write(mac, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_BIND), inputValue(TYPE_SECURITY_VALIDATE));
    }

    private void requestBind(String mac) {
        BleMgr.getInstance().write(mac, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_BIND), inputValue(TYPE_REQUEST_BIND));
    }

    private void requestUnbind(String mac) {
        BleMgr.getInstance().write(mac, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_BIND), inputValue(TYPE_REQUEST_UNBIND));
    }

    private byte[] inputValue(int type) {
        byte[] src = new byte[4];
        src[0] = (byte) (type & 0x0FF);
        return src;
    }

    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null) return "";
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
//          String hexString = Integer.toHexString(bytes[i] & 0xFF);
            String hexString = Integer.toHexString(bytes[i] & 0x0FF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result += hexString.toUpperCase();
        }
        return result;
    }
}
