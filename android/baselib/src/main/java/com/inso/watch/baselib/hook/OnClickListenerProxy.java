package com.inso.watch.baselib.hook;

import android.util.Log;
import android.view.View;

import com.inso.watch.commonlib.utils.L;

/**
 * Created by wulei
 * Data: 2016/10/17.
 */

public class OnClickListenerProxy implements View.OnClickListener{
    private View.OnClickListener object;
    private HookListenerContract.OnClickListener mlistener;
    private int MIN_CLICK_DELAY_TIME = 500;
    private long lastClickTime = 0;

    public OnClickListenerProxy(View.OnClickListener object, HookListenerContract.OnClickListener listener){
        this.object = object;
        this.mlistener = listener;
    }

    @Override
    public void onClick(View v) {
        Log.e("OnClickListenerProxy", "--------------- OnClickListenerProxy -------------");
        if(mlistener != null) mlistener.doInListener(v);
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            if (object != null) {
                L.d("exe");
                object.onClick(v);
            }
        }else {
            L.d("debounce");
        }
    }
}
