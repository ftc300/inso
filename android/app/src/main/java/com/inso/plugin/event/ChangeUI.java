package com.inso.plugin.event;

/**
 * Created by chendong on 2017/3/16.
 */
public class ChangeUI {
    public static final int CONNECT_ING = 0;
    public static final int CONNECT_SUCCESS = 1;
    public static final int CONNECT_FAIL = 2;
    public static final int CONNECT_DFU = 3;
    public static final String RENDER_AGAIN = "RENDER_AGAIN";
    public static final String CONNECT_AGAIN = "CONNECT_AGAIN";
    public static final String SYNC_BIND_RENDER = "SYNC_BIND_RENDER";
    public String action;
    public int btCode; //0:dis;1:con

    public ChangeUI(String action) {
        this.action = action;
    }

}
