package com.inshow.watch.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.inshow.watch.android.R;

/**
 * Created by chendong on 2017/2/15.
 */

public class SolidDot extends View{
    private  Paint mPaint = new Paint();
    private int color;
    private int circleRadius;

    public SolidDot(Context context) {
        this(context,null);
    }

    public SolidDot(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public SolidDot(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setColor(getResources().getColor(R.color.watch_red));
        circleRadius = 10;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int verticalCenter    =  getHeight() / 2;
        int horizontalCenter  =  getWidth() / 2;
        canvas.drawCircle( horizontalCenter, verticalCenter, circleRadius, mPaint);
    }

    public void setColor(int color)
    {
        mPaint.setColor(getResources().getColor(color));
        invalidate();
    }

    public void setRadius(int Radius)
    {
        circleRadius = Radius;
        invalidate();
    }

}
