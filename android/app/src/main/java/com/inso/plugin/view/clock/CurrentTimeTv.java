package com.inso.plugin.view.clock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;


import com.inso.R;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/9/5
 * @ 描述:
 */
@SuppressLint("AppCompatCustomView")
public class CurrentTimeTv extends TextView {
    public CurrentTimeTv(Context context) {
        this(context, null);
    }
    public CurrentTimeTv(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    private TimeZone timeZone;
    private Calendar mCalendar;
    private int hour,minute;
    private String mZoneID;//时区
    private static Locale DEFAULT_LOCALE = Locale.getDefault();

    public void setScale(float factor){
        setPivotX(0.5f);
        setPivotY(0);
        setScaleX(factor);
        setScaleY(factor);
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public CurrentTimeTv(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        timeZone = TextUtils.isEmpty(mZoneID)? TimeZone.getDefault():TimeZone.getTimeZone(mZoneID);
        mCalendar = Calendar.getInstance();
        setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/MIUI_EX_Light.ttf"));
        setTextColor(ContextCompat.getColor(context, R.color.primaryColor));
    }

    private Handler handler = new Handler();

    public void onStart(){
        handler.removeCallbacks(renderRunnable);
        handler.post(renderRunnable);
    }

    public void onStop(){
        handler.removeCallbacks(renderRunnable);
    }


    Runnable renderRunnable = new Runnable() {
        @Override
        public void run() {
            setText(getCurrentText());
            handler.postDelayed(this,1000);
        }
    };

    private String  getCurrentText() {
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        mCalendar.setTimeZone(timeZone);
        minute = mCalendar.get(Calendar.MINUTE);
        hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        return String.format(DEFAULT_LOCALE,"%02d:%02d", hour, minute);
    }
}
