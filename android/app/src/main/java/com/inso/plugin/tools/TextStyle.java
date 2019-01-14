package com.inso.plugin.tools;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

/**
 * Created by chendong on 2017/2/9.
 */

public class TextStyle {

    /**
     * 设置文本的字体、颜色
     *
     * @param text  指定的文本
     * @param color 颜色值
     * @param size  字体大小（dp）
     * @return
     */
    public static SpannableStringBuilder spanColorAndSize(CharSequence text, int color, int size) {
        if (TextUtils.isEmpty(text)) {
            return new SpannableStringBuilder();
        }
        final int length = text.length();
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        ssb.setSpan(new ForegroundColorSpan(color), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (size > 0) {
            ssb.setSpan(new AbsoluteSizeSpan(size, true), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ssb;
    }

    /**
     * 设置文本的字体颜色
     *
     * @param text  指定的文本
     * @param color 颜色值
     * @return
     */
    public static SpannableStringBuilder spanColor(CharSequence text, int color) {
        if (TextUtils.isEmpty(text)) {
            return new SpannableStringBuilder();
        }
        final int length = text.length();
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        ssb.setSpan(new ForegroundColorSpan(color), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    /**
     * 设置文本的字体大小
     *
     * @param text 指定的文本
     * @param size 字体大小
     * @return
     */
    public static SpannableStringBuilder spanSize(CharSequence text, int size) {
        if (TextUtils.isEmpty(text)) {
            return new SpannableStringBuilder();
        }
        final int length = text.length();
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        ssb.setSpan(new AbsoluteSizeSpan(size, true), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }


    private SpannableStringBuilder ssb = new SpannableStringBuilder();

    private int color;

    private int size;

    public TextStyle(int color, int size) {
        this.color = color;
        this.size = size;
    }

    public TextStyle(int color) {
        this.color = color;
        this.size = 0;
    }

    public TextStyle setColor(int color) {
        this.color = color;
        return this;
    }

    public TextStyle setSize(int size) {
        this.size = size;
        return this;
    }

    /**
     *
     * @param prefix 前缀字符
     * @param styleStr 待设置样式字符
     * @param suffix 后缀字符
     * @return
     */
    public SpannableStringBuilder merge(String prefix, String styleStr, String suffix) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(prefix);
        ssb.append(spanColorAndSize(styleStr, color, size));
        ssb.append(suffix);
        return ssb;
    }

    /**
     *
     * @param prefix 前缀字符
     * @param styleStr 待设置样式字符
     * @return
     */
    public SpannableStringBuilder merge(String prefix, String styleStr) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(prefix);
        ssb.append(spanColorAndSize(styleStr, color, size));
        return ssb;
    }

    /**
     * @param styleStr 待设置样式字符
     * @param suffix 后缀字符
     * @return
     */
    public SpannableStringBuilder mergeEnd(String styleStr, String suffix) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(spanColorAndSize(styleStr, color, size));
        ssb.append(suffix);
        return ssb;
    }

    public TextStyle spanSize(String text) {
        ssb.append(spanSize(text, size));
        return this;
    }

    public TextStyle spanColor(String text) {
        ssb.append(spanColor(text, color));
        return this;
    }

    public TextStyle spanColorAndSize(String text) {
        ssb.append(spanColorAndSize(text, color, size));
        return this;
    }

    public TextStyle span(String text) {
        ssb.append(text);
        return this;
    }

    public TextStyle clear() {
        ssb.clear();
        return this;
    }

    public SpannableStringBuilder getText() {
        return ssb;
    }

    /**
     * 获取结果，并清空现有的字符串
     * @return
     */
    public SpannableStringBuilder getResultText() {
        SpannableStringBuilder result = new SpannableStringBuilder(ssb);
        ssb.clear();
        return result;
    }


}
