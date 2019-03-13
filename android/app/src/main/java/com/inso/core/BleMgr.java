package com.inso.core;

import com.inso.App;
import com.inso.plugin.tools.L;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleReadRssiResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.response.SearchResponse;

import java.util.UUID;

import static com.inuker.bluetooth.library.Code.REQUEST_SUCCESS;

public class BleMgr {

    private static BleMgr mInstance;
    private static BluetoothClient mClient;

    private static void  initClient() {
        if (mClient == null) {
            synchronized (BleMgr.class) {
                if (mClient == null) {
                    mClient = new BluetoothClient(App.getInstance());
                }
            }
        }
    }

    private BleMgr() {
    }

    public static BleMgr getInstance() {
        initClient();
        if (mInstance == null) {
            mInstance = new BleMgr();
        }
        return mInstance;
    }

    // Constants.STATUS_UNKNOWN
    // Constants.STATUS_DEVICE_CONNECTED
    // Constants.STATUS_DEVICE_CONNECTING
    // Constants.STATUS_DEVICE_DISCONNECTING
    // Constants.STATUS_DEVICE_DISCONNECTED

    public boolean isBluetoothOpen(){
        return mClient.isBluetoothOpened();
    }

    public void openBluetoothSilently(){
        mClient.openBluetooth();
    }

    public  int getBleState(String MAC){
        return mClient.getConnectStatus(MAC);
    }

    public  boolean isConnected(String MAC){
        return mClient.getConnectStatus(MAC) == Constants.STATUS_DEVICE_CONNECTED;
    }


    public void register(String MAC,BleConnectStatusListener mBleConnectStatusListener ){
        mClient.registerConnectStatusListener(MAC, mBleConnectStatusListener);
    }


    public void unRegister(String MAC,BleConnectStatusListener mBleConnectStatusListener ){
        mClient.unregisterConnectStatusListener(MAC, mBleConnectStatusListener);
    }


    public void connect(String MAC,BleConnectResponse response) {
        mClient.connect(MAC, response);
    }

    public void disConnect(String MAC) {
        mClient.disconnect(MAC);
    }

    public void readRssi(String mac,BleReadRssiResponse response){
        mClient.readRssi(mac,response);
    }

    public void search(SearchRequest request, SearchResponse response){
        mClient.search(request, response);
    }
    public void stopSearch(){
        mClient.stopSearch();
    }

    public void notify(String mac, UUID service, UUID character,BleNotifyResponse response) {
        mClient.notify(mac, service, character,response);
    }
    public void unNotify(String mac, UUID service, UUID character,BleUnnotifyResponse response) {
        mClient.unnotify(mac, service, character,response);
    }

    public void read(String MAC, final UUID serviceUUID, final UUID characterUUID, final IReadOnResponse readOnResponse) {
        mClient.read(MAC, serviceUUID, characterUUID, new BleReadResponse() {
            @Override
            public void onResponse(int code, byte[] data) {
                if (code == REQUEST_SUCCESS) {
                    L.d("serviceUUID:" + serviceUUID.toString() + ",characterUUID :" + characterUUID.toString()+ ",read:" + bytes2HexString(data));
                    readOnResponse.onSuccess(data);
                }else {
                    readOnResponse.onFail();
                }
            }
        });
    }


    public void write(String MAC, UUID serviceUUID, final UUID characterUUID, final byte[] bytes) {
        mClient.write(MAC, serviceUUID, characterUUID, bytes, new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    L.d(characterUUID.toString() + ",writeCharacteristic success :"  + bytes2HexString(bytes));
                }else {
                    L.d(characterUUID.toString()  +",writeCharacteristic fail" );
                }
            }
        });
    }

    public void write(String MAC, UUID serviceUUID, final UUID characterUUID, final byte[] bytes,final IWriteResponse writeResponse) {
        mClient.write(MAC, serviceUUID, characterUUID, bytes, new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    L.d(characterUUID.toString() + ",writeCharacteristic success :"  + bytes2HexString(bytes));
                    writeResponse.onSuccess();
                }else {
                    L.d(characterUUID.toString()  +",writeCharacteristic fail" );
                    writeResponse.onFail();
                }
            }
        });
    }



    public void clear(String MAC){
        mClient.clearRequest(MAC, Constants.REQUEST_READ);
        // Constants.REQUEST_READ，所有读请求
        // Constants.REQUEST_WRITE，所有写请求
        // Constants.REQUEST_NOTIFY，所有通知相关的请求
        // Constants.REQUEST_RSSI，所有读信号强度的请求
    }


    public interface IReadOnResponse{
        void onSuccess(byte[] data);
        void onFail();
    }

    public interface IWriteResponse{
        void onSuccess();
        void onFail();
    }

    public static String bytes2HexString(byte[] bytes) {
        if (bytes == null) return "";
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            String hexString = Integer.toHexString(bytes[i] & 0x0FF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result += hexString.toUpperCase();
        }
        return result;
    }

}
