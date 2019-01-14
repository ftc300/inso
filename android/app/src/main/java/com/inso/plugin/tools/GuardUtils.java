package com.inso.plugin.tools;

/**
 * Created by chendong on 2017/6/22.
 * 更多设置时读取数据有时比较慢 防止多次打开页面
 * 防止测试妹子连续快速触发某事件
 */
public class GuardUtils {
    private static long lastClickTime = 0;
    private static long DIFF = 1000;
    private static int lastButtonId = -1;

    /** * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击 * * @return */
    public static boolean isPrevented() {
        return isPrevented(-1, DIFF);
    }

    /** * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击 * * @return */
    public static boolean isPrevented(int buttonId) {
        return isPrevented(buttonId, DIFF);
    }

    /** * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击 * * @return */
    public static boolean isAllowed(int buttonId) {
        return !isPrevented(buttonId, DIFF);
    }

    public static boolean isAllowed(int viewId,long diff) {
        return !isPrevented(viewId, diff);
    }


    /** * 判断两次点击的间隔，如果小于diff，则认为是多次无效点击 * * @param diff * @return */
    public static boolean isPrevented(int buttonId, long diff) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (lastButtonId == buttonId && lastClickTime > 0 && timeD < diff) {
            return true;
        }
        lastClickTime = time;
        lastButtonId = buttonId;
        return false;
    }
}
