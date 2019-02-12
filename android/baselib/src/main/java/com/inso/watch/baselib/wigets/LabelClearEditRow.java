package com.inso.watch.baselib.wigets;

import android.content.Context;
import android.util.AttributeSet;

import com.inso.watch.baselib.R;


public class LabelClearEditRow extends LabelEditRow {

    public LabelClearEditRow(Context context) {
        super(context);
    }

    public LabelClearEditRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.base_clear_edit_row;
    }
}
