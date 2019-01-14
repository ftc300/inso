package com.inshow.watch.android.act.debug.debugadjust;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.event.AdjustStepBus;
import com.inshow.watch.android.manager.BleManager;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.view.RotateImage;
import com.inshow.watch.android.view.UpDownRotateImage;
import com.xiaomi.smarthome.bluetooth.Response;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;

import static com.inshow.watch.android.manager.BleManager.I2B_StepDriver;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_STEP_DRIVER;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_STEP_DRIVER_COMPLETE;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;

/**
 * Created by chendong on 2017/1/22.
 */
public class DebugAdjustStepAct extends BasicAct {
    private boolean hasChanged = false;

    @Override
    protected int getContentRes() {
        return R.layout.watch_act_adjust_step2_debug;
    }


    @Override
    protected int getTitleRes() {
        return R.layout.title_bar_transparent_black;
    }

    @Override
    protected void initViewOrData() {
        setActStyle(ActStyle.DT);
        findViewById(R.id.title_bar_more).setVisibility(View.GONE);
        setBtnOnBackPress();

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasChanged) {
                    EventBus.getDefault().post(new AdjustStepBus(true));
                }
                finish();
            }
        });
        UpDownRotateImage rotateImg = (UpDownRotateImage) findViewById(R.id.rotateImg);
        rotateImg.setListener(new UpDownRotateImage.IRotate() {
            @Override
            public void onRotate(int delta) {
                hasChanged = true;
                XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_STEP_DRIVER), BleManager.I2B_StepDriver(delta == 1 ? 1 : delta / 2), new Response.BleWriteResponse() {
                    @Override
                    public void onResponse(int code, Void data) {
                    }
                });
            }

            @Override
            public void onTouch() {
                hideImageTip();
            }
        });
    }

    private void hideImageTip() {
        if (findViewById(R.id.imgTip).getVisibility() == View.VISIBLE) {
            ValueAnimator ani = ValueAnimator.ofFloat(1f, 0f);
            ani.setDuration(1000);
            ani.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    findViewById(R.id.imgTip).setAlpha((float) valueAnimator.getAnimatedValue());
                    findViewById(R.id.tv_tip_01).setAlpha((float) valueAnimator.getAnimatedValue());

                }
            });
            ani.start();
            ani.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    findViewById(R.id.imgTip).setVisibility(View.GONE);
                    findViewById(R.id.tv_tip_01).setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (hasChanged) {
            L.e("adjust step write 00000");
            XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_STEP_DRIVER_COMPLETE), new byte[]{0, 0, 0, 0}, new Response.BleWriteResponse() {
                @Override
                public void onResponse(int code, Void data) {

                }
            });
        }
    }
}
