package com.inshow.watch.android.view.clock;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/12/15
 * @ 描述:
 */
public class NonStopRotateView extends ImageView {
    private float degree;
    private int centerX, centerY;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            degree += 1f;
            if (degree > 360f) {
                degree -= 360f;
            }
            invalidate();
            handler.post(this);
        }
    };
    public NonStopRotateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void start() {
        handler.post(runnable);
    }

    public void pause() {
        handler.removeCallbacks(runnable);
    }

    public void destroy() {
        handler.removeCallbacksAndMessages(null);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.rotate(degree, centerX, centerY);
        super.draw(canvas);
        canvas.restore();
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }
}