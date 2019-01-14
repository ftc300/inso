package com.inso.plugin.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.inso.R;


/**
 * Created by chendong on 2017/03/27
 */
public class SearchClearEditView extends EditText {
    private Context mContext;
    private Bitmap mClearButton;
    private Bitmap mSearchButton;
    private Paint mPaint;
    private int mInitPaddingRight;
    private int mInitPaddingLeft;
    private int mButtonPadding = dp2px(7);

    public SearchClearEditView(Context context) {
        super(context);
        init(context);
    }

    public SearchClearEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchClearEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        mClearButton = ((BitmapDrawable) getDrawableCompat(R.drawable.clear_button)).getBitmap();
        mSearchButton = ((BitmapDrawable) getDrawableCompat(R.drawable.ic_searchbar)).getBitmap();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mInitPaddingRight = getPaddingRight();
        mInitPaddingLeft = getPaddingLeft();
        setSingleLine(true);
    }

    /**
     * 按钮状态管理
     * @param canvas onDraw的Canvas
     */
    private void buttonManager(Canvas canvas) {
        drawClearBitmap(canvas, getRect(hasFocus() && getText().length() > 0));
        drawSearchBitmap(canvas,getSearchRect());
    }

    /**
     * 设置输入框的内边距
     * @param isShow  是否显示按钮
     */
    private void setPadding(boolean isShow) {
        int paddingRight = mInitPaddingRight + (isShow ? mClearButton.getWidth() + mButtonPadding  : 0);
        setPadding(getPaddingLeft(), getPaddingTop(), paddingRight, getPaddingBottom());
    }

    private void setSearchPadding() {
        int paddingLeft = mInitPaddingLeft +  mSearchButton.getWidth() + mButtonPadding;
        setPadding(paddingLeft, getPaddingTop(), getPaddingRight(), getPaddingBottom());
    }

    /**
     * 取得显示按钮与不显示按钮时的Rect
     * @param isShow  是否显示按钮
     */
    private Rect getRect(boolean isShow) {
        int left, top, right, bottom;
        right   = isShow ? getMeasuredWidth() + getScrollX() - mButtonPadding - mButtonPadding : 0;
        left    = isShow ? right - mClearButton.getWidth() : 0;
        top     = isShow ? (getMeasuredHeight() - mClearButton.getHeight())/2 : 0;
        bottom  = isShow ? top + mClearButton.getHeight() : 0;
        setPadding(isShow);
        return new Rect(left, top, right, bottom);
    }

    /**
     * 取得显示按钮与不显示按钮时的Rect
     */
    private Rect getSearchRect() {
        int left, top, right, bottom;
        left    =  mButtonPadding + getScrollX();
        right   = left + mSearchButton.getWidth();;
        top     =  (getMeasuredHeight() - mSearchButton.getHeight())/2 ;
        bottom  = top + mSearchButton.getHeight() ;
        setSearchPadding();
        return new Rect(left, top, right, bottom);
    }

    private void drawClearBitmap(Canvas canvas, Rect rect) {
        if (rect != null) {
            canvas.drawBitmap(mClearButton, null, rect, mPaint);
        }
    }

    private void drawSearchBitmap(Canvas canvas, Rect rect) {
        if (rect != null) {
            canvas.drawBitmap(mSearchButton, null, rect, mPaint);
        }
    }




    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        buttonManager(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                //判断是否点击到按钮所在的区域
                if (event.getX() - (getMeasuredWidth() - getPaddingRight()) >= 0) {
                    setError(null);
                    this.setText("");
                }
                break;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 获取Drawable
     * @param resourseId  资源ID
     */
    private Drawable getDrawableCompat(int resourseId) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            return getResources().getDrawable(resourseId, mContext.getTheme());
        } else {
            return getResources().getDrawable(resourseId);
        }
    }

    /**
     * 设置按钮左右内边距
     * @param buttonPadding 单位为dp
     */
    public void setButtonPadding(int buttonPadding) {
        this.mButtonPadding = dp2px(buttonPadding);
    }


    public int dp2px(float dipValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}