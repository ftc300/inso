package com.inso.core.bind;

import android.app.Activity;
import android.content.Context;

import com.inso.plugin.tools.L;
import com.inso.product.BindSuccessFrg;
import com.inso.watch.baselib.base.BaseFragment;
import com.inso.watch.baselib.base.CommonAct;
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
    public void showNoPermission() {
        L.d("showNoPermission");
        ToastWidget.showFail(mContext,"showNoPermission");
    }

    @Override
    public void showNetError() {
        L.d("showNetError");
        ToastWidget.showFail(mContext,"showNoPermission");
    }

    @Override
    public void showBleError() {
        L.d("showBleError");
        ToastWidget.showFail(mContext,"showNoPermission");
    }

    @Override
    public void showNotFoundDevice() {
        L.d("showNotFoundDevice");
        ToastWidget.showFail(mContext,"showNotFoundDevice");
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
        ((Activity)mContext).finish();
        CommonAct.start(mContext, BindSuccessFrg.class, BaseFragment.configNoTitle());
    }
}
