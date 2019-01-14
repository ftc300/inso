package com.inshow.watch.android.act.debug.debugadjust;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.widget.TextView;

import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.event.AdjustStepPageOneBus;
import com.inshow.watch.android.event.AdjustTimeBus;
import com.inshow.watch.android.sync.SyncDeviceHelper;
import com.inshow.watch.android.view.RotateImage;
import com.inshow.watch.android.view.UpDownRotateImage;
import com.xiaomi.smarthome.bluetooth.Response;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.UUID;

import static com.inshow.watch.android.manager.BleManager.I2B_StepDriver;
import static com.inshow.watch.android.manager.BleManager.I2B_WatchTime;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_CLOCK_DRIVER;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_SYNC_WATCH_TIME;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;

public class DebugAdjustTimeFirstAct extends BasicAct {

    private boolean b = true;

    @Subscribe
    public void onEventMainThread(AdjustStepPageOneBus event) {
        if (event.finish) {
            finish();
        }
    }

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
        //停止走表
        setActStyle(ActStyle.DT);
        findViewById(R.id.title_bar_more).setVisibility(View.GONE);
        setBtnOnBackPress();
//        ((TextView) findViewById(R.id.ok)).setText(getString(R.string.next_step));
//        ((TextView) findViewById(R.id.tv_tip)).setText(R.string.adjust_time_first_tip);
        ((TextView) findViewById(R.id.tv_tip)).setText("滑动下方滚轮 ,\n使时、分针指向12点刻度");
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkHasStopWatch();
                XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_SYNC_WATCH_TIME), I2B_WatchTime(12 * 3600), new Response.BleWriteResponse() {
                    @Override
                    public void onResponse(int code, Void data) {

                    }
                });
                EventBus.getDefault().post(new AdjustTimeBus(true));
                finish();
//             switchTo(DebugAdjustTimeSecAct.class);
            }
        });
        final UpDownRotateImage rotateImg = (UpDownRotateImage) findViewById(R.id.rotateImg);
        rotateImg.setListener(new UpDownRotateImage.IRotate() {
            @Override
            public void onRotate(int delta) {
                checkHasStopWatch();
                XmBluetoothManager.getInstance().writeNoRsp(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_CLOCK_DRIVER), I2B_StepDriver(delta), new Response.BleWriteResponse() {
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
//        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rotateImg.start();
//            }
//        });
//        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rotateImg.stop();
//            }
//        });
    }


    /**
     * 判断是否已经停表
     */
    private void checkHasStopWatch() {
        if (b) {
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
        }, new int[]{2, 0, 0, 0});
    }
}
