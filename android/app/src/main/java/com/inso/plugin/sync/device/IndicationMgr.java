package com.inso.plugin.sync.device;

import com.inso.core.BleMgr;
import com.inso.plugin.tools.Constants;
import com.inso.plugin.tools.L;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;

import java.util.UUID;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/4/3
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class IndicationMgr {
    public  static  void  indicateWorldCity(String MAC){
        BleMgr.getInstance().indicate(MAC, UUID.fromString(Constants.GattUUIDConstant.IN_SHOW_SERVICE), UUID.fromString(Constants.GattUUIDConstant.CHARACTERISTIC_WORLD_CITY), new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
                L.d(String.format("onNotify service %s character %s value %s",service.toString(),character.toString(),BleMgr.bytes2HexString(value)));
            }

            @Override
            public void onResponse(int code) {
                L.d("onResponse code " +code);

            }
        });
        BleMgr.getInstance().write(MAC, UUID.fromString(Constants.GattUUIDConstant.IN_SHOW_SERVICE), UUID.fromString(Constants.GattUUIDConstant.CHARACTERISTIC_WORLD_CITY),new byte[]{0,0,0,0});
    }
}
