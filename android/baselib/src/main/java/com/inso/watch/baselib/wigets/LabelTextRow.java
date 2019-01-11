package com.inso.watch.baselib.wigets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inso.watch.baselib.R;


/**
 * 通用的显示label和文本的View
 */
public class LabelTextRow extends LinearLayout {

    private static final int INVALID = -1;

    private TextView value;

    private TextView label;

    protected Paint paint;

    private int dividerIndent; // 分隔线缩进值

    private boolean drawBottomDivider;
    private boolean drawTopDivider;

    public LabelTextRow(Context context) {
        this(context, null);
    }

    public LabelTextRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 设置布局文件
        View.inflate(context, getLayoutRes(), this);

        // 初始化布局参数
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.label_row_height));
        int padding = getResources().getDimensionPixelOffset(R.dimen.content_padding);
        setPadding(padding, 0, padding, 0);

        findViews();
        bindFromAttrs(attrs);

        initPaint();

        // 不设置背景的情况下，继承自ViewGroup的容器不会调用onDraw方法，调用下面的方法使其调用onDraw()方法
        setWillNotDraw(!(drawBottomDivider || drawTopDivider));
    }

    protected int getLayoutRes() {
        return R.layout.base_label_text_row;
    }

    protected void findViews() {
        // 初始化控件
        label = (TextView) findViewById(R.id.row_label);
        value = (TextView) findViewById(R.id.row_value);

        if (getBackground() == null) {
            setBackgroundResource(R.drawable.selector_label_item);
        }
    }

    protected void bindFromAttrs(AttributeSet attrs) {
        // xml属性为控件赋值
        if (attrs != null) {
            TypedArray styled = getContext().obtainStyledAttributes(attrs, R.styleable.LabelTextRow);

            CharSequence text = styled.getText(R.styleable.LabelTextRow_labelText);
            label.setText(text);

            int defaultColor = getResources().getColor(R.color.word_black_second);
            int color = styled.getColor(R.styleable.LabelTextRow_labelValueColor, defaultColor);
            value.setTextColor(color);

            CharSequence valueText = styled.getText(R.styleable.LabelTextRow_labelValue);
            value.setText(valueText);

            boolean alignRight = styled.getBoolean(R.styleable.LabelTextRow_labelValueRight, true);
            if (!alignRight) {
                value.setGravity(Gravity.LEFT);
            }

            boolean drawArrows = styled.getBoolean(R.styleable.LabelTextRow_labelArrows, false);
            if (drawArrows) {
                value.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_right, 0);
            }

            int labelWidth = styled.getDimensionPixelSize(R.styleable.LabelTextRow_labelExactlyWidth, INVALID);
            if (labelWidth != INVALID) {
                label.getLayoutParams().width = labelWidth;
            }

            float textSize = styled.getDimension(R.styleable.LabelTextRow_labelTextSize, INVALID);
            if(textSize != INVALID) {
                label.setTextSize(textSize);
                value.setTextSize(textSize);
            }

            textSize = styled.getDimension(R.styleable.LabelTextRow_labelValueSize, INVALID);
            if(textSize != INVALID){
                value.setTextSize(textSize);
            }

            int labelColor = styled.getColor(R.styleable.LabelTextRow_labelTextColor, INVALID);
            if(labelColor != INVALID) {
                label.setTextColor(labelColor);
            }

            int valueColor = styled.getColor(R.styleable.LabelTextRow_labelValueColor, INVALID);
            if(labelColor != INVALID) {
                value.setTextColor(valueColor);
            }

            int drawableLeft = styled.getResourceId(R.styleable.LabelTextRow_labelIcon, INVALID);
            if(drawableLeft != INVALID){
                label.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(drawableLeft),null,null,null);
            }

            int scaleLeft = styled.getResourceId(R.styleable.LabelTextRow_labelScaleIcon, INVALID);
            if(scaleLeft != INVALID && !isInEditMode()){
                Drawable left = getResources().getDrawable(scaleLeft);
                final int size = dpToPx(getContext(), 20);
                left.setBounds(0, 0, size, size);
                label.setCompoundDrawables(left, null, null, null);
            }

            drawBottomDivider = styled.getBoolean(R.styleable.LabelTextRow_labelBottomDivider, true);
            drawTopDivider = styled.getBoolean(R.styleable.LabelTextRow_labelTopDivider, false);
            dividerIndent = styled.getDimensionPixelSize(R.styleable.LabelTextRow_labelDividerIndent, 0);

            styled.recycle();
        }
    }

    /**
     * 设置值控件所占的权重比
     */
    public void setViewWeight(int labelWeight, int valueWeight) {
        ((LayoutParams) label.getLayoutParams()).weight = labelWeight;
        ((LayoutParams) value.getLayoutParams()).weight = valueWeight;
    }


    public TextView getLabelView() {
        return label;
    }

    public TextView getValueView() {
        return value;
    }

    public void setLabel(String text) {
        label.setText(text);
    }

    public void setText(CharSequence text) {
        value.setText(text);
    }


    public CharSequence getText() {
        return value.getText();
    }

    public String getLabel() {
        return label.getText().toString();
    }

    /**
     * 设置分隔线缩进值
     * @param indent
     */
    public void setDividerIndent(int indent) {
        dividerIndent = indent;
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.label_divider));
    }

    public void setDrawBottomDivider(boolean drawBottomDivider) {
        this.drawBottomDivider = drawBottomDivider;
    }

    public void setDrawTopDivider(boolean drawTopDivider) {
        this.drawTopDivider = drawTopDivider;
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

    public void setLabelIcon(int resId){
        Drawable left = getResources().getDrawable(resId);
        final int size = dpToPx(getContext(), 20);
        left.setBounds(0, 0, size, size);
        label.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
    }

    private int dpToPx(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dpValue * scale);
    }
}
