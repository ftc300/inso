package com.inso.watch.baselib.wigets;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.inso.watch.baselib.R;

@SuppressLint("AppCompatCustomView")
public class ClearEditText extends EditText implements View.OnFocusChangeListener, TextWatcher {


    private Drawable mClearTextIcon;
    private View.OnFocusChangeListener mOnFocusChangeListener;

    public ClearEditText(final Context context) {
        this(context, null);
    }


    public ClearEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    public ClearEditText(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(final Context context) {
        if (isInEditMode()) {
            return;
        }
        mClearTextIcon = context.getResources().getDrawable(R.drawable.ic_edit_clear);
        mClearTextIcon.setBounds(0, 0, mClearTextIcon.getIntrinsicHeight(), mClearTextIcon.getIntrinsicHeight());
        setClearIconVisible(false);
        super.setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }


    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        mOnFocusChangeListener = l;
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.onFocusChange(v, hasFocus);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getCompoundDrawables()[2] != null) {
            final int x = (int) event.getX();
            if (x > getWidth() - getPaddingRight() - mClearTextIcon.getIntrinsicWidth()
                && x < getWidth() - getPaddingRight()) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    setError(null);
                    setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public final void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (isFocused()) {
            setClearIconVisible(text.length() > 0);
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


    }


    @Override
    public void afterTextChanged(Editable s) {


    }


    private void setClearIconVisible(final boolean visible) {
        final Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(
                compoundDrawables[0],
                compoundDrawables[1],
                visible ? mClearTextIcon : null,
                compoundDrawables[3]);
    }
}
