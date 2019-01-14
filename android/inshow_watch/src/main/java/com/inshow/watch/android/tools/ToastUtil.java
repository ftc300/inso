package com.inshow.watch.android.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.inshow.watch.android.R;


/**
 * Created by chendong on 2017/3/31.
 */

public class ToastUtil {
    private static TextView mTextView;
    public static long TIME_INTERVAL = 3000;
    public static long LAST_TIME_STAMP = 0;

    public static void showToastNoRepeat(Context context, String message) {
        if (LAST_TIME_STAMP == 0 || System.currentTimeMillis() - LAST_TIME_STAMP > TIME_INTERVAL) {
            View toastRoot = LayoutInflater.from(context).inflate(R.layout.watch_toast_item, null);
            mTextView = (TextView) toastRoot.findViewById(R.id.tv_toast_msg);
            mTextView.setText(message);
            Toast toastStart = new Toast(context);
            toastStart.setDuration(Toast.LENGTH_SHORT);
            toastStart.setView(toastRoot);
            toastStart.show();
            LAST_TIME_STAMP = System.currentTimeMillis();
        }
    }

    public static void showToast(Context context, String message) {
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.watch_toast_item, null);
        mTextView = (TextView) toastRoot.findViewById(R.id.tv_toast_msg);
        mTextView.setText(message);
        Toast toastStart = new Toast(context);
        toastStart.setDuration(Toast.LENGTH_SHORT);
        toastStart.setView(toastRoot);
        toastStart.show();
    }


}
