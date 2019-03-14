package com.inso.core.bind;

import android.os.Handler;

import com.inso.core.BleMgr;
import com.inso.plugin.tools.L;
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
    private static final int NOTIFY_KEY_EVENT = 3; // press key
    private static final int SCAN_DURATION = 10 * 1000;
    private static final int KEY_EVENT_WAIT_FOR = 10 * 1000;
    private static final int RSSI_THRESHOLD = -100;

    private AtomicBoolean hasAcceptKeyEventNotify = new AtomicBoolean(false);
    private AtomicBoolean isConnected = new AtomicBoolean(false);
    private Set<String> hasCheckedBondSet = new HashSet<>();
    private Handler mHandler;
    private IBindUiHandle mUiHandle;
    private AtomicBoolean foundTarget = new AtomicBoolean(false); // only allow one device bind

//    private BleConnectStatusListener mStatusListener =  new BleConnectStatusListener() {
//        @Override
//        public void onConnectStatusChanged(String mac, int status) {
//            if (status == STATUS_CONNECTED) {
//                L.d("StatusListener receive status on");
//                notifyWatch(mac);
//                securityValidate(mac);
//            }
//        }
//    };

    private BindServerImp mServerImp = new BindServerImp();

    public BindMgr(IBindUiHandle uiHandle) {
        mUiHandle = uiHandle;
        mHandler = new Handler();
    }

    public void startBind() {
        //ble 和 wifi 权限
        L.d("bind :: start check permissions");
        if (!PermissionUtils.isGranted(android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
            mUiHandle.showNoPermission();
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
                .searchBluetoothLeDevice(SCAN_DURATION) //
                .build();
        BleMgr.getInstance().search(request, new SearchResponse() {
            @Override
            public void onSearchStarted() {
                foundTarget.set(false);
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                String mac = device.getAddress();
                synchronized (BindMgr.class) {
                    if (isMiWatch2(device)) {
                        L.d("bind :: found MiWatch2 " + mac);
                        if (foundTarget.get()) {
                            L.d("there is one binding process exit , reject it ");
                            return;
                        }
                        if (hasCheckedBondSet.contains(mac)) return;
                        if (hasBond(mac)) {
                            L.d("have bond " + mac);
                            mUiHandle.showHasBond();
                            return;
                        }
                        L.d("bind :: found never bond MiWatch2 " + mac);
                        foundTarget.set(true);
                        BleMgr.getInstance().stopSearch();
//                      registerBleConStatusListener(mac,mStatusListener);
                        connectWatch(mac);
                    }
                }

            }

            @Override
            public void onSearchStopped() { // scan over
                L.d("search:: onSearchStopped");
                if (hasCheckedBondSet.size() == 0) {
                    L.d("search:: not found devices");
                    mUiHandle.showNotFoundDevice();
                }
            }

            @Override
            public void onSearchCanceled() { // stop by user
                L.d("search:: onSearchCanceled");
            }
        });
    }

    private boolean isMiWatch2(SearchResult result) {
        return result.rssi > RSSI_THRESHOLD && bytesToHexString(result.scanRecord).contains("1695FE3030CDAB");
    }


    //request server
    private boolean hasBond(String mac) {
        //todo
        boolean ret = mServerImp.searchInfo();
        if (ret) hasCheckedBondSet.add(mac); // have bond before ,add to set
        return ret;
    }

    private void connectWatch(final String mac) {
        L.d("bind :: connectWatch");
        bindFailWithoutKeyEventAfter10(mac);
        BleMgr.getInstance().connect(mac, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                if (REQUEST_SUCCESS == code) {
                    L.d("connectWatch :: connect success");
                    isConnected.set(true);
                    notifyWatch(mac);
                    securityValidate(mac);
                } else {
                    L.d("connectWatch :: connect fail");
                    isConnected.set(false);
                    mUiHandle.showBindFail();
                }
            }
        });
    }

    private void notifyWatch(final String mac) {
        BleMgr.getInstance().notify(mac, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_BIND), new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
                int response = value[0] & 0x0FF;
//                unRegisterBleConStatusListener(mac,mStatusListener);
                if (response == NOTIFY_SUCCESS) {
                    L.d("bind ::receive notify security validate success");
                    requestBind(mac);
                } else if (response == NOTIFY_FAIL) {
                    L.d("bind ::receive notify security validate fail");
                    mUiHandle.showBindFail();
                } else if (response == NOTIFY_KEY_EVENT) {
                    L.d("bind ::receive notify event ");
                    hasAcceptKeyEventNotify.set(true);
                    mUiHandle.showBindSuccess();
                } else {
                    L.d("bind :: receive unknown");
//                    mUiHandle.showBindFail();
                }
            }

            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    L.d("bind :: set notifyWatch success");
                } else {
                    L.d("bind :: set notifyWatch fail");
                }
            }
        });
    }

    private void bindFailWithoutKeyEventAfter10(final String mac) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isConnected.get()) return; // connect fail don't handle it , show fail ui
                if (!hasAcceptKeyEventNotify.get()) {
//                    unRegisterBleConStatusListener(mac,mStatusListener);
                    mUiHandle.showBindTimeout();
                    BleMgr.getInstance().disConnect(mac);
                }
            }
        }, KEY_EVENT_WAIT_FOR);// bind fail if > 10s without key event
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

    public void registerBleConStatusListener(String mac, BleConnectStatusListener mBleConnectStatusListener) {
        BleMgr.getInstance().register(mac, mBleConnectStatusListener);
    }


    public void unRegisterBleConStatusListener(String MAC, BleConnectStatusListener mBleConnectStatusListener) {
        BleMgr.getInstance().unRegister(MAC, mBleConnectStatusListener);
    }
}
