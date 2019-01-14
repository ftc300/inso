package com.inshow.watch.android.act.setting;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;

import com.inshow.watch.android.R;
import com.inshow.watch.android.basic.BasicAct;
import com.inshow.watch.android.event.AdjustStepBus;
import com.inshow.watch.android.manager.BleManager;
import com.inshow.watch.android.tools.L;
import com.inshow.watch.android.view.RotateImage;
import com.xiaomi.smarthome.bluetooth.Response;
import com.xiaomi.smarthome.bluetooth.XmBluetoothManager;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;

import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_STEP_DRIVER;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.CHARACTERISTIC_STEP_DRIVER_COMPLETE;
import static com.inshow.watch.android.tools.Constants.GattUUIDConstant.IN_SHOW_SERVICE;

/**
 * Created by chendong on 2017/1/22.
 */
public class AdjustStepAct extends BasicAct {
//    private ImageView img;
//    private int mScreenW;
//    private float startAngle;
    private boolean hasChanged = false;
    @Override
    protected int getContentRes() {
        return R.layout.watch_act_adjust_step2;
    }

    //    private GestureDetector mGestureDetector = new GestureDetector(mContext, new GestureDetector.OnGestureListener() {
//        @Override
//        public boolean onDown(MotionEvent motionEvent) {
//            return false;
//        }
//
//        @Override
//        public void onShowPress(MotionEvent motionEvent) {
//
//        }
//
//        @Override
//        public boolean onSingleTapUp(MotionEvent motionEvent) {
//            Animation rotateAnimation = new RotateAnimation(startAngle,startAngle+6f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
//            rotateAnimation.setInterpolator(new LinearInterpolator());
//            rotateAnimation.setDuration(200);
//            rotateAnimation.setFillAfter(true);
//            img.startAnimation(rotateAnimation);
//            XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_STEP_DRIVER), I2B_StepDriver(1), new Response.BleWriteResponse() {
//                @Override
//                public void onResponse(int code, Void data) {
//
//                }
//            });
//            startAngle = startAngle + 6f;
//            return false;
//        }
//
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float v1) {
//            return true;
//        }
//
//        @Override
//        public void onLongPress(MotionEvent motionEvent) {
//
//        }
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            try {
//                float distanceX = e1.getX() - e2.getX();
//                if (distanceX > 0) {
//                    Animation rotateAnimation = new RotateAnimation(startAngle,startAngle + distanceX / mScreenW * 60f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
//                    rotateAnimation.setInterpolator(new LinearInterpolator());
//                    rotateAnimation.setDuration(200);
//                    rotateAnimation.setFillAfter(true);
//                    img.startAnimation(rotateAnimation);
//                    XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_STEP_DRIVER), I2B_StepDriver(Math.round(distanceX / mScreenW * 10)), new Response.BleWriteResponse() {
//                        @Override
//                        public void onResponse(int code, Void data) {
//
//                        }
//                    });
//                    startAngle  = startAngle + distanceX / mScreenW * 30f;
//                }else{
////                    img.clearAnimation();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return false;
//        }
//    });
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
////        if(event.getAction()==MotionEvent.ACTION_UP)
////            img.clearAnimation();
//        return mGestureDetector.onTouchEvent(event);
//    }
    @Override
    protected int getTitleRes() {
        return R.layout.title_bar_transparent_black;
    }

    @Override
    protected void initViewOrData() {
        setActStyle(ActStyle.DT);
        findViewById(R.id.title_bar_more).setVisibility(View.GONE);
//        setImageText();
        setBtnOnBackPress();
//        mScreenW = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
//        img = (ImageView) findViewById(R.id.imageView);

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasChanged) {
                    EventBus.getDefault().post(new AdjustStepBus(true));
                }
                finish();
            }
        });
        RotateImage rotateImg = (RotateImage) findViewById(R.id.rotateImg);
        rotateImg.setListener(new RotateImage.IRotate() {
            @Override
            public void onRotate(int delta) {
                if(delta < 3) {
                    hasChanged = true;
                    XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_STEP_DRIVER), BleManager.I2B_StepDriver(1), new Response.BleWriteResponse() {
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

//    private void setImageText() {
//        TextView textViewImage = (TextView) findViewById(R.id.tv_tip);
//        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
//        ImageSpan imageSpan1 = new ImageSpan(this, R.drawable.adjust_step_ic_0, ImageSpan.ALIGN_BASELINE);
//        SpannableString spannableString1 = new SpannableString("请滑动刻度，使手表上的计步指针\n指示到");
//        spannableString1.setSpan(imageSpan1, spannableString1.length() - 1, spannableString1.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        spannableStringBuilder.append(spannableString1);
//        spannableStringBuilder.append("0刻度，然后按“确定”");
//        textViewImage.setText(spannableStringBuilder);
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(hasChanged) {
            L.e("adjust step write 00000" );
            XmBluetoothManager.getInstance().write(MAC, UUID.fromString(IN_SHOW_SERVICE), UUID.fromString(CHARACTERISTIC_STEP_DRIVER_COMPLETE), new byte[]{0, 0, 0, 0}, new Response.BleWriteResponse() {
                @Override
                public void onResponse(int code, Void data) {

                }
            });
        }
    }
}
