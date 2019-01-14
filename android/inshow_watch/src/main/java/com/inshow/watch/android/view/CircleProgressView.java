package com.inshow.watch.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.inshow.watch.android.R;

/**
 * Created by chendong on 2017/2/15.
 */

public class CircleProgressView extends View {

    private int mMaxProgress = 100;
    private int mProgress = 0;
    private final int mCircleLineStrokeWidth = 24;
    private final RectF mRectF;
    private final Paint mPaint;
    private final Context mContext;
    private String mTxtHint1;
    private String mTxtHint2;
    private int hintTextSize = 36;
    private int highLightColor = R.color.watch_blue;
    private Path mPath;
    private PathEffect effects;

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mRectF = new RectF();
        mPaint = new Paint();
        mPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int width = this.getWidth();
        int height = this.getHeight();
        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }
        mRectF.left = mCircleLineStrokeWidth / 2; // 左上角x
        mRectF.top = mCircleLineStrokeWidth / 2; // 左上角y
        mRectF.right = width - mCircleLineStrokeWidth / 2; // 左下角x
        mRectF.bottom = height - mCircleLineStrokeWidth / 2; // 右下角y
        mPath.addArc(mRectF, 0, 360);
        //计算路径的长度
        PathMeasure pathMeasure = new PathMeasure(mPath, false);
        float length = pathMeasure.getLength();
        float step = length / 180;
        effects = new DashPathEffect(new float[]{1 * step / 3, step * 2 / 3},2 * step / 3);
        mPaint.setAntiAlias(true);
        mPaint.setColor(ContextCompat.getColor(mContext, R.color.primaryColor));
        mPaint.setStrokeWidth(mCircleLineStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(effects);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        mPaint.setAlpha(51);
//        canvas.drawArc(mRectF, -90, 360 - ((float) mProgress / mMaxProgress) * 360, false, mPaint);
        canvas.drawArc(mRectF, -90, 360, false, mPaint);
        mPaint.setAlpha(255);
        canvas.drawArc(mRectF, -90, -((float) mProgress / mMaxProgress) * 360, false, mPaint);
    }

    public void setMaxProgress(int maxProgress) {
        this.mMaxProgress = maxProgress;
    }

    public void setProgress(final int progress) {
        mProgress = progress;
        postInvalidate();
    }

    public void setHightLightColor(int color) {
        highLightColor = color;
        invalidate();
    }

    public void setmTxtHint1(String mTxtHint1) {
        this.mTxtHint1 = mTxtHint1;
        invalidate();
    }


    public void setmTxtHint2(String mTxtHint2) {
        this.mTxtHint2 = mTxtHint2;
        invalidate();
    }
}
