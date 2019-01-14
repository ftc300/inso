package com.inshow.watch.android.view;

import android.content.Context;
import android.os.SystemClock;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.tools.L;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/8/24
 * @ 描述:
 */
public class UpDownRotateImage extends ImageView {
    private int mRadius;
    private float mLastX;
    private float mLastY;
    private IRotate listener;
    private boolean hideFlag = true;
    private float startAngle;
    private long mLast;
    private long LIMIT_TIME = 80;

    public void setListener(IRotate listener) {
        this.listener = listener;
    }

    public UpDownRotateImage(Context context) {
        this(context, null);
    }

    public UpDownRotateImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UpDownRotateImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setImageDrawable(getResources().getDrawable(R.drawable.adjust_step_dial));
    }

    private int measureDimension(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = 800;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureDimension(widthMeasureSpec), measureDimension(heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = Math.min(w, h);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float start;
        long deltaTime;
        long actionTime = SystemClock.uptimeMillis();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                deltaTime = SystemClock.uptimeMillis() - mLast;
                if (deltaTime < LIMIT_TIME) break;
                if (null != listener && hideFlag) {
                    listener.onTouch();
                    hideFlag = false;
                }
                start = getAngle(mLastX, mLastY);
                float end = getAngle(x, y);
                int lastQuadrant = getQuadrant(mLastX, mLastY);
                int currentQuadrant = getQuadrant(x, y);
                if (currentQuadrant == lastQuadrant) {
                        if (currentQuadrant == 1 || currentQuadrant == 4) {    // 如果是一、四象限，则直接end-start，角度值都是正值
                            if (null != listener) {
                                mLastX = x;
                                mLastY = y;
                                rotate(end - start);
                                if (start - end <= 15) {
                                    listener.onRotate(getRotateStep(end - start));
                                    mLast = SystemClock.uptimeMillis();
                                }
                            }
                        } else { // 二、三象限，角度值是负值
                            if (null != listener) {
                                mLastX = x;
                                mLastY = y;
                                rotate(start - end);
                                if (start - end <= 15) {
                                    listener.onRotate(getRotateStep(start - end));
                                    mLast = SystemClock.uptimeMillis();
                                }
                            }
                    }
                } else {
                    mLastX = x;
                    mLastY = y;
                    rotate(start - end);
                    mLast = SystemClock.uptimeMillis();
                    listener.onRotate(currentQuadrant > lastQuadrant ? 1 : -1);
                }
                break;
            case MotionEvent.ACTION_UP:
                mLastX = x;
                mLastY = y;
                if (((SystemClock.uptimeMillis() - actionTime) < 1000 )&& !hideFlag && null != listener) {
                    rotate(-100);
                    listener.onRotate(1);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mLastX = x;
                mLastY = y;
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private int getRotateStep(double delta) {
        if (delta >= 0)
            return (delta = (int) delta / 2) > 0 ? (int) delta : 1;
        else
            return (delta = (int) delta / 2) < 0 ? (int) delta : -1;
    }

    private void rotate(double delta) {
        Animation rotateAnimation = new RotateAnimation(startAngle, startAngle = startAngle + (float) delta, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(10);
        rotateAnimation.setFillAfter(true);
        startAnimation(rotateAnimation);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /**
     * 根据触摸的位置，计算角度
     *
     * @param xTouch
     * @param yTouch
     * @return
     */
    private float getAngle(float xTouch, float yTouch) {
        double x = xTouch - (mRadius / 2d);
        double y = yTouch - (mRadius / 2d);
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }

    /**
     * 根据当前位置计算象限
     *
     * @param x
     * @param y
     * @return
     */
    private int getQuadrant(float x, float y) {
        int tmpX = (int) (x - mRadius / 2);
        int tmpY = (int) (y - mRadius / 2);
        if (tmpX >= 0) {
            return tmpY >= 0 ? 4 : 1;
        } else {
            return tmpY >= 0 ? 3 : 2;
        }
    }

    public interface IRotate {
        void onRotate(int delta);

        void onTouch();
    }


    public interface IFlingDirection {
        public void onFlingDown();

        public void onFlingUp();
    }


}
