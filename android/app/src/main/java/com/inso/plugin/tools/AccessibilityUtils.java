package com.inso.plugin.tools;

import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Comment:
 * Author: ftc300
 * Date: 2018/9/28
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class AccessibilityUtils {

    public static void setAccessibilityFocusable(View view, boolean focused) {
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            if (focused) {
                ViewCompat.setImportantForAccessibility(view, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);
            } else {
                ViewCompat.setImportantForAccessibility(view, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
            }
        }
    }

    public static void setContentDescription(View view, String content) {
        view.setContentDescription(content);
    }

    public static void setContentDescriptionByState(View view, boolean state,String postiveContent,String negativeContent) {
        view.setContentDescription(state?postiveContent:negativeContent);
    }


}
