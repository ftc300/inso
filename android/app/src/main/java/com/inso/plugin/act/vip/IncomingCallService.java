package com.inso.plugin.act.vip;

import android.content.Intent;

import com.inso.core.BleMgr;
import com.inso.plugin.manager.SPManager;
import com.inso.plugin.tools.Constants;
import com.inso.plugin.tools.L;
import com.inso.watch.phonelib.PhoneCallService;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import static com.inso.plugin.tools.Constants.GattUUIDConstant.ALERT_NOTIFICATION_SERVICE;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.CHARACTERISTIC_NEW_ALERT;

/**
 * Comment:
 * Author: ftc300
 * Date: 2018/11/2
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class IncomingCallService extends PhoneCallService {

    private final int SIMPLE_ALERT = 0;
    private final int EMAIL = 1;
    private final int NEWS = 2;
    private final int CALL = 3;
    private final int MISSED_CALL = 4;
    private final int SMS = 5;
    private final int VOICE_MAIL = 6;
    private final int SCHEDULE = 7;
    private final int HIGH_PRIORITIZED_ALERT = 8;
    private final int INSTANT_MESSAGING = 9;
    private String mac;
    private int numberOfAlert = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.d( "onStartCommand action: " + intent.getAction() + " flags: " + flags + " startId: " + startId);
        registerPhoneStateListener();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onIncomingCallStarted(String number, Date start) {
        L.d("IncomingCallService onIncomingCallStarted starttime is " + start.getTime() + " number is " + number);
        setIncomingPhoneState(true,number);
    }

    //pick up
    @Override
    public void onIncomingCallEnded(String number, Date start, Date end) {
        L.d("IncomingCallService onIncomingCallEnded starttime is " + start.getTime() + " number is " + number + "endtime is " + end);
    }

    @Override
    public void onMissedCall(String number, Date start) {
        L.d("IncomingCallService onIncomingCallStarted onMissedCall is " + start.getTime() + " number is " + number );
        super.onMissedCall(number, start);
        setIncomingPhoneState(false,number);
    }

    @Override
    public void onPickUp(String number, Date end) {
        super.onPickUp(number, end);
        setIncomingPhoneState(false,number);
    }


    private void setIncomingPhoneState(boolean isStarted,String number){
        mac = (String) SPManager.get(this, Constants.SystemConstant.SP_ARG_MAC,"");
        if(isStarted) {
            if (BleMgr.getInstance().isConnected(mac)) {
                byte[] param = new byte[]{CALL, 1};
                byte[] bytes = number.getBytes(StandardCharsets.UTF_8);
                byte[] data = new byte[param.length + bytes.length];
                System.arraycopy(param, 0, data, 0, param.length);
                System.arraycopy(bytes, 0, data, param.length, bytes.length);
                L.d("IncomingCallService onIncomingCallStarted set data " + BleMgr.bytes2HexString(data));
                BleMgr.getInstance().write(mac, UUID.fromString(ALERT_NOTIFICATION_SERVICE), UUID.fromString(CHARACTERISTIC_NEW_ALERT), data);
            }
        }else {
            if(BleMgr.getInstance().isConnected(mac)){
                BleMgr.getInstance().write(mac, UUID.fromString(ALERT_NOTIFICATION_SERVICE), UUID.fromString(CHARACTERISTIC_NEW_ALERT),new byte[]{3,0,0});
            }
        }
    }
}
