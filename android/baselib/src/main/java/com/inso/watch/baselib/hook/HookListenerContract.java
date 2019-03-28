package com.inso.watch.baselib.hook;

import android.view.View;

public class HookListenerContract {
    public interface OnClickListener{
        void doInListener(View v);
    }
}
