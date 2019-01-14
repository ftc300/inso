
package com.inshow.watch.android;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.inshow.watch.android.basic.onBleStateChangedListener;
import com.inshow.watch.android.basic.onDeviceNameChangeListener;
import com.inshow.watch.android.fragment.IStepChangeListener;
import com.inshow.watch.android.tools.L;

import java.util.UUID;

import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_TODAY_STEP;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;

/**
 * Created by chendong on 2017/3/17.
 */

public class WatchBleReceiver extends BroadcastReceiver {
    private onBleStateChangedListener bleStateChangedListener;
    private onDeviceNameChangeListener nameChangedListener;
    private IStepChangeListener stepChangeListener;

    public void setStepChangeListener(IStepChangeListener stepChangeListener) {
        this.stepChangeListener = stepChangeListener;
    }

    public onDeviceNameChangeListener getNameChangedListener() {
        return nameChangedListener;
    }

    public void setNameChangedListener(onDeviceNameChangeListener nameChangedListener) {
        this.nameChangedListener = nameChangedListener;
    }

    public void setBleStateChangedListener(onBleStateChangedListener bleStateChangedListener) {
        this.bleStateChangedListener = bleStateChangedListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
//        if (XmBluetoothManager.ACTION_RENAME_NOTIFY.equalsIgnoreCase(intent.getAction())) {
//            String name = intent.getStringExtra(XmBluetoothManager.EXTRA_NAME);
//            if (nameChangedListener != null && !TextUtils.isEmpty(name))
//                nameChangedListener.onChanged(name);
//        } else if (XmBluetoothManager.ACTION_CHARACTER_CHANGED.equalsIgnoreCase(intent.getAction())) {
//            if (((UUID) intent.getSerializableExtra(XmBluetoothManager.KEY_SERVICE_UUID)).toString().equals(IN_SHOW_SERVICE) &&
//                    ((UUID) intent.getSerializableExtra(XmBluetoothManager.KEY_CHARACTER_UUID)).toString().equals(CHARACTERISTIC_TODAY_STEP) &&
//                    stepChangeListener != null) {
//                stepChangeListener.onChanged(intent.getByteArrayExtra(XmBluetoothManager.KEY_CHARACTER_VALUE));
//            }
//        } else if (XmBluetoothManager.ACTION_CONNECT_STATUS_CHANGED.equalsIgnoreCase(intent.getAction())) {
//            int status = intent.getIntExtra(XmBluetoothManager.KEY_CONNECT_STATUS, XmBluetoothManager.STATUS_UNKNOWN);
//            if (status == XmBluetoothManager.STATUS_DISCONNECTED) {
//                if (bleStateChangedListener != null)
//                    bleStateChangedListener.onDisconnect();
//            }
//        }else
            if(BluetoothAdapter.ACTION_STATE_CHANGED.equalsIgnoreCase(intent.getAction())){
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch(blueState){
                    case BluetoothAdapter.STATE_TURNING_ON:
                        L.e("ACTION_STATE_CHANGED:STATE_TURNING_ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        L.e("ACTION_STATE_CHANGED:STATE_TURNING_OFF");
                        if (bleStateChangedListener != null)
                            bleStateChangedListener.onBleTurnOff();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        L.e("ACTION_STATE_CHANGED:STATE_OFF");

                        break;
                    case BluetoothAdapter.STATE_ON:
                        L.e("ACTION_STATE_CHANGED:STATE_OFF");
                        break;
                }
        }
    }


}