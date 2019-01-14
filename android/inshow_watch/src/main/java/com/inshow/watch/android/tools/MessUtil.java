package com.inshow.watch.android.tools;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.inshow.watch.android.R;
import com.inshow.watch.android.act.alarm.IOnceAlarm;
import com.inshow.watch.android.dao.AlarmDao;
import com.inshow.watch.android.manager.SPManager;
import com.inshow.watch.android.model.PickVipEntity;
import com.inshow.watch.android.provider.DBHelper;
import com.xiaomi.smarthome.bluetooth.BleUpgrader;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;
import com.xiaomi.smarthome.common.ui.dialog.MLAlertDialog;
import com.xiaomi.smarthome.device.api.IXmPluginHostActivity;
import com.xiaomi.smarthome.device.api.XmPluginHostApi;
import com.xiaomi.smarthome.plugin.service.PluginServiceHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;


import static android.content.Context.NOTIFICATION_SERVICE;
import static com.inshow.watch.android.provider.DBHelper.DATABASENAME;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_DEBUG_FLAG;
import static com.inshow.watch.android.tools.Constants.SystemConstant.SP_IS_FIRST_OPEN;
import static com.xiaomi.smarthome.bluetooth.XmBluetoothManager.ALERT_INCALL_IN_CONTACTS_ENABLED;
import static com.xiaomi.smarthome.bluetooth.XmBluetoothManager.ALERT_INCALL_NO_CONTACTS_ENABLED;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/9/12
 * @ 描述:     很杂的方法
 */
public class MessUtil {

    public static final int MENU_CODE = 11;
    public static final String STDVERSION = "1.0.5_15";
    private static final String VIP_CHANGE_VERSION = "1.0.5_23";


    /**
     * 检测响一次闹钟是否已经过期
     *
     * @param dbHelper
     */
    public static List<AlarmDao> checkOnceAlarm(DBHelper dbHelper, IOnceAlarm onceAlarm) {
        List<AlarmDao> dataSource = dbHelper.getAllAlarm();
        for (AlarmDao item : dataSource) {
            if (item.repeatType.length() == 1 && Integer.parseInt(item.repeatType) == 0 && item.status) {
                L.e("checkOnceAlarm item.status = " + item.status);
                if (TimeUtil.getNowTimeSeconds(dbHelper.getSettingZone()) >= item.extend) {
                    L.e("checkOnceAlarm && updateAlarmClock");
                    dbHelper.updateAlarmClock(item.id, false);
                    item.status = false;
                    if (null != onceAlarm) {
                        onceAlarm.onStateChanged();
                    }
                }
            }
        }
        return dataSource;
    }


    /**
     * 服务端规则：
     * 0: 响一次；1: 每天；2: 法定工作日；3: 法定节假日；4: 周一；5: 周二；6: 周三；7: 周四；8: 周五；9: 周六；10: 周日
     * 自己以前制定的规则：
     * 0: 响一次；1: 每天；2: 法定工作日；3: 法定节假日；4:周一至周五；5:自定义 eg:（周一|周二）,(5,0,1)
     *
     * @param type
     * @return
     */
    public static List<Integer> getListType(String type) {
        List<Integer> ret = new ArrayList<>();
        String[] src = type.split(",");
        if (src.length == 1) {
            if (Integer.parseInt(src[0]) < 4) {
                ret.add(Integer.parseInt(src[0]));
                return ret;
            } else {//等于4
                for (int i = 4; i < 9; i++) {
                    ret.add(i);
                }
                return ret;
            }
        }
        for (int i = 1; i < src.length; i++) {
            ret.add(Integer.parseInt(src[i]) + 4);
        }
        return ret;
    }

    public static ArrayList<PickVipEntity> getSystemContact(Context context) {
        ArrayList<PickVipEntity> dataSrc = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(number)) {
                        number = number.replaceAll("^(\\+86)", "");
                        number = number.replaceAll("^(86)", "");
                        number = number.replaceAll("-", "");
                        number = number.replaceAll(" ", "");
                        number = number.trim();
                        //TODO:检查号码是否是手机号码？
                        PickVipEntity entity = new PickVipEntity(contactId, number, name, false);
                        if (dataSrc.size() == 0) {
                            dataSrc.add(entity);
                        } else {
                            //按名字排序后一个相同的话不添加
                            PickVipEntity lastItem = dataSrc.get(dataSrc.size() - 1);
                            if (!TextUtils.equals(lastItem.name, name) || !TextUtils.equals(lastItem.number, number)) {
                                dataSrc.add(entity);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            L.e(e.getMessage());
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
            }
            return new ArrayList<>(dataSrc);
        }

    }

    /**
     * 表示是否打开绑定miui来电提醒
     *
     * @param context
     * @param MAC
     * @param isNeedOpen
     */
    public static void bindOrNot(Context context, String MAC, boolean isNeedOpen) {
        try {
            if (Rom.isMIUI()) {
                if (isNeedOpen) {
                    XmBluetoothManager.getInstance().bindDevice(MAC);
                    L.e("bindDevice");
                } else {
                    XmBluetoothManager.getInstance().unBindDevice(MAC);
                    L.e("unBindDevice");
                }
                XmBluetoothManager.getInstance().setAlertConfigs(MAC, ALERT_INCALL_IN_CONTACTS_ENABLED, isNeedOpen);
                XmBluetoothManager.getInstance().setAlertConfigs(MAC, ALERT_INCALL_NO_CONTACTS_ENABLED, isNeedOpen);
            }
        } catch (NoSuchMethodError e) {
            new MLAlertDialog.Builder(context)
                    .setCancelable(false)
                    .setMessage(context.getString(R.string.vip_error))
                    .setPositiveButton(context.getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
        }
    }


    public static void startColorGradientAnim(final View view, int from, int to) {
        final ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), from, to);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color = (int) animation.getAnimatedValue();
                view.setBackgroundColor(color);
            }
        });
        colorAnimator.setDuration(1000);
        colorAnimator.start();
    }

    /**
     * 首页低电量 闪动3次
     *
     * @param v
     */
    public static void setFlickerAnimation(View v) {
        final Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(1200);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(3);
        animation.setRepeatMode(Animation.REVERSE);
        v.setAnimation(animation);
    }

    public static String getDataBaseName(final String userID) {
        return Configuration.ServerHandle(new Configuration.ServerHandler() {
            @Override
            public String defaultServer() {
                return "cn_" + userID + "_" + DATABASENAME;
            }

            @Override
            public String cnServer() {
                return "cn_" + userID + "_" + DATABASENAME;
            }

            @Override
            public String twServer() {
                return "tw_" + userID + "_" + DATABASENAME;
            }

            @Override
            public String hkServer() {
                return "hk_" + userID + "_" + DATABASENAME;
            }
        });
    }

    /**
     * 列表divider的margin设置
     *
     * @param v
     */
    public static void setMarginsZero(View v) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                p.setMarginStart(0);
                p.setMarginEnd(0);
            }
            v.requestLayout();
        }
    }


    public static String getIntString(String s) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        return p.matcher(s).replaceAll("").trim();
    }

    /**
     * dfu 升级 mac地址加1
     *
     * @param deviceAddress
     * @return
     */
    public static String getIncrementedAddress(final String deviceAddress) {
        final String firstBytes = deviceAddress.substring(0, 15);
        final String lastByte = deviceAddress.substring(15); // assuming that the device address is correct
        final String lastByteIncremented = String.format("%02X", (Integer.valueOf(lastByte, 16) + BootloaderScanner.ADDRESS_DIFF) & 0xFF);
        return firstBytes + lastByteIncremented;
    }


    /**
     * 转到自定义以及米家捆绑设定页
     */
//    public static void switchToSetting(IXmPluginHostActivity mHostActivity, BleUpgrader startUpgrade) {
//        ArrayList<IXmPluginHostActivity.MenuItemBase> items = new ArrayList<>();
//        IXmPluginHostActivity.BleMenuItem bleMenu = IXmPluginHostActivity.BleMenuItem.newUpgraderItem(startUpgrade);
//        items.add(bleMenu);
//        Intent intent = new Intent();
//        intent.putExtra("share_enable", false);
//        mHostActivity.openMoreMenu(items, true, 100, intent);
//    }
    public static void openMenu(final Context context, IXmPluginHostActivity mHostActivity, String[] menus, BleUpgrader startUpgrade, final String userID) {
        ArrayList<IXmPluginHostActivity.MenuItemBase> items = new ArrayList<>();
        IXmPluginHostActivity.BleMenuItem bleMenu = IXmPluginHostActivity.BleMenuItem.newUpgraderItem(startUpgrade);
        items.add(bleMenu);
        IXmPluginHostActivity.StringMenuItem stringMenu0 = new IXmPluginHostActivity.StringMenuItem();
        stringMenu0.name = menus[0];
        items.add(stringMenu0);
        IXmPluginHostActivity.StringMenuItem stringMenu1 = new IXmPluginHostActivity.StringMenuItem();
        stringMenu1.name = menus[1];
        items.add(stringMenu1);
        IXmPluginHostActivity.StringMenuItem stringMenu2 = new IXmPluginHostActivity.StringMenuItem();
        stringMenu2.name = menus[2];
        items.add(stringMenu2);
        IXmPluginHostActivity.StringMenuItem stringMenu3 = new IXmPluginHostActivity.StringMenuItem();
        stringMenu3.name = menus[3];
        items.add(stringMenu3);
//        IXmPluginHostActivity.StringMenuItem stringMenu4 = new IXmPluginHostActivity.StringMenuItem();
//        stringMenu4.name = menus[4];
//        items.add(stringMenu4);

//        https://github.com/MiEcosystem/NewXmPluginSDK/blob/master/API%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3/%E5%85%AC%E5%85%B1%E8%AE%BE%E5%A4%87%E8%8F%9C%E5%8D%95API.md
//        IXmPluginHostActivity.StringMenuItem stringMenu5 = new IXmPluginHostActivity.StringMenuItem();
//        stringMenu5.name = menus[5];
//        items.add(stringMenu5);
        L.e(" SPManager.get(context, SP_DEBUG_FLAG, false)：" + SPManager.get(context, SP_DEBUG_FLAG, false));
        if ((Boolean) SPManager.get(context, SP_DEBUG_FLAG, false)) {
            IXmPluginHostActivity.StringMenuItem stringMenu6 = new IXmPluginHostActivity.StringMenuItem();
            stringMenu6.name = menus[6];
            items.add(stringMenu6);
        }

        Intent intent = new Intent();
        intent.putExtra("scence_enable", false);
        intent.putExtra("share_enable", false);
//        intent.putExtra("help_feedback_enable", false);
        final Intent commonSetting = new Intent();
        Configuration.getInstance().ServerHandle(new Configuration.ServerHandler2() {
            @Override
            public void defaultServer() {
                L.d("into defaultServer ");
                commonSetting.putExtra("enableRemoveLicense", true);
//                if (XmPluginHostApi.instance().getApiLevel() >= 67) {
//                    commonSetting.putExtra("licenseContent", getHtmlLicenseContent(context));
//                    commonSetting.putExtra("privacyContent", getHtmlPrivacyContent(context));
//                } else
                if (XmPluginHostApi.instance().getApiLevel() >= 48) {
                    commonSetting.putExtra("licenseContent", Html.fromHtml(context.getString(R.string.inshow_protocol)));
                    commonSetting.putExtra("privacyContent", Html.fromHtml(context.getString(R.string.inshow_privacy)));
                }
                L.d("out defaultServer ");
            }

            @Override
            public void cnServer() {
                if (TextUtils.equals("1263338353", userID) || TextUtils.equals("78377019", userID)) {
                    commonSetting.putExtra("enableRemoveLicense", true);
//                    if (XmPluginHostApi.instance().getApiLevel() >= 67) {
//                        commonSetting.putExtra("licenseContent", getHtmlLicenseContent(context));
//                        commonSetting.putExtra("privacyContent", getHtmlPrivacyContent(context));
//                    } else
                    if (XmPluginHostApi.instance().getApiLevel() >= 48) {
                        commonSetting.putExtra("licenseContent", Html.fromHtml(context.getString(R.string.inshow_protocol)));
                        commonSetting.putExtra("privacyContent", Html.fromHtml(context.getString(R.string.inshow_privacy)));
                    }
                }
            }
        });
        mHostActivity.openMoreMenu2(items, true, MENU_CODE, intent, commonSetting);
    }


    //    public static void checkIsFirstOpen(final Context context, IXmPluginHostActivity mHostActivity) {
//        L.e("SP_IS_FIRST_OPEN：" + (Boolean) SPManager.get(context, SP_IS_FIRST_OPEN, true));
//        if ((Boolean) SPManager.get(context, SP_IS_FIRST_OPEN, true)) {
//            if (XmPluginHostApi.instance().getApiLevel() >= 48) {
//                mHostActivity.showUserLicenseDialog(context.getString(R.string.useragreement_privacypolicy), context.getString(R.string.user_agreement), Html.fromHtml(context.getString(R.string.inshow_protocol)),
//                        context.getString(R.string.privacy_policy), Html.fromHtml(context.getString(R.string.inshow_privacy)), new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                SPManager.put(context, SP_IS_FIRST_OPEN, false);
//                            }
//                        });
//            }
//        }
//    }
    public static void checkIsFirstOpen(final Context context, IXmPluginHostActivity mHostActivity) {
        L.e("SP_IS_FIRST_OPEN：" + (Boolean) SPManager.get(context, SP_IS_FIRST_OPEN, true));
        if ((Boolean) SPManager.get(context, SP_IS_FIRST_OPEN, true)) {
//            if (XmPluginHostApi.instance().getApiLevel() >= 67) {
//                mHostActivity.showUserLicenseHtmlDialog(context.getString(R.string.useragreement_privacypolicy), context.getString(R.string.user_agreement), getHtmlLicenseContent(context),
//                        context.getString(R.string.privacy_policy), getHtmlPrivacyContent(context), new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                SPManager.put(context, SP_IS_FIRST_OPEN, false);
//                            }
//                        });
//            } else
            if (XmPluginHostApi.instance().getApiLevel() >= 48) {
                mHostActivity.showUserLicenseDialog(context.getString(R.string.useragreement_privacypolicy), context.getString(R.string.user_agreement), Html.fromHtml(context.getString(R.string.inshow_protocol)),
                        context.getString(R.string.privacy_policy), Html.fromHtml(context.getString(R.string.inshow_privacy)), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SPManager.put(context, SP_IS_FIRST_OPEN, false);
                            }
                        });
            } else {
                L.e("checkIsFirstOpen XmPluginHostApi.instance().getApiLevel() < 48");
            }
        }
    }

    private static String getHtmlLicenseContent(Context context) {
        InputStream is = null;
        try {
            is = context.getAssets().open("inshow_license.html");
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            return new String(buffer, "utf-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static String getHtmlPrivacyContent(Context context) {
        InputStream is = null;
        try {
            is = context.getAssets().open("inshow_privacy.html");
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            return new String(buffer, "utf-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public static void openMenuLess52(Context context, IXmPluginHostActivity mHostActivity, String[] menus, BleUpgrader startUpgrade) {
        ArrayList<IXmPluginHostActivity.MenuItemBase> items = new ArrayList<>();
        IXmPluginHostActivity.BleMenuItem bleMenu = IXmPluginHostActivity.BleMenuItem.newUpgraderItem(startUpgrade);
        items.add(bleMenu);
        IXmPluginHostActivity.StringMenuItem stringMenu0 = new IXmPluginHostActivity.StringMenuItem();
        stringMenu0.name = menus[0];
        items.add(stringMenu0);
        IXmPluginHostActivity.StringMenuItem stringMenu1 = new IXmPluginHostActivity.StringMenuItem();
        stringMenu1.name = menus[1];
        items.add(stringMenu1);
        IXmPluginHostActivity.StringMenuItem stringMenu2 = new IXmPluginHostActivity.StringMenuItem();
        stringMenu2.name = menus[2];
        items.add(stringMenu2);
        IXmPluginHostActivity.StringMenuItem stringMenu3 = new IXmPluginHostActivity.StringMenuItem();
        stringMenu3.name = menus[3];
        items.add(stringMenu3);
//        IXmPluginHostActivity.StringMenuItem stringMenu4 = new IXmPluginHostActivity.StringMenuItem();
//        stringMenu4.name = menus[4];
//        items.add(stringMenu4);
//        IXmPluginHostActivity.StringMenuItem stringMenu5 = new IXmPluginHostActivity.StringMenuItem();
//        stringMenu5.name = menus[5];
//        items.add(stringMenu5);
        if ((Boolean) SPManager.get(context, SP_DEBUG_FLAG, false)) {
            IXmPluginHostActivity.StringMenuItem stringMenu6 = new IXmPluginHostActivity.StringMenuItem();
            stringMenu6.name = menus[6];
            items.add(stringMenu6);
        }
        Intent intent = new Intent();
        intent.putExtra("scence_enable", false);
        intent.putExtra("share_enable", false);
//        intent.putExtra("help_feedback_enable", false);
        mHostActivity.openMoreMenu2(items, true, MENU_CODE, intent);
    }

    public static boolean isWhiteList(String userId) {
        String[] arr = new String[]{
                "10657439",
                "430208357",
                "1266553736",
                "1213095807",
                "78377019",
                "9430558",
                "3546804",
                "1272566668",
                "1263338353",
                "9502890",
                "1116742423",
                "4590400",
                "1303482558",
                "957557539",
                "338831105",
                "1255760948",
                "381989929",
                "298395327",
                "36216959",
                "26968055",
                "1235096620",
                "193454169"};
        return Arrays.asList(arr).contains(userId);
    }

    public static boolean forceUpgrade(String v) {
        try {
            int[] std = getVersionSum(STDVERSION);
            int[] currentV = getVersionSum(v);
            if (currentV[0] < std[0]) {
                return true;
            } else if (currentV[0] == std[0] && currentV[1] < std[1]) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //1.0.4_8 =>[108,8]
    public static int[] getVersionSum(String v) {
        String[] s = v.split("_");
        String t = s[0].replace(".", "");
        return new int[]{Integer.parseInt(t), Integer.parseInt(s[1])};
    }


    public interface IVip {
        void lowerThanVipChangVersion();
        void largerThan();
    }

    public static void checkAndHandleVipChange(String currentV, IVip vip) {
        try {
            int[] std = getVersionSum(VIP_CHANGE_VERSION);
            int[] cV = getVersionSum(currentV);
            if ((cV[0] < std[0]) || (cV[0] == std[0] && cV[1] <= std[1])) {
                vip.lowerThanVipChangVersion();
            }else {
                vip.largerThan();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void showVipOta(Context mContext ){
        L.d("showVipOta");
        if(Rom.isMIUI()) {
            new MLAlertDialog.Builder(mContext)
                    .setCancelable(false)
                    .setMessage(mContext.getString(R.string.vip_change_ota))
                    .setPositiveButton(mContext.getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
        }
    }

}
