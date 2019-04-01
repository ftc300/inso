package com.inso.watch.baselib.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inso.watch.baselib.R;
import com.inso.watch.commonlib.utils.ScreenUtils;

import java.io.Serializable;

public class BaseDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {
    private View view,topLine,bottomLine;
    private Wrapper wrapper;
    private RelativeLayout topLayout;
    private LinearLayout bottomLayout;
    private ImageView topImg;
    private TextView title;
    private LinearLayout contentLayout;
    private Button btnYes;
    private Button btnNo;
    private LinearLayout dialogLayout;


    private void setWrapper(Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    private static class Wrapper implements Serializable {
        CharSequence title;
        transient Context context;
        CharSequence[] messages;
        transient Drawable icon;
        CharSequence btTextYes;
        CharSequence btTextNo;
        transient View.OnClickListener btListenerYes;
        transient View.OnClickListener btListenerNo;
        transient OnMultiClickListener btMultiListenerYes;
        transient OnMultiClickListener btMultiListenerNo;
        transient OnItemClickListener onItemClickListener;
        transient View contentView;//content view
        transient ContentViewOperator contentViewOperator;
        int contentViewLayoutResId;
        boolean cancelable = true;
        boolean showTitle = true;
        boolean showButtons = true;
        boolean showNegativeButton = true;
        boolean showPositiveButton = true;
        boolean contentViewClickable = true;
        float widthRatio = 0.95f;
        int widthMaxDp = 0;
        int cornerRadiusDp = 5;
        int dividerMarginHorizontalDp = 12;
        int contentTextColor = R.color.black_80_transparent;
        int contentDividerColor = R.color.black_20_transparent;
        int titleColor = R.color.black_80_transparent;
        int yesBtnColor = R.color.inso_red;
        int contentPaddingDp = 18;
        int contentItemHeightDp = 50;
        int contentTextSizeDp = 14;
        boolean showTopDivider = true;
    }

    public void saveData(Bundle outState) {
        outState.putSerializable("wrapper", wrapper);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            wrapper = (Wrapper) savedInstanceState.get("wrapper");
        }
        setStyle(AppCompatDialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
    }

    @Override
    public void onStart() {
        super.onStart();
        initWindow();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveData(outState);
    }


    public void initWindow() {
        Dialog dialog = getDialog();
        Window win = dialog.getWindow();
        win.setWindowAnimations(R.style.dialog);
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = (int) (wrapper.widthRatio * ScreenUtils.getScreenWidth());
        if (wrapper.widthMaxDp != 0 && lp.width > dp2px(getContext(), wrapper.widthMaxDp)) {
            lp.width = dp2px(getContext(), wrapper.widthMaxDp);
        }
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.verticalMargin = 0.02F;
        win.setAttributes(lp);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.base_frg_dialog, container);
        initView();
        return view;
    }

    private void initView() {
        topLine = view.findViewById(R.id.top_line);
        bottomLine = view.findViewById(R.id.bottom_line);
        dialogLayout = view.findViewById(R.id.ll_dialog);
        topLayout = view.findViewById(R.id.rl_title);
        topImg = view.findViewById(R.id.iv_title);
        title = view.findViewById(R.id.tv_title);
        contentLayout = view.findViewById(R.id.ll_content);
        bottomLayout = view.findViewById(R.id.rl_buttons);
        btnYes = view.findViewById(R.id.btn_yes);
        btnNo = view.findViewById(R.id.btn_no);
        if (wrapper == null) {
            wrapper = new Wrapper();
        }
        if (!wrapper.showTitle) {
            topLayout.setVisibility(View.GONE);
            topLine.setVisibility(View.GONE);
        }
        if (!wrapper.showTopDivider) {
            topLine.setVisibility(View.GONE);
        }
        if (!wrapper.showButtons) {
            bottomLayout.setVisibility(View.GONE);
            bottomLine.setVisibility(View.GONE);
        }
        if (!wrapper.showNegativeButton) {
            btnNo.setVisibility(View.GONE);
        }
        if (!wrapper.showPositiveButton) {
            btnYes.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnNo.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }

        if (wrapper.icon != null) {
            topImg.setImageDrawable(wrapper.icon);
            topImg.setVisibility(View.VISIBLE);
            title.setPadding(0, 0, 0, 0);
        }
        title.setText(wrapper.title);
        title.setTextColor(getResources().getColor(wrapper.titleColor));
        btnYes.setTag(BUTTON_POSITIVE_INDEX);
        btnYes.setOnClickListener(this);
        btnYes.setTextColor(getResources().getColor(wrapper.yesBtnColor));
        if (!TextUtils.isEmpty(wrapper.btTextYes)) {
            btnYes.setText(wrapper.btTextYes);
        }
        btnNo.setTag(BUTTON_NEGATIVE_INDEX);
        btnNo.setOnClickListener(this);
        if (!TextUtils.isEmpty(wrapper.btTextNo)) {
            btnNo.setText(wrapper.btTextNo);
        }
        dialogLayout.setBackground(getRoundRectShapeDrawable(getActivity(), wrapper.cornerRadiusDp, Color.WHITE));
        btnYes.setBackground(getStateListDrawable(getActivity(), 0, 0xdddddddd, 0x00000000));
        btnNo.setBackground(getStateListDrawable(getActivity(), 0, 0xdddddddd, 0x00000000));

        analyseContent();
    }

    private static final int BUTTON_POSITIVE_INDEX = -1;
    private static final int BUTTON_NEGATIVE_INDEX = -2;

    private void analyseContent() {
        if (wrapper.messages == null || wrapper.messages.length == 0) {
            if (wrapper.contentView != null) {
                contentLayout.addView(wrapper.contentView);
            } else if (wrapper.contentViewLayoutResId != 0) {
                LayoutInflater.from(getActivity()).inflate(wrapper.contentViewLayoutResId, contentLayout);
            }
            if (wrapper.contentViewOperator != null && contentLayout.getChildCount() != 0) {
                wrapper.contentViewOperator.operate(contentLayout.getChildAt(0));
            }
            return;
        }

        int itemPadding = dp2px(getActivity(), wrapper.contentPaddingDp);
        int itemHeight = dp2px(getActivity(), wrapper.contentItemHeightDp);
        for (int i = 0; i < wrapper.messages.length; i++) {
            TextView tv = new TextView(getActivity());
            tv.setText(wrapper.messages[i]);
            tv.setTextSize(wrapper.contentTextSizeDp);
            tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            tv.setOnClickListener(this);
            tv.setClickable(wrapper.contentViewClickable);
            tv.setTextColor(getResources().getColor(wrapper.contentTextColor));
            tv.setTag(i);
            tv.setMinHeight(itemHeight);
            if (wrapper.messages.length == 1) {
                if (wrapper.showTitle && wrapper.showButtons) {
                    tv.setBackground(getStateListDrawable(getActivity(), 0, 0xdddddddd, 0x00000000));
                } else if (wrapper.showTitle && !wrapper.showButtons) {
                    tv.setBackground(getStateListDrawableForBottomItem(getActivity(), wrapper.cornerRadiusDp, 0xdddddddd, 0x00000000));
                } else if (!wrapper.showTitle && wrapper.showButtons) {
                    tv.setBackground(getStateListDrawableForTopItem(getActivity(), wrapper.cornerRadiusDp, 0xdddddddd, 0x00000000));
                } else {
                    tv.setBackground(getStateListDrawable(getActivity(), wrapper.cornerRadiusDp, 0xdddddddd, 0x00000000));
                }
            } else {
                if (i == 0) {
                    if (wrapper.showTitle) {
                        tv.setBackground(getStateListDrawable(getActivity(), 0, 0xdddddddd, 0x00000000));
                    } else {
                        tv.setBackground(getStateListDrawableForTopItem(getActivity(), wrapper.cornerRadiusDp, 0xdddddddd, 0x00000000));
                    }
                } else if (i == wrapper.messages.length - 1) {
                    if (wrapper.showButtons) {
                        tv.setBackground(getStateListDrawable(getActivity(), 0, 0xdddddddd, 0x00000000));
                    } else {
                        tv.setBackground(getStateListDrawableForBottomItem(getActivity(), wrapper.cornerRadiusDp, 0xdddddddd, 0x00000000));
                    }
                } else {
                    tv.setBackground(getStateListDrawable(getActivity(), 0, 0xdddddddd, 0x00000000));
                }
            }
            tv.setPadding(itemPadding, 0, itemPadding, 0);
            contentLayout.addView(tv);
            if (i != wrapper.messages.length - 1) {
                View divider = new View(getActivity());
                divider.setBackgroundColor(getResources().getColor(wrapper.contentDividerColor));
                LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                dividerParams.setMargins(dp2px(getActivity(), wrapper.dividerMarginHorizontalDp), 0,
                        dp2px(getActivity(), wrapper.dividerMarginHorizontalDp), 0);
                divider.setLayoutParams(dividerParams);
                contentLayout.addView(divider);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Object o = v.getTag();
        if (o != null && o instanceof Integer) {
            if (((Integer) o) == BUTTON_POSITIVE_INDEX) {
                if (wrapper.btListenerYes != null) {
                    wrapper.btListenerYes.onClick(v);
                }
                if (wrapper.btMultiListenerYes != null && (wrapper.contentView != null || wrapper.contentViewLayoutResId != 0)) {
                    if (contentLayout.getChildCount() > 0) {
                        wrapper.btMultiListenerYes.onClick(v, contentLayout.getChildAt(0));
                    }
                }
            } else if (((Integer) o) == BUTTON_NEGATIVE_INDEX) {
                if (wrapper.btListenerNo != null) {
                    wrapper.btListenerNo.onClick(v);
                }
                if (wrapper.btMultiListenerNo != null && (wrapper.contentView != null || wrapper.contentViewLayoutResId != 0)) {
                    if (contentLayout.getChildCount() > 0) {
                        wrapper.btMultiListenerNo.onClick(v, contentLayout.getChildAt(0));
                    }
                }
            } else if (((Integer) o) >= 0) {
                if (wrapper.onItemClickListener != null) {
                    wrapper.onItemClickListener.onItemClicked((Integer) o);
                }
            }
        }
        dismiss();
    }

    public interface OnItemClickListener {
        void onItemClicked(int index);
    }

    public interface ContentViewOperator {
        void operate(View contentView);
    }

    public interface OnMultiClickListener {
        void onClick(View clickedView, View contentView);
    }

    public static class Builder {

        private final Wrapper wrapper;

        public Builder(Context context) {
            wrapper = new Wrapper();
            wrapper.context = context;
        }

        public Context getContext() {
            return wrapper.context;
        }


        /**
         * dialog title color
         *
         * @param color
         * @return
         */
        public Builder setTitleColor(int color) {
            wrapper.titleColor = color;
            return this;
        }

        /**
         * dialog title color
         *
         * @param color
         * @return
         */
        public Builder setBtnYesColor(int color) {
            wrapper.yesBtnColor = color;
            return this;
        }

        /**
         * dialog title
         *
         * @param titleId
         * @return
         */
        public Builder setTitle(int titleId) {
            wrapper.title = wrapper.context.getText(titleId);
            return this;
        }

        /**
         *
         * @param divider
         * @return
         */
        public Builder setTopDivider(boolean divider) {
            wrapper.showTopDivider = divider;
            return this;
        }

        /**
         * dialog title
         *
         * @param title
         * @return
         */
        public Builder setTitle(CharSequence title) {
            wrapper.title = title.toString();
            return this;
        }

        /**
         * dialog message
         *
         * @param messagesId
         * @return
         */
        public Builder setMessages(int messagesId) {
            wrapper.context.getResources().getTextArray(messagesId);
            return this;
        }

        /**
         * dialog message
         *
         * @param messages
         * @return
         */
        public Builder setMessages(CharSequence[] messages) {
            wrapper.messages = messages;
            return this;
        }

        /**
         * left iconId
         *
         * @param iconId
         * @return
         */
        public Builder setIcon(int iconId) {
            wrapper.icon = wrapper.context.getResources().getDrawable(iconId);
            return this;
        }

        /**
         * left icon Drawable
         *
         * @param icon
         * @return
         */
        public Builder setIcon(Drawable icon) {
            wrapper.icon = icon;
            return this;
        }

        /**
         * set PositiveButton View.OnClickListener callback
         *
         * @param listener
         * @return
         */
        public Builder setPositiveButton(final View.OnClickListener listener) {
            wrapper.btListenerYes = listener;
            return this;
        }

        /**
         * set PositiveButton View.OnClickListener callback and button text
         *
         * @param textId
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int textId, final View.OnClickListener listener) {
            wrapper.btTextYes = wrapper.context.getText(textId);
            wrapper.btListenerYes = listener;
            return this;
        }

        /**
         * set PositiveButton View.OnClickListener callback and button text
         *
         * @param text
         * @param listener
         * @return
         */
        public Builder setPositiveButton(CharSequence text, final View.OnClickListener listener) {
            wrapper.btTextYes = text;
            wrapper.btListenerYes = listener;
            return this;
        }

        public Builder setPositiveButtonMultiListener(final OnMultiClickListener btMultiListenerYes) {
            wrapper.btMultiListenerYes = btMultiListenerYes;
            return this;
        }

        public Builder setNegativeButton(final View.OnClickListener listener) {
            wrapper.btListenerNo = listener;
            return this;
        }

        public Builder setNegativeButton(int textId, final View.OnClickListener listener) {
            wrapper.btTextNo = wrapper.context.getText(textId);
            wrapper.btListenerNo = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text, final View.OnClickListener listener) {
            wrapper.btTextNo = text;
            wrapper.btListenerNo = listener;
            return this;
        }

        public Builder setNegativeButtonMultiListener(final OnMultiClickListener btMultiListenerNo) {
            wrapper.btMultiListenerNo = btMultiListenerNo;
            return this;
        }

        public Builder setShowNegativeButton(boolean showNegativeButton) {
            wrapper.showNegativeButton = showNegativeButton;
            return this;
        }

        public Builder setShowPositiveButton(boolean showPositiveButton) {
            wrapper.showPositiveButton = showPositiveButton;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            wrapper.cancelable = cancelable;
            return this;
        }

        /**
         * is show tittle
         *
         * @param showTitle
         * @return
         */
        public Builder setShowTitle(boolean showTitle) {
            wrapper.showTitle = showTitle;
            return this;
        }

        /**
         * is show buttons
         *
         * @param showButtons
         * @return
         */
        public Builder setShowButtons(boolean showButtons) {
            wrapper.showButtons = showButtons;
            return this;
        }

        public Builder setOnItemClickListener(final OnItemClickListener listener) {
            wrapper.onItemClickListener = listener;
            return this;
        }

        /**
         * custom view id
         *
         * @param layoutResId
         * @return
         */
        public Builder setContentView(int layoutResId) {
            wrapper.contentView = null;
            wrapper.contentViewLayoutResId = layoutResId;
            return this;
        }

        public Builder setContentViewClickable(boolean clickable) {
            wrapper.contentViewClickable = clickable;
            return this;
        }

        /**
         * custom view
         *
         * @param contentView
         * @return
         */
        public Builder setContentView(View contentView) {
            wrapper.contentView = contentView;
            wrapper.contentViewLayoutResId = 0;
            return this;
        }

        /**
         * dialog  Ratio width
         *
         * @param widthRatio
         * @return
         */
        public Builder setWidthRatio(float widthRatio) {
            wrapper.widthRatio = widthRatio;
            return this;
        }

        /**
         * dialog width max
         *
         * @param widthMaxDp
         * @return
         */
        public Builder setWidthMaxDp(int widthMaxDp) {
            wrapper.widthMaxDp = widthMaxDp;
            return this;
        }

        public Builder setBackgroundCornerRadius(int cornerRadiusDp) {
            wrapper.cornerRadiusDp = cornerRadiusDp;
            return this;
        }

        public Builder setDividerMarginHorizontalDp(int marginHoriDp) {
            wrapper.dividerMarginHorizontalDp = marginHoriDp;
            return this;
        }


        /**
         * dialog content text color
         *
         * @param contentTextColor
         * @return
         */
        public Builder setContentTextColor(int contentTextColor) {
            wrapper.contentTextColor = contentTextColor;
            return this;
        }

        /**
         * dialog content divider color
         *
         * @param contentDividerColor
         * @return
         */
        public Builder setContentDividerColor(int contentDividerColor) {
            wrapper.contentDividerColor = contentDividerColor;
            return this;
        }

        public Builder setContentPaddingDp(int contentPaddingDp) {
            wrapper.contentPaddingDp = contentPaddingDp;
            return this;
        }

        public Builder setContentItemHeightDp(int contentItemHeightDp) {
            wrapper.contentItemHeightDp = contentItemHeightDp;
            return this;
        }

        public Builder setContentTextSizeDp(int contentTextSizeDp) {
            wrapper.contentTextSizeDp = contentTextSizeDp;
            return this;
        }

        public Builder setContentViewOperator(ContentViewOperator operator) {
            wrapper.contentViewOperator = operator;
            return this;
        }

        /**
         * create BaseDialogFragment
         *
         * @return BaseDialogFragment
         */
        public BaseDialogFragment create() {
            BaseDialogFragment dialog = new BaseDialogFragment();
            dialog.setCancelable(wrapper.cancelable);
            dialog.setWrapper(wrapper);
            return dialog;
        }

        /**
         * build method init BaseDialogFragment
         *
         * @param tag
         * @return
         */
        public Builder showDialog(String tag) {
            BaseDialogFragment dialog = create();
            FragmentTransaction fragmentTransaction = ((AppCompatActivity) (getContext())).getSupportFragmentManager().beginTransaction();
            dialog.show(fragmentTransaction, tag);
            return this;
        }
    }

    private Drawable getRoundRectShapeDrawable(float[] cornerRadiusPx, int color) {
        RoundRectShape rr = new RoundRectShape(cornerRadiusPx, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(color);
        drawable.getPaint().setStyle(Paint.Style.FILL);
        return drawable;
    }

    private Drawable getRoundRectShapeDrawable(Context context, int cornerRadiusDp, int color) {
        int cornerRadiusPx = dp2px(context, cornerRadiusDp);
        float[] outerR = new float[]{cornerRadiusPx, cornerRadiusPx, cornerRadiusPx, cornerRadiusPx,
                cornerRadiusPx, cornerRadiusPx, cornerRadiusPx, cornerRadiusPx};
        return getRoundRectShapeDrawable(outerR, color);
    }

    private Drawable getRoundRectShapeDrawableForTopItem(Context context, int cornerRadiusDp, int color) {
        int cornerRadiusPx = dp2px(context, cornerRadiusDp);
        float[] outerR = new float[]{cornerRadiusPx, cornerRadiusPx, cornerRadiusPx, cornerRadiusPx, 0, 0, 0, 0};
        return getRoundRectShapeDrawable(outerR, color);
    }

    private Drawable getRoundRectShapeDrawableForBottomItem(Context context, int cornerRadiusDp, int color) {
        int cornerRadiusPx = dp2px(context, cornerRadiusDp);
        float[] outerR = new float[]{0, 0, 0, 0, cornerRadiusPx, cornerRadiusPx, cornerRadiusPx, cornerRadiusPx};
        return getRoundRectShapeDrawable(outerR, color);
    }

    private int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    private Drawable getStateListDrawable(Context context, int cornerRadiusDp, int colorPressed, int colorNormal) {
        StateListDrawable stateListDrawable = new StateListDrawable();

        int[] stateHighlighted = new int[]{android.R.attr.state_pressed};
        Drawable highlightedDrawable = getRoundRectShapeDrawable(context, cornerRadiusDp, colorPressed);
        stateListDrawable.addState(stateHighlighted, highlightedDrawable);

        int[] stateNormal = new int[]{};
        Drawable normalDrawable = getRoundRectShapeDrawable(context, cornerRadiusDp, colorNormal);
        stateListDrawable.addState(stateNormal, normalDrawable);
        return stateListDrawable;
    }

    private Drawable getStateListDrawableForTopItem(Context context, int cornerRadiusDp, int colorPressed, int colorNormal) {
        StateListDrawable stateListDrawable = new StateListDrawable();

        int[] stateHighlighted = new int[]{android.R.attr.state_pressed};
        Drawable highlightedDrawable = getRoundRectShapeDrawableForTopItem(context, cornerRadiusDp, colorPressed);
        stateListDrawable.addState(stateHighlighted, highlightedDrawable);

        int[] stateNormal = new int[]{};
        Drawable normalDrawable = getRoundRectShapeDrawableForTopItem(context, cornerRadiusDp, colorNormal);
        stateListDrawable.addState(stateNormal, normalDrawable);
        return stateListDrawable;
    }

    private Drawable getStateListDrawableForBottomItem(Context context, int cornerRadiusDp, int colorPressed, int colorNormal) {
        StateListDrawable stateListDrawable = new StateListDrawable();

        int[] stateHighlighted = new int[]{android.R.attr.state_pressed};
        Drawable highlightedDrawable = getRoundRectShapeDrawableForBottomItem(context, cornerRadiusDp, colorPressed);
        stateListDrawable.addState(stateHighlighted, highlightedDrawable);

        int[] stateNormal = new int[]{};
        Drawable normalDrawable = getRoundRectShapeDrawableForBottomItem(context, cornerRadiusDp, colorNormal);
        stateListDrawable.addState(stateNormal, normalDrawable);
        return stateListDrawable;
    }
}