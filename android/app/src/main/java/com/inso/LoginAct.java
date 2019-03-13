package com.inso;

import android.accounts.OperationCanceledException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.inso.core.HttpMgr;
import com.inso.entity.http.XmProfile;
import com.inso.entity.http.post.Sign;
import com.inso.plugin.manager.SPManager;
import com.inso.plugin.tools.L;
import com.inso.watch.baselib.wigets.RotateLoading;
import com.inso.watch.baselib.wigets.ToastWidget;
import com.xiaomi.account.openauth.XMAuthericationException;
import com.xiaomi.account.openauth.XiaomiOAuthConstants;
import com.xiaomi.account.openauth.XiaomiOAuthFuture;
import com.xiaomi.account.openauth.XiaomiOAuthResults;
import com.xiaomi.account.openauth.XiaomiOAuthorize;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.inso.core.Constant.PLATFORM_XM;
import static com.inso.core.Constant.REDIRECT_URI;
import static com.inso.core.Constant.SP_ACCESS_TOKEN;
import static com.inso.core.Constant.SP_EXPIRED_AT;
import static com.inso.core.Constant.XIAOMI_APPID;
import static com.inso.watch.baselib.Constants.BASE_URL;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/2/12
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */

public class LoginAct extends AppCompatActivity {
    @BindView(R.id.rotateloading)
    RotateLoading mRotateloading;
    private XiaomiOAuthResults results;
    private AsyncTask waitResultTask;
    private Context mContext = this;
    private Executor mExecutor = Executors.newCachedThreadPool();


    void switch2Main() {
        this.finish();
        Intent intent = new Intent(this, MainAct.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white));
        ButterKnife.bind(this);
        long expired_at = (Long) SPManager.get(this, SP_EXPIRED_AT, 0L);
        long now = Calendar.getInstance().getTimeInMillis() / 1000;
        if (now <= expired_at) {
            mRotateloading.start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch2Main();
                }
            },800);
        }
    }

    @OnClick({R.id.wechat, R.id.xiaomi})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.wechat:
                break;
            case R.id.xiaomi:
                XiaomiOAuthFuture<XiaomiOAuthResults> future = new XiaomiOAuthorize()
                        .setAppId(XIAOMI_APPID)
                        .setRedirectUrl(REDIRECT_URI)
                        .setPhoneNumAutoFill(getApplicationContext(), true)
                        .startGetAccessToken(LoginAct.this);
                waitAndShowFutureResult(future);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRotateloading.stop();
        if (waitResultTask != null && !waitResultTask.isCancelled()) {
            waitResultTask.cancel(false);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private <V> void waitAndShowFutureResult(final XiaomiOAuthFuture<V> future) {
        waitResultTask = new AsyncTask<Void, Void, V>() {
            Exception e;

            @Override
            protected void onPreExecute() {
                L.d("waiting for Future result...");
            }

            @Override
            protected V doInBackground(Void... params) {
                L.d("waiting for Future result getting...");
                V v = null;
                try {
                    v = future.getResult();
                } catch (IOException e1) {
                    this.e = e1;
                } catch (OperationCanceledException e1) {
                    this.e = e1;
                } catch (XMAuthericationException e1) {
                    this.e = e1;
                }
                return v;
            }

            @Override
            protected void onPostExecute(V v) {
                if (v != null) {
                    if (v instanceof XiaomiOAuthResults) {
                        results = (XiaomiOAuthResults) v;
                        if (!TextUtils.isEmpty(results.getAccessToken()) && !TextUtils.isEmpty(results.getMacAlgorithm()) && !TextUtils.isEmpty(results.getMacKey())) {
                            XiaomiOAuthFuture<String> future = new XiaomiOAuthorize()
                                    .callOpenApi(getApplicationContext(),
                                            XIAOMI_APPID,
                                            XiaomiOAuthConstants.OPEN_API_PATH_PROFILE,
                                            results.getAccessToken(),
                                            results.getMacKey(),
                                            results.getMacAlgorithm());
                            waitAndShowFutureResult(future);
                        }
                    }
                    String r = v.toString();
                    if (r.indexOf("unionId") > -1 && r.indexOf("ok") > -1) {
                        try {
                            XmProfile response = new Gson().fromJson(r, XmProfile.class);
                            XmProfile.DataBean data = response.getData();
                            Sign sign = new Sign(PLATFORM_XM, data.getMiliaoNick(), data.getUnionId(), data.getMiliaoIcon(), "male");
                            mRotateloading.start();
                            HttpMgr.getRequestQueue(getApplicationContext()).add(HttpMgr.postRequest(BASE_URL + "member/signup", new Gson().toJson(sign), new HttpMgr.IResponse<JSONObject>() {
                                @Override
                                public void onSuccess(JSONObject obj) {
                                    try {
                                        SPManager.put(mContext, SP_ACCESS_TOKEN, obj.getString("access_token"));
                                        SPManager.put(mContext, SP_EXPIRED_AT, obj.getLong("expired_at"));
                                        L.d("obj.get(\"access_token\")" + obj.get("access_token"));
                                        switch2Main();
                                    } catch (JSONException arg_e) {
                                        arg_e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFail() {
                                    ToastWidget.showFail(mContext, "Login Error!");
                                }
                            }));
                        } catch (Exception e) {
                            e.printStackTrace();
                            L.e(e.getMessage());
                        }
                    }
                    L.d(r);
                } else if (e != null) {
                    L.d(e.toString());
                } else {
                    L.d("done and ... get no result :(");
                }
            }
        }.executeOnExecutor(mExecutor);
    }


}
