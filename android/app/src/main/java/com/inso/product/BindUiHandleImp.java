package com.inso.product;

import android.content.Context;

import com.inso.plugin.tools.L;
import com.inso.watch.baselib.wigets.ToastWidget;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/3/13
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class BindUiHandleImp implements IBindUiHandle {
    private Context mContext;

    public BindUiHandleImp(Context context) {
        mContext = context;
    }

    @Override
    public void showNoPermison() {
        L.d("showNoPermison");
        ToastWidget.showFail(mContext,"showNoPermison");
    }

    @Override
    public void showNetError() {
        L.d("showNetError");
        ToastWidget.showFail(mContext,"showNoPermison");
    }

    @Override
    public void showBleError() {
        L.d("showBleError");
        ToastWidget.showFail(mContext,"showNoPermison");
    }

    @Override
    public void showHasBond() {
        L.d("showHasBond");
        ToastWidget.showWarn(mContext,"showHasBond");
    }

    @Override
    public void showBindTimeout() {
        L.d("showBindTimeout");
        ToastWidget.showFail(mContext,"showBindTimeout");
    }

    @Override
    public void showBindFail() {
        L.d("showBindFail");
        ToastWidget.showFail(mContext,"showBindFail");
    }

    @Override
    public void showBindSuccess() {
        L.d("showBindSuccess");
        ToastWidget.showSuccess(mContext,"showBindSuccess");
    }
}
