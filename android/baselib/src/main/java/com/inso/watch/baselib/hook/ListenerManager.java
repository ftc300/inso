package com.inso.watch.baselib.hook;

/**
 * Created by wulei
 * Data: 2016/10/18.
 */

public class ListenerManager {
    public HookListenerContract.OnClickListener mOnClickListener;

    private ListenerManager(){};

    public static ListenerManager create(Builder builder){
        if (builder == null){
            return null;
        }
        return builder.build();
    }

    public static class Builder {
        private ListenerManager listenerManager = new ListenerManager();


        public Builder buildOnClickListener(HookListenerContract.OnClickListener onClickListener){
            listenerManager.mOnClickListener = onClickListener;
            return this;
        }

        public ListenerManager build(){
            return listenerManager;
        }
    }
}
