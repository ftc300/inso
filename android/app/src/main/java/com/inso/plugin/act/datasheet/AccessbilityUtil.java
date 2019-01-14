package com.inso.plugin.act.datasheet;

import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Comment:
 * Author: ftc300
 * Date: 2018/10/30
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class AccessbilityUtil {
    public static void setAccessibilityFocusable(View view, boolean focused){
        if(android.os.Build.VERSION.SDK_INT >= 16){
            if(focused){
                ViewCompat.setImportantForAccessibility(view, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);
            }else{
                ViewCompat.setImportantForAccessibility(view, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
            }
        }
    }
}
