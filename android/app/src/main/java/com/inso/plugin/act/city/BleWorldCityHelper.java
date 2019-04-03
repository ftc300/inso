package com.inso.plugin.act.city;

import com.inso.core.BleMgr;

import java.util.UUID;

import static com.inso.plugin.manager.BleManager.I2B_WorldCity;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.CHARACTERISTIC_SYNC_CURRENT_TIME2;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.CHARACTERISTIC_WORLD_CITY;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/4/3
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class BleWorldCityHelper {

    public static void addCity(String MAC,long cityId,int offset){
        BleMgr.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_WORLD_CITY), I2B_WorldCity(2,0,cityId, offset), null);
    }


    public static void change2DefaultCity(String MAC,long cityId){
        BleMgr.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_WORLD_CITY), I2B_WorldCity(2,1, cityId, 0), null);
    }

    public static void deleteCity(String MAC,long cityId,BleMgr.IWriteResponse iWriteResponse){
        BleMgr.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_WORLD_CITY), I2B_WorldCity(1,0,cityId, 0), iWriteResponse);
    }

    public static void setCurrentTime(String MAC, byte[] bytes, BleMgr.IWriteResponse iWriteResponse){
        BleMgr.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_SYNC_CURRENT_TIME2), bytes, iWriteResponse);
    }

}
