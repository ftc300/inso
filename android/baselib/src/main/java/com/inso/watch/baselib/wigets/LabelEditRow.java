package com.inso.watch.baselib.wigets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.inso.watch.baselib.R;
import com.xiaomi.smarthome.common.plug.utils.DisplayUtils;

public class LabelEditRow extends LabelTextRow {

    private static final int INVALID = -1;

    private EditText value;

    private TextView label;

    private CheckBox pwdCheck;

    private TextView unit;

    private int dividerIndent; // 分隔线缩进值

    private boolean drawBottomDivider;
    private boolean drawTopDivider;

    public LabelEditRow(Context context) {
        this(context, null);
    }

    public LabelEditRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.base_edit_row;
    }

    @Override
    protected void findViews() {
        // 初始化控件
        label = (TextView) findViewById(R.id.row_label);
        value = (EditText)findViewById(R.id.row_value);
        pwdCheck = (CheckBox) findViewById(R.id.pwdCheck);
        unit = (TextView) findViewById(R.id.unit);

        if (getBackground() == null) {
            setBackgroundColor(getResources().getColor(R.color.main_fore));
        }
    }

    @Override
    protected void bindFromAttrs(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray styled = getContext().obtainStyledAttributes(attrs, R.styleable.LabelTextRow);

        CharSequence text = styled.getText(R.styleable.LabelTextRow_labelText);
        if (!TextUtils.isEmpty(text)) {
            label.setText(text);
            // 有设置文本时，保持最小宽度
            label.setMinWidth(getResources().getDimensionPixelSize(R.dimen.dp50));
        }

        CharSequence valueText = styled.getText(R.styleable.LabelTextRow_labelValue);
        value.setText(valueText);

        int drawableLeft = styled.getResourceId(R.styleable.LabelTextRow_labelIcon, INVALID);
        if(drawableLeft != INVALID){
            label.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(drawableLeft), null, null, null);
        }

        int scaleLeft = styled.getResourceId(R.styleable.LabelTextRow_labelScaleIcon, INVALID);
        if(scaleLeft != INVALID){
            Drawable left = getResources().getDrawable(scaleLeft);
            final int size = DisplayUtils.dip2px(getContext(), 20);
            left.setBounds(0, 0, size, size);
            label.setCompoundDrawables(left, null, null, null);
        }

        int labelColor = styled.getColor(R.styleable.LabelTextRow_labelTextColor, INVALID);
        if(labelColor != INVALID) {
            label.setTextColor(labelColor);
        }

        int valueColor = styled.getColor(R.styleable.LabelTextRow_labelValueColor, INVALID);
        if(labelColor != INVALID) {
            value.setTextColor(valueColor);
        }

        int hintColor = styled.getColor(R.styleable.LabelTextRow_labelHintColor, INVALID);
        if(labelColor != INVALID) {
            value.setHintTextColor(hintColor);
        }

        // 字体大小
        float textSize = styled.getDimension(R.styleable.LabelTextRow_labelTextSize, INVALID);
        if(textSize != INVALID) {
            label.setTextSize(textSize);
            value.setTextSize(textSize);
            unit.setTextSize(textSize);
        }

        // EditText字体大小
        textSize = styled.getDimension(R.styleable.LabelTextRow_labelValueSize, INVALID);
        if(textSize != INVALID){
            value.setTextSize(textSize);
        }

        CharSequence hint = styled.getText(R.styleable.LabelTextRow_labelHint);
        value.setHint(hint);

        int labelWidth = styled.getDimensionPixelSize(R.styleable.LabelTextRow_labelExactlyWidth, 0);
        if (labelWidth > 0) {
            label.setMinWidth(labelWidth);
        }

        dividerIndent = styled.getDimensionPixelSize(R.styleable.LabelTextRow_labelDividerIndent, 0);
        drawBottomDivider = styled.getBoolean(R.styleable.LabelTextRow_labelBottomDivider, true);
        drawTopDivider = styled.getBoolean(R.styleable.LabelTextRow_labelTopDivider, false);

        styled.recycle();

        // EditText相关属性
        TypedArray styledEdit = getContext().obtainStyledAttributes(attrs, R.styleable.LabelEditRow);

        // 输入类型
        int inputType = styledEdit.getInt(R.styleable.LabelEditRow_android_inputType, EditorInfo.TYPE_CLASS_TEXT);
        value.setInputType(inputType);

        // 是否可用
        value.setEnabled(styledEdit.getBoolean(R.styleable.LabelEditRow_labelEditEnable, true));

        // 密码模式，显示密码按钮，并设置EditText的InputTpe
        if(styledEdit.getBoolean(R.styleable.LabelEditRow_labelPasswordMode, false)) {
            value.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            if(inputType == EditorInfo.TYPE_CLASS_NUMBER){
                value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            }

            // 用于明文显示密码的CheckBox
            if(styledEdit.getBoolean(R.styleable.LabelEditRow_labelPwdCheck, true)) {
                pwdCheck.setVisibility(View.VISIBLE);
                pwdCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            value.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        } else {
                            value.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        }
                        // 输入框光标一直在输入文本后面
                        value.setSelection(value.getText().length());
                    }
                });
            }
        }

        // 最大长度
        int maxLength = styledEdit.getInt(R.styleable.LabelEditRow_android_maxLength, INVALID);
        if(maxLength != INVALID) {
            value.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        }

        // 单位显示
        int unitColor = styledEdit.getColor(R.styleable.LabelEditRow_labelUnitColor, INVALID);
        if(unitColor != INVALID) {
            unit.setTextColor(unitColor);
        }
        CharSequence unitText = styledEdit.getText(R.styleable.LabelEditRow_labelUnit);
        if (!TextUtils.isEmpty(unitText)) {
            unit.setText(unitText);
            unit.setVisibility(View.VISIBLE);
        }

        styledEdit.recycle();

    }

    @Override
    public void setText(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            value.setText(text);
        }
    }

    @Override
    public Editable getText() {
        return value.getText();
    }

    @Override
    public void setLabel(String text) {
        label.setText(text);
    }

    @Override
    public String getLabel() {
        return label.getText().toString();
    }

    /**
     * 设置分隔线缩进值
     * @param indent
     */
    @Override
    public void setDividerIndent(int indent) {
        dividerIndent = indent;
    }

    @Override
    public void setDrawBottomDivider(boolean drawBottomDivider) {
        this.drawBottomDivider = drawBottomDivider;
    }

    @Override
    public void setDrawTopDivider(boolean drawTopDivider) {
        this.drawTopDivider = drawTopDivider;
    }

    public void setHint(String hint) {
        value.setHint(hint);
    }

    public void setInputType(int type) {
        value.setInputType(type);
    }

    public EditText getEditText() {
        return value;
    }

    /**
     * 设置单位显示
     * @param unitText
     */
    public void setUnit(CharSequence unitText) {
        unit.setText(unitText);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        value.setEnabled(enabled);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawBottomDivider) {
            int y = getHeight() - 1;
            canvas.drawLine(dividerIndent, y, getRight(), y, paint);
        }
        if (drawTopDivider) {
            canvas.drawLine(0, 0, getRight(), 0, paint);
        }
    }

}
