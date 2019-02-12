package com.inso;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.inso.core.HttpAPI;
import com.inso.entity.http.SignUpResponse;
import com.inso.entity.http.XmProfile;
import com.inso.plugin.tools.L;
import com.xiaomi.account.openauth.XMAuthericationException;
import com.xiaomi.account.openauth.XiaomiOAuthConstants;
import com.xiaomi.account.openauth.XiaomiOAuthFuture;
import com.xiaomi.account.openauth.XiaomiOAuthResults;
import com.xiaomi.account.openauth.XiaomiOAuthorize;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.inso.core.Constant.PLATFORM_XM;
import static com.inso.core.Constant.REDIRECT_URI;
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
    XiaomiOAuthResults results;
    private AsyncTask waitResultTask;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        ButterKnife.bind(this);
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
                } catch (android.accounts.OperationCanceledException e1) {
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
                        if(!TextUtils.isEmpty( results.getAccessToken())&&!TextUtils.isEmpty( results.getMacAlgorithm()) && !TextUtils.isEmpty( results.getMacKey())) {
                            XiaomiOAuthFuture<String> future = new XiaomiOAuthorize()
                                    .callOpenApi(getApplicationContext(),
                                            XIAOMI_APPID,
                                            XiaomiOAuthConstants.OPEN_API_PATH_PROFILE,
                                            results.getAccessToken(),
                                            results.getMacKey(),
                                            results.getMacAlgorithm());
                            waitAndShowFutureResult(future);
                        }
                        String r = v.toString();
                        if(r.indexOf("unionId")>-1 && r.indexOf("ok")>-1){
                            XmProfile response =  new Gson().fromJson(r, XmProfile.class);
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(BASE_URL)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
                            HttpAPI api = retrofit.create(HttpAPI.class);
                            Map<String, String> params = new HashMap<>();
                            params.put("platform",PLATFORM_XM);
                            params.put("nickname",response.getData().getMiliaoNick());
                            params.put("unionId",response.getData().getUnionId());
                            params.put("avatar",response.getData().getMiliaoIcon_orig());
                            api.postSignUp(params).enqueue(new Callback<SignUpResponse>() {
                                @Override
                                public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                                    SignUpResponse info = response.body();
                                    L.d(info.toString());
                                }

                                @Override
                                public void onFailure(Call<SignUpResponse> call, Throwable t) {

                                }
                            });

                        }
                        L.d(r);
                    }
                } else if (e != null) {
                    L.d(e.toString());
                } else {
                    L.d("done and ... get no result :(");
                }
            }
        }.executeOnExecutor(mExecutor);
    }

    Executor mExecutor = Executors.newCachedThreadPool();
}
