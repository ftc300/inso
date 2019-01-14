package com.inso.plugin.tools;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2018/1/3
 * @ 描述:
 */


public class EfficiencyTool {
    private static EfficiencyTool instance;
    private boolean log = true;
    private static long lastTime = 0;
    private String TAG_START = "start";
    private String TAG_END = "end";
    private String Tag = "";

    public static EfficiencyTool initTool() {
        if (instance == null) {
            instance = new EfficiencyTool();
        }
        return instance;
    }


    public static EfficiencyTool getInstance() {
        if (instance == null) {
            throw new NullPointerException();
        } else {
            return instance;
        }
    }


    public void Log(String tag) {
        if (log && null != tag) {
            Tag = tag;
            if (!tag.endsWith(TAG_START) && !tag.endsWith(TAG_END)) {
                L.e("EfficiencyTool set tag error!");
                return;
            }
            if (tag.endsWith(TAG_START)) {
                lastTime = System.currentTimeMillis();
            } else if (tag.endsWith(TAG_END)) {
                L.e(instance.toString());
            }
        }
    }


    @Override
    public String toString() {
        return "EfficiencyTool" + Tag.replace(TAG_END, "") + "耗时" + (System.currentTimeMillis() - lastTime) + "ms";
    }
}
