package com.inshow.watch.android.act.setting;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.event.AdjustStepPageOneBus;
import com.inshow.watch.android.sync.SyncDeviceHelper;
import com.inshow.watch.android.view.RotateImage;
import com.xiaomi.smarthome.bluetooth.Response;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;

import org.greenrobot.eventbus.Subscribe;

import java.util.UUID;

import static com.inshow.watch.android.manager.BleManager.I2B_StepDriver;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_CLOCK_DRIVER;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;

public class AdjustTimeFirstAct extends BasicAct {

    private boolean b = true;
    @Subscribe
    public void onEventMainThread(AdjustStepPageOneBus event) {
        if (event.finish) {
            finish();
        }
    }

    @Override
    protected int getContentRes() {
        return R.layout.watch_act_adjust_step2;
    }

    @Override
    protected int getTitleRes() {
        return R.layout.title_bar_transparent_black;
    }

    @Override
    protected void initViewOrData() {
        //停止走表
        setActStyle(ActStyle.DT);
        findViewById(R.id.title_bar_more).setVisibility(View.GONE);
        setBtnOnBackPress();
        ((TextView) findViewById(R.id.ok)).setText(getString(R.string.next_step));
        ((TextView) findViewById(R.id.tv_tip)).setText(R.string.adjust_time_first_tip);
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkHasStopWatch();
                switchTo(AdjustTimeSecAct.class);
            }
        });
        RotateImage rotateImg = (RotateImage) findViewById(R.id.rotateImg);
        rotateImg.setListener(new RotateImage.IRotate() {
            @Override
            public void onRotate(int delta) {
                checkHasStopWatch();
                if (delta < 3) {
                    XmBluetoothManager.getInstance().writeNoRsp(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CLOCK_DRIVER), I2B_StepDriver(delta), new Response.BleWriteResponse() {
                        @Override
                        public void onResponse(int code, Void data) {

                        }
                    });

                }
            }

            @Override
            public void onTouch() {
                hideImageTip();
            }
        });
    }


    /**
     * 判断是否已经停表
     */
    private void checkHasStopWatch(){
        if(b) {
            SyncDeviceHelper.syncSetControlFlag(MAC, new SyncDeviceHelper.BtCallback() {
                @Override
                public void onBtResponse(byte[] bytes) {

                }
            }, new int[]{1, 0, 0, 0});
            b = false;
        }
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
        //恢复时针走针
        SyncDeviceHelper.syncSetControlFlag(MAC, new SyncDeviceHelper.BtCallback() {
            @Override
            public void onBtResponse(byte[] bytes) {

            }
        },new int[]{2,0,0,0});
    }
}
