package com.inso.core.msg;

import android.content.Context;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.ITelephony;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/4/2
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class PhoneCallUtil {

    public static void endPhone(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        Method method = null;
        try {
            method = TelephonyManager.class.getDeclaredMethod("getITelephony");
            method.setAccessible(true);
            ITelephony telephony = (ITelephony) method.invoke(telephonyManager);
            telephony.endCall();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
