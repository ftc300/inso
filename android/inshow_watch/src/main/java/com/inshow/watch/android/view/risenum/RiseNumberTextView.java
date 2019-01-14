package com.inshow.watch.android.view.risenum;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.tools.TextStyle;

import java.text.DecimalFormat;

/**
 * @ 时间:    2017/08/17
 * @ 创建者:  CoderChen
 * @ 描述:  今日计步数递增
 */
public class RiseNumberTextView extends TextView implements IRiseNumber {

    private static final int STOPPED = 0;

    private static final int RUNNING = 1;

    private int mPlayingState = STOPPED;

    private float number;

    private float fromNumber;

    private long duration = 1000;
    /**
     * 1.int 2.float 3.String
     */
    private int numberType = 2;

    private DecimalFormat fnum;

    private EndListener mEndListener = null;

    private Context context;

    final static int[] sizeTable = {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};

    public RiseNumberTextView(Context context) {
        this(context, null);
    }

    public RiseNumberTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RiseNumberTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public interface EndListener {
        public void onEndFinish();
    }

    public boolean isRunning() {
        return (mPlayingState == RUNNING);
    }

    private void runFloat() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(fromNumber, number);
        valueAnimator.setDuration(duration);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                setText(fnum.format(Float.parseFloat(valueAnimator.getAnimatedValue().toString())));
                if (valueAnimator.getAnimatedFraction() >= 1) {
                    mPlayingState = STOPPED;
                    if (mEndListener != null)
                        mEndListener.onEndFinish();
                }
            }


        });
        valueAnimator.start();
    }

    private void runInt() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt((int) fromNumber, (int) number);
        valueAnimator.setDuration(duration);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                setText(valueAnimator.getAnimatedValue().toString());
                if (valueAnimator.getAnimatedFraction() >= 1) {
                    mPlayingState = STOPPED;
                    if (mEndListener != null)
                        mEndListener.onEndFinish();
                }
            }
        });
        valueAnimator.start();
    }

    private void runString() {
        final TextStyle ts = new TextStyle(ContextCompat.getColor(context, R.color.black_90_transparent), 37);
        ValueAnimator valueAnimator = ValueAnimator.ofInt((int) fromNumber, (int) number);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                setText(ts.merge("今天步数\n", "\t\t"+String.valueOf(valueAnimator.getAnimatedValue()), "\t步"));
//                setText(ts.merge(context.getString(R.string.A05), "\t\t" + String.valueOf(valueAnimator.getAnimatedValue()), context.getString(R.string.A06)));
               setText(ts.clear().span(context.getString(R.string.A05)).spanSize("\t\t").spanColorAndSize(String.valueOf(valueAnimator.getAnimatedValue())).spanColor(context.getString(R.string.A06)).getText());
                if (valueAnimator.getAnimatedFraction() >= 1) {
                    mPlayingState = STOPPED;
                    if (mEndListener != null)
                        mEndListener.onEndFinish();
                }
            }
        });
        valueAnimator.start();
    }

    static int sizeOfInt(int x) {
        for (int i = 0; ; i++)
            if (x <= sizeTable[i])
                return i + 1;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        fnum = new DecimalFormat("0.00");
    }

    @Override
    public void start() {

        if (!isRunning()) {
            mPlayingState = RUNNING;
            if (numberType == 1)
                runInt();
            else if (numberType == 2)
                runFloat();
            else if (numberType == 3)
                runString();
        }
    }


    @Override
    public RiseNumberTextView withNumber(float number) {
        this.number = number;
        numberType = 2;
        fromNumber = 0.00f;
        return this;
    }

    @Override
    public RiseNumberTextView withNumber(int number) {
        this.number = number;
        numberType = 1;
        fromNumber = 0.00f;
        return this;
    }

    @Override
    public RiseNumberTextView withString(int number) {
        this.number = number;
        numberType = 3;
        fromNumber = 0.00f;
        return this;
    }

    @Override
    public RiseNumberTextView setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    @Override
    public void setOnEnd(EndListener callback) {
        mEndListener = callback;
    }

}
