package com.inso.plugin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.inso.R;


/**
 * Description: 宽高保持比例的FrameLayout,由宽度决定高度
 *
 */
public class ScaleFrameLayout extends FrameLayout implements ScaleView{

	/**
	 * 默认的高度与宽度相等
	 */
	private static final float DEFAULT_RATIO = 1.0f;
	
	private float ratio;

	public ScaleFrameLayout(Context context) {
		super(context);
	}

	public ScaleFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScaleView, 0, 0);
		ratio = typedArray.getFloat(R.styleable.ScaleView_scale_ratio, DEFAULT_RATIO);
		typedArray.recycle();
	}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if(ratio > 0) {
            int heightSize = (int) (widthSize * ratio);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setRatio(float f) {
        this.ratio = f;
        requestLayout();
    }

    @Override
    public float getRatio() {
        return this.ratio;
    }
}
