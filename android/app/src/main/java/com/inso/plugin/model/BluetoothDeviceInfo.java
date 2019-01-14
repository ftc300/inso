package com.inso.plugin.model;

/**
 * Created by chendong on 2017/2/9.
 * 扫描的蓝牙设备 属性待添加
 */
public class BluetoothDeviceInfo {
    public String deviceName;
    public String deviceMac;
    public BluetoothDeviceInfo(String deviceMac, String deviceName) {
        this.deviceMac = deviceMac;
        this.deviceName = deviceName;
    }
}
