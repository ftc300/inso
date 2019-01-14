package com.inshow.watch.android.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.view.clock.CurrentTimeTv;
import com.inshow.watch.android.view.clock.MainClockView;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/8/28
 * @ 描述:
 */
public class MainDragLayout extends RelativeLayout {

    private FrameLayout frameTop, frameBottom;
    public ViewDragHelper mViewDragHelper;
    private GestureDetectorCompat gestureDetector;
    private boolean allowMove;// 允许上下滑动
    private boolean isFirstLayout;
    private MainClockView clockView;
    private CurrentTimeTv clockTv;
    private int maxTopPos, minTopPos, deltaTopPos;//以底部的frame为基准
    private float clockWidth, clockTvWidth;
    private FrameLayout frameCity;
    private int mLastHeight;//全面屏适配
    private int deltaHeight;
    private boolean hasVersion = false;//是否显示底部的版本
    private Context context;

    public void setAllowMove(boolean allowMove) {
        this.allowMove = allowMove;
    }

    public MainDragLayout(Context context) {
        this(context, null);
    }

    public MainDragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainDragLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        setWillNotDraw(false);
        allowMove = false;
        isFirstLayout = true;
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelperCallBack());
        gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
                // 垂直滑动时dy>dx，才被认定是上下拖动
                return Math.abs(dy) > Math.abs(dx);
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        L.e("onLayout getHeight():"+getHeight());
        if (frameTop.getTop() == 0 && isFirstLayout) {
            // 只在初始化的时候调用
            frameTop.layout(l, 0, r, getHeight() - (int) (getResources().getDisplayMetrics().density * 194));
            frameBottom.layout(l, 0, r, frameBottom.getMeasuredHeight());
            frameBottom.offsetTopAndBottom(getHeight() - (int) (getResources().getDisplayMetrics().density * 194));
//            mBottomLastMaxTop = frameBottom.getTop();
            isFirstLayout = false;
            mLastHeight = getHeight();
        } else {
            // 如果已被初始化，这次onLayout只需要将之前的状态存入即可
//            L.e("deltaHeight："+deltaHeight);
            if (null != frameCity)
                frameCity.layout(l, frameCity.getTop(), r, frameCity.getBottom());
            if (null != clockTv)
                clockTv.layout(clockTv.getLeft(), clockTv.getTop(), clockTv.getRight(), clockTv.getBottom());
            if (null != clockView)
                clockView.layout(clockView.getLeft(), clockView.getTop(), clockView.getRight(), clockView.getBottom());
            if (null != frameBottom) {
                frameBottom.layout(l, frameBottom.getTop(), r, frameBottom.getBottom());
                if (mLastHeight != getHeight()) {//全面屏兼容
                    L.d("mLastHeight:" + mLastHeight + ",getHeight():" + getHeight());
                    maxTopPos = getHeight() - (int) (getResources().getDisplayMetrics().density * 194);
//                    minTopPos = getHeight() - (int) (getResources().getDisplayMetrics().density * 404);
                    minTopPos = getHeight() - (int) (getResources().getDisplayMetrics().density * (hasVersion ? 434 : 404));
                    deltaHeight = getHeight() - mLastHeight;
                    mLastHeight = getHeight();
                    mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelperCallBack());//屏幕改变 重新设定回调
                    frameBottom.offsetTopAndBottom(deltaHeight);
                }
            }
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        //由于是fragment加载的，onFinishInflate时，fragment还没有commit
//        L.e("onWindowFocusChanged getHeight():"+getHeight());
        try {
            mLastHeight = getHeight();
            frameCity = (FrameLayout) ((LinearLayout) frameTop.getChildAt(0)).getChildAt(1);
            clockView = (MainClockView) ((FrameLayout) ((LinearLayout) frameTop.getChildAt(0)).getChildAt(0)).getChildAt(0);
            clockTv = (CurrentTimeTv) ((FrameLayout) ((LinearLayout) frameTop.getChildAt(0)).getChildAt(0)).getChildAt(1);
            clockWidth = clockView.getWidth();
            clockTvWidth = clockTv.getWidth();
            maxTopPos = getHeight() - (int) (getResources().getDisplayMetrics().density * 194);
            deltaTopPos = (int) (getResources().getDisplayMetrics().density * (hasVersion ? 240 : 210));
//        minTopPos = getHeight() - (int) (getResources().getDisplayMetrics().density * 404);
            minTopPos = getHeight() - (int) (getResources().getDisplayMetrics().density * (hasVersion ? 434 : 404));
        } catch (Exception arg_e) {
            arg_e.printStackTrace();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        frameTop = (FrameLayout) getChildAt(0);
        frameBottom = (FrameLayout) getChildAt(1);
    }

    private class ViewDragHelperCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return allowMove && child == frameBottom;
        }


        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int ret = top;
            if (top <= minTopPos) ret = minTopPos;
            if (top >= maxTopPos) ret = maxTopPos;
            return ret;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            mViewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), yvel <= 0 ? minTopPos : maxTopPos);
            invalidate();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            frameCity.offsetTopAndBottom(dy);
            if (clockTv.getVisibility() != VISIBLE) clockTv.setVisibility(VISIBLE);
            float percent = (float) (top - minTopPos) / (float) deltaTopPos;
            clockView.setScale(percent);
            clockView.setTranslationX((1 - percent) * clockWidth / 2);
            clockView.setAlpha(percent);
            clockTv.setScale(1 - percent);
            clockTv.setTranslationX(percent * clockTvWidth / 2);
            clockTv.setAlpha(1 - percent);
        }
    }


    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            // action_down时就让mDragHelper开始工作，否则导致异常(无法滑动)，坑爹玩意
            mViewDragHelper.processTouchEvent(ev);
        }
        if (ev.getActionMasked() == MotionEvent.ACTION_MOVE) {
            mViewDragHelper.processTouchEvent(ev);
        }
        return mViewDragHelper.shouldInterceptTouchEvent(ev) && gestureDetector.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

}
