package com.inso.core.bind;

import android.content.Context;

import com.google.gson.Gson;
import com.inso.core.HttpMgr;
import com.inso.entity.http.ResBase;
import com.inso.entity.http.ResBindStatus;
import com.inso.entity.http.post.Bind;
import com.inso.entity.http.post.BindStatus;
import com.inso.entity.http.post.Unbind;
import com.inso.plugin.tools.L;

import static com.inso.watch.baselib.Constants.BASE_URL;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/3/14
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class BindServerImp implements IBindServer {
    private Context mContext;
    private Gson mGson;
    private IServerResult mServerResult;


    public BindServerImp(Context context, IServerResult mServerResult) {
        mContext = context;
        this.mServerResult = mServerResult;
        mGson = new Gson();
    }

    @Override
    public void checkDeviceStatus(BindStatus o) {
        L.d(" bind :: checkDeviceStatus " + o.toString());
        getResult("device/check-device", o, ResBindStatus.class);
    }

    @Override
    public void bindDevice(Bind o) {
        L.d(" bind :: bindDevice " + o.toString());
        getResult("device/bind", o, ResBase.class);
    }

    @Override
    public void unBindDevice(Unbind o) {
        L.d(" bind :: unBindDevice " + o.toString());
        getResult("device/unbind", o, ResBase.class);
    }

    private <T> void getResult(String url, final Object o, final Class<T> cls) {
        HttpMgr.postStringRequest(mContext, BASE_URL + url , o, new HttpMgr.IResponse<String>() {
            @Override
            public void onSuccess(final String obj) {
                L.d("postStringRequest onSuccess " + obj);
                T t = mGson.fromJson(obj, cls);
                ResBase base = (ResBase) t;
                if (o instanceof BindStatus) {
                    if (isOk(base.getErrcode())) {
                        ResBindStatus status = (ResBindStatus) t;
                        if (status.isResult()) {
                            mServerResult.onDeviceHaveBond();
                        } else {
                            mServerResult.onDeviceNotBond();
                        }
                    } else {
                        mServerResult.onException();
                    }
                } else if (o instanceof Bind) {
                    if (isOk(base.getErrcode())) {
                        mServerResult.onBindSuccess();
                    } else {
                        mServerResult.onBindFail();
                    }
                } else if (o instanceof Unbind) {
                    if (isOk(base.getErrcode())) {
                        mServerResult.onUnBindSuccess();
                    } else {
                        mServerResult.onUnBindFail();
                    }
                }
            }

            @Override
            public void onFail() {
                L.d("postStringRequest onFail ");
            }
        });
    }

    private boolean isOk(int errcode) {
        return errcode == 0;
    }
}