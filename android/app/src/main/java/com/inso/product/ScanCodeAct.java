package com.inso.product;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceView;
import android.view.Window;

import com.githang.statusbar.StatusBarCompat;
import com.google.zxing.Result;
import com.google.zxing.client.android.AutoScannerView;
import com.google.zxing.client.android.BaseCaptureActivity;
import com.inso.R;
import com.inso.watch.commonlib.utils.StatusBarCompatUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanCodeAct extends BaseCaptureActivity {

    private static final String TAG = ScanCodeAct.class.getSimpleName();
    private SurfaceView surfaceView;
    private AutoScannerView autoScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarCompatUtil.compat(this);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.black_60_transparent));
        setContentView(R.layout.act_scancode);
        ButterKnife.bind(this);
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        autoScannerView = (AutoScannerView) findViewById(R.id.autoscanner_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoScannerView.setCameraManager(cameraManager);
    }

    @Override
    public SurfaceView getSurfaceView() {
        return (surfaceView == null) ? (SurfaceView) findViewById(R.id.preview_view) : surfaceView;
    }

    @Override
    public void dealDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        Log.i(TAG, "dealDecode  " + rawResult.getText() + " " + barcode + " " + scaleFactor + " ");
        playBeepSoundAndVibrate(true, true);
    }


    @OnClick(R.id.tv_back)
    public void onViewClicked() {
        finish();
    }

}
