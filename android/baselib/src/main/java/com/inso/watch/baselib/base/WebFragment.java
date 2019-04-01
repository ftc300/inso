package com.inso.watch.baselib.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.inso.watch.baselib.R;


/**
 * 通用的WebView界面
 */
public class WebFragment extends BaseFragment {

    private static final String ARGS_HTML = "html";
    private static final String ARGS_URL = "url";
    private static final String ARGS_TITLE = "title";

    protected WebView mWebView;

    public static void start(Context context, String title, String url, String html) {
        CommonAct.start(context, WebFragment.class, configArgs(title, url, html));
    }

    public static Bundle configArgs(String title, String url, String html) {
        Bundle args = new Bundle();
        args.putString(ARGS_TITLE, title);
        args.putString(ARGS_URL, url);
        args.putString(ARGS_HTML, html);
        return args;
    }

    @Override
    protected View getContentView() {
        mWebView = (WebView) View.inflate(mContext, R.layout.webview, null);

        final Bundle args = getArguments();
        if (args == null) {
            ((Activity) mContext).finish();
            return null;
        }

        // 设置标题
        final String _title = args.getString(ARGS_TITLE);
        setTitle(true, _title);

        String url = args.getString(ARGS_URL);
        if (TextUtils.isEmpty(url)) {
            String htmlParam = args.getString(ARGS_HTML);
//            mWebView.loadDataWithBaseURL(NetManager.getDOMAIN(), htmlParam, "text/html", "utf-8", null);
        } else {
            mWebView.loadUrl(url);
        }
        mWebView.requestFocusFromTouch();

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (TextUtils.isEmpty(_title)) {
                    setTitle(true, title);
                }
            }
        });

        WebSettings settings = mWebView.getSettings();
        //settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);


        return mWebView;
    }

}
