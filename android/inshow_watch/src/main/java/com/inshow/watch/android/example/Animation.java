package com.inshow.watch.android.example;

import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/9/11
 * @ 描述:
 */


public class Animation {
    /**
     * 滑动动画
     * @param isUp
     */
    private void startAnim(final boolean isUp) {
        AnimationSet set = new AnimationSet(true);
        android.view.animation.Animation scaleAnim, alphaAnim;
        if (isUp) {
            alphaAnim = new AlphaAnimation(1, 0);
            scaleAnim = new ScaleAnimation(1f, 0f, 1f, 0f, android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f, android.view.animation.Animation.RELATIVE_TO_SELF, 0);
        } else {
            scaleAnim = new ScaleAnimation(0f, 1f, 0f, 1f, android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f, android.view.animation.Animation.RELATIVE_TO_SELF, 0);
            alphaAnim = new AlphaAnimation(0, 1);
        }
        set.addAnimation(alphaAnim);
        set.addAnimation(scaleAnim);
        set.setFillAfter(true);
        set.setDuration(600);
//        clockView.startAnimation(set);

        AnimationSet set1 = new AnimationSet(true);
        android.view.animation.Animation scaleAnim1, alphaAnim1;
        if (isUp) {
            scaleAnim1 = new ScaleAnimation(0f, 1f, 0f, 1f, android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f, android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f);
            alphaAnim1 = new AlphaAnimation(0, 1);
//            transAnim = new TranslateAnimation(0, 0, 0,MainDragLayout.DELTA_LAYOUT_HEIGHT);//计算绝对值定位,位移使文字时间居中
        } else {
            alphaAnim1 = new AlphaAnimation(1, 0);
            scaleAnim1 = new ScaleAnimation(1f, 0f, 1f, 0f, android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f, android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f);
//            transAnim = new TranslateAnimation(0, 0, 0, MainDragLayout.DELTA_LAYOUT_HEIGHT);
        }
        set1.addAnimation(alphaAnim1);
        set1.addAnimation(scaleAnim1);
//        set1.addAnimation(transAnim);
        set1.setFillAfter(true);
        set1.setDuration(600);
        set1.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {
            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {

            }
        });
//        currentTv.startAnimation(set1);
    }
}
