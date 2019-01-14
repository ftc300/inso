package com.inso.plugin.view.tipview;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Github: https://github.com/JeasonWong
 * 2017/09/30
 */
public class TipItem {

    public String title;
    public int titleColor = Color.BLACK;
    public int bgColor = Color.WHITE;
    public Bitmap icon;

    public TipItem(String title) {
        this.title = title;
    }

    public TipItem(String title, Bitmap icon) {
        this.title = title;
        this.icon = icon;
    }

    public TipItem(String title, int bgColor) {
        this.title = title;
        this.bgColor = bgColor;
    }

    public TipItem(String title, int titleColor, int bgColor) {
        this.title = title;
        this.titleColor = titleColor;
        this.bgColor = bgColor;
    }

    public TipItem(String title, int bgColor, Bitmap icon) {
        this.title = title;
        this.bgColor = bgColor;
        this.icon = icon;
    }

    public TipItem(String title, int titleColor, int bgColor, Bitmap icon) {
        this.title = title;
        this.titleColor = titleColor;
        this.bgColor = bgColor;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "TipItem{"
                + "title='" + title + '\''
                + ", titleColor=" + titleColor
                + ", bgColor=" + bgColor
                + ", icon=" + icon
                + '}';
    }
}
