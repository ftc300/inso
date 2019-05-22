package com.inso.service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import com.inso.core.BleMgr;
import com.inso.core.HttpMgr;
import com.inso.entity.http.post.Interval;
import com.inso.plugin.manager.SPManager;
import com.inso.plugin.tools.Constants;
import com.inso.plugin.tools.L;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.inso.plugin.manager.BleManager.getIntervalIndicate;
import static com.inso.plugin.manager.BleManager.setWriteIntervalByte;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.CHARACTERISTIC_INTERVAL_REMIND;
import static com.inso.plugin.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;
import static com.inso.plugin.tools.Constants.SystemConstant.SP_ARG_MAC;
import static com.inso.watch.baselib.Constants.BASE_URL;

/**
 * 杀死app后需要重新开启通知使用权限 bug？
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationService extends NotificationListenerService {
    private static final String TAG = "NotificationService";
    public static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static NotificationService self;
    private static NotificationListener notificationListener;

    /*----------------- 静态方法 -----------------*/
    public synchronized static void startNotificationService(Context context, NotificationListener notificationListener) {
        NotificationService.notificationListener = notificationListener;
        context.startService(new Intent(context, NotificationService.class));
    }

    public synchronized static void stopNotificationService(Context context) {
        context.stopService(new Intent(context, NotificationService.class));
    }


    public static void startNotificationListenSettings(Context context) {
        Intent intent = new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static boolean isNotificationListenEnable(Context context) {
        return isNotificationListenEnable(context, context.getPackageName());
    }

    public static boolean isNotificationListenEnable(Context context, String pkgName) {
        final String flat = Settings.Secure.getString(context.getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*----------------- 生命周期 -----------------*/
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate..");

        if (notificationListener != null) {
            notificationListener.onServiceCreated(this);
        }
        self = this;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ensureCollectorRunning();
            }
        });
        indicateIntervalReminder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand..");

        return notificationListener == null ? START_STICKY : notificationListener.onServiceStartCommand(this, intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy..");

        if (notificationListener != null) {
            notificationListener.onServiceDestroy();
            notificationListener = null;
        }
        self = null;
    }

    //确认NotificationMonitor是否开启
    private void ensureCollectorRunning() {
        ComponentName collectorComponent = new ComponentName(this, NotificationService.class);
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        boolean collectorRunning = false;
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
        if (runningServices == null) {
            return;
        }
        for (ActivityManager.RunningServiceInfo service : runningServices) {
            if (service.service.equals(collectorComponent)) {
                if (service.pid == android.os.Process.myPid()) {
                    collectorRunning = true;
                }
            }
        }
        if (collectorRunning) {
            return;
        }
        toggleNotificationListenerService();
    }

    //重新开启NotificationMonitor
    private void toggleNotificationListenerService() {
        ComponentName thisComponent = new ComponentName(this, NotificationService.class);
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }

    /*----------------- 通知回调 -----------------*/
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        Log.i(TAG, notification.toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Bundle bundle = notification.extras;
            String content = "";
            for (String key : bundle.keySet()) {
                Log.i(TAG, key + ": " + bundle.get(key));
                content.concat(bundle.get(key).toString());
            }
            content = bundle.get("android.text").toString();
            L.d("bundle.get(key):"+content);
            if (!content.contains("间隔提醒")) return;
            if (content.contains("关闭")) {
                closeWatchInterval();
            }else if( content.contains("开启")){
                String regEx="[^0-9]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(content);
                String number = m.replaceAll("").trim();
                if(TextUtils.isEmpty(number)&& content.contains("整点")){
                    setWatchInterval(61);
                }else if(Integer.parseInt(number) >=5 && Integer.parseInt(number) <=60){
                    setWatchInterval(Integer.parseInt(number));
                }
            }

        }
        if (self != null && notificationListener != null) {
            notificationListener.onNotificationPosted(sbn);
        }
    }


    private void setWatchInterval(final int min) {
        final String MAC = (String) SPManager.get(self, SP_ARG_MAC, "");
        BleMgr.getInstance().connect(MAC, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                int[] inputs = new int[4];
                inputs[0] = 2;
                inputs[1] = 0;
                inputs[2] = min; //min
                inputs[3] = min * 60; //s 整点就是61*60
                BleMgr.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_INTERVAL_REMIND), setWriteIntervalByte(inputs));
            }
        });
    }


    public void closeWatchInterval() {
        final String MAC = (String) SPManager.get(self, SP_ARG_MAC, "");
        BleMgr.getInstance().connect(MAC, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                int[] inputs = new int[4];
                inputs[0] = 3;
                inputs[1] = 0;
                inputs[2] = 0;
                inputs[3] = 0;
                BleMgr.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_INTERVAL_REMIND), setWriteIntervalByte(inputs));
            }
            });
    }

    public void indicateIntervalReminder(){
        final String MAC = (String) SPManager.get(self, SP_ARG_MAC, "");
        BleMgr.getInstance().connect(MAC, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                L.d("indicateIntervalReminder");
                BleMgr.getInstance().indicate(MAC, UUID.fromString(Constants.GattUUIDConstant.IN_SHOW_SERVICE), UUID.fromString(Constants.GattUUIDConstant.CHARACTERISTIC_INTERVAL_REMIND), new BleNotifyResponse() {
                    @Override
                    public void onNotify(UUID service, UUID character, byte[] value) {
//                        000230400B
                        L.d(String.format("onNotify service %s character %s value %s",service.toString(),character.toString(),BleMgr.bytes2HexString(value)));
                        int[] indication  = getIntervalIndicate(value);
                        pushIntervalReminder2Server(indication);
                    }

                    @Override
                    public void onResponse(int code) {
                    }
                });
            }
        });
    }

    /**
     /v1/flowy/client
     status:on/off
     time: xx （单位分钟)
     **/
    void pushIntervalReminder2Server(int[] indication){
        HttpMgr.postStringRequest(self, BASE_URL + "flowy/client" , new Interval(indication[1] == 2?"on":"off",indication[2]), new HttpMgr.IResponse<String>() {
            @Override
            public void onSuccess(final String obj) {
                L.d("postStringRequest  /v1/flowy/client onSuccess " + obj);
            }

            @Override
            public void onFail() {
                L.d("postStringRequest /v1/flowy/client  onFail ");
            }
        });
    }


    @Override
    public void onNotificationRemoved (StatusBarNotification sbn){
            if (self != null && notificationListener != null) {
                notificationListener.onNotificationRemoved(sbn);
            }
        }

    public void printCurrentNotifications() {
        StatusBarNotification[] ns = getActiveNotifications();
        for (StatusBarNotification n : ns) {
            Log.i(TAG, String.format("%20s", n.getPackageName()) + ": " + n.getNotification().tickerText);
        }
    }


    public static abstract class NotificationListener {
        public void onServiceCreated(NotificationService service) {
        }

        public abstract int onServiceStartCommand(NotificationService service, Intent intent, int flags, int startId);

        public void onServiceDestroy() {
        }

        /**
         * Implement this method to learn about new notifications as they are posted by apps.
         *
         * @param sbn A data structure encapsulating the original {@link android.app.Notification}
         *            object as well as its identifying information (tag and id) and source
         *            (package name).
         */
        public abstract void onNotificationPosted(StatusBarNotification sbn);

        /**
         * Implement this method to learn when notifications are removed.
         * <p/>
         * This might occur because the user has dismissed the notification using system UI (or another
         * notification listener) or because the app has withdrawn the notification.
         * <p/>
         * NOTE: The {@link StatusBarNotification} object you receive will be "light"; that is, the
         * result from {@link StatusBarNotification#getNotification} may be missing some heavyweight
         * fields such as {@link android.app.Notification#contentView} and
         * {@link android.app.Notification#largeIcon}. However, all other fields on
         * {@link StatusBarNotification}, sufficient to match this call with a prior call to
         * {@link #onNotificationPosted(StatusBarNotification)}, will be intact.
         *
         * @param sbn A data structure encapsulating at least the original information (tag and id)
         *            and source (package name) used to post the {@link android.app.Notification} that
         *            was just removed.
         */
        public abstract void onNotificationRemoved(StatusBarNotification sbn);
    }
}