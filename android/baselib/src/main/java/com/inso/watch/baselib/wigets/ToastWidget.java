package com.inso.watch.baselib.wigets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inso.watch.baselib.R;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/1/8
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class ToastWidget {

    /**
     *
     * @param context
     * @param iconRes 显示图片
     * @param toastStr 弹出文本
     * @Description:
     */
    public static void makeText(Context context, int iconRes,CharSequence toastStr) {
        View layout = LayoutInflater.from(context).inflate(R.layout.toast,null);
        TextView tvToast = layout.findViewById(R.id.toastTv);
        ImageView img = layout.findViewById(R.id.img);
        tvToast.setText(toastStr);
        img.setBackgroundResource(iconRes);
        img.setVisibility(iconRes > 0 ? View.VISIBLE : View.GONE);
        Toast  toast = new Toast(context);
        //toast.setGravity(Gravity.TOP, 0, 200);
        toast.setView(layout);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 弹出提示(成功)
     * @param context
     * @param toastStr
     * @Description:
     */
    public static void showSuccess(Context context, CharSequence toastStr) {
        makeText(context, R.drawable.ic_toast_operation_success, toastStr);
    }

    /**
     * 弹出提示(失败)
     * @param context
     * @param toastStr
     * @Description:
     */
    public static void showFail(Context context, CharSequence toastStr) {
        makeText(context, R.drawable.ic_toast_operation_fail, toastStr);
    }

    /**
     * 弹出提示(警告)
     * @param context
     * @param toastStr
     * @Description:
     */
    public static void showWarn(Context context, CharSequence toastStr) {
        makeText(context, R.drawable.ic_toast_operation_waring, toastStr);
    }

}
