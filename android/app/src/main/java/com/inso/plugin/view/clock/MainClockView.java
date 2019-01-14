package com.inso.plugin.view.clock;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.inso.R;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static com.inso.plugin.tools.Constants.deltaTimeFromUTC;


/**
 * @ 创建者:   CoderChen
 * @ 时间:     2017/12/16
 * @ 描述:
 */
public class MainClockView extends FrameLayout {

    private ImageView midImg, imgSuccess, imgSecond;
    private NonStopRotateView connectImgBg;
    private Context context;
    private DeviceStatus mCurrentStatus;
    private float mHourDegree;
    private float mMinuteDegree;
    private float mSecondDegree;
    private TimeZone timeZone;
    private Calendar mCalendar;
    private Path mHourHandPath;
    private Path mMinuteHandPath;
    private float mRadius;
    private Paint mPaint;
    private RectF mCircleRectF;
    private String zoneId;
    private float second;
    private float minute;
    private float hour;
    private boolean hasAnimationEnd = false;
    private boolean drawFlag = true;

    public void setDrawFlag(boolean drawFlag) {
        this.drawFlag = drawFlag;
    }

    public MainClockView(Context context) {
        this(context, null);
    }

    public MainClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.setWillNotDraw(false);//call onDraw
        viewInit(context);
        startConnectingRotate();
        variableInit();
    }

    private void variableInit() {
        mCurrentStatus = DeviceStatus.CONNECTING;
        mHourHandPath = new Path();
        mMinuteHandPath = new Path();
        mCircleRectF = new RectF();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(ContextCompat.getColor(context, R.color.clock_minute_color));
        timeZone = TextUtils.isEmpty(zoneId) ? TimeZone.getDefault() : TimeZone.getTimeZone(zoneId);
        mCalendar = Calendar.getInstance();
    }

    private void viewInit(Context context) {
        View container = LayoutInflater.from(context).inflate(R.layout.custom_clock_layout, this);
        this.connectImgBg = (NonStopRotateView) container.findViewById(R.id.img_bg);
        this.midImg = (ImageView) container.findViewById(R.id.mid_image);
        this.imgSuccess = (ImageView) container.findViewById(R.id.img_suc_bg);
        this.imgSecond = (ImageView) container.findViewById(R.id.clock_second);
    }

    public void startConnectingRotate() {
        connectImgBg.start();
    }

    public void pauseConnectingRotate() {
        connectImgBg.pause();
    }

    public void destroyConnectingRotate() {
        connectImgBg.destroy();
    }

    public void setStatus(DeviceStatus status) {
        mCurrentStatus = status;
        if (status == DeviceStatus.CONNECTED) {
            postInvalidate();
            destroyConnectingRotate();
            animationClock();
        } else if (status == DeviceStatus.TIMEOUT) {
            pauseConnectingRotate();
            connectImgBg.setImageDrawable(null);
            midImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.connect_fail));
            postInvalidate();
        } else if (status == DeviceStatus.CONNECTING) {
            connectImgBg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.connect_ing_circle));
            midImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.connect_ing_icon));
            startConnectingRotate();
            postInvalidate();
        }

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = Math.min(w - getPaddingLeft() - getPaddingRight(), h - getPaddingTop() - getPaddingBottom()) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCurrentStatus == DeviceStatus.CONNECTED && drawFlag) {
            if (hasAnimationEnd) {
                getTimeDegree();
            }
            drawClock(canvas);
            setContentDescription(String.format(Locale.getDefault(),"%02d:%02d", (int)hour, (int)minute));
        }
        setEnabled(getAlpha() == 1);
        invalidate();
    }

    private void drawClock(Canvas canvas) {
        imgSecond.setRotation(mSecondDegree);
        drawHourHand(canvas);
        drawMinuteHand(canvas);
    }

    private void getTimeDegree() {
        mCalendar.setTimeInMillis(System.currentTimeMillis() + deltaTimeFromUTC);
        mCalendar.setTimeZone(timeZone);
        float milliSecond = mCalendar.get(Calendar.MILLISECOND);
        second = mCalendar.get(Calendar.SECOND) + milliSecond / 1000;
        minute = mCalendar.get(Calendar.MINUTE) + second / 60;
        hour = mCalendar.get(Calendar.HOUR) + minute / 60;
        setTimeDegree();
    }

    private void setTimeDegree() {
        mSecondDegree = second / 60 * 360;
        mMinuteDegree = minute / 60 * 360;
        mHourDegree = hour / 12 * 360;
    }

    /**
     * 画时针，根据不断变化的时针角度旋转画布
     * 针头为圆弧状，使用二阶贝塞尔曲线
     */
    private void drawHourHand(Canvas mCanvas) {
        mCanvas.save();
        mCanvas.rotate(mHourDegree, getWidth() / 2, getHeight() / 2);
        mHourHandPath.reset();
        mHourHandPath.moveTo(getWidth() / 2 - 0.014f * mRadius, getHeight() / 2 - 0.014f * mRadius);
        mHourHandPath.lineTo(getWidth() / 2 - 0.007f * mRadius, 0.5f * mRadius);
        mHourHandPath.quadTo(getWidth() / 2, 0.48f * mRadius, getWidth() / 2 + 0.007f * mRadius, 0.5f * mRadius);
        mHourHandPath.lineTo(getWidth() / 2 + 0.014f * mRadius, getHeight() / 2 - 0.02f * mRadius);
        mHourHandPath.close();
        mPaint.setColor(ContextCompat.getColor(context, R.color.clock_minute_color));
        mCanvas.drawPath(mHourHandPath, mPaint);
        mCanvas.restore();
    }

    /**
     * 画分针，根据不断变化的分针角度旋转画布
     */
    private void drawMinuteHand(Canvas mCanvas) {
        mCanvas.save();
        mCanvas.rotate(mMinuteDegree, getWidth() / 2, getHeight() / 2);
        mMinuteHandPath.reset();
        mMinuteHandPath.moveTo(getWidth() / 2 - 0.014f * mRadius, getHeight() / 2 - 0.014f * mRadius);
        mMinuteHandPath.lineTo(getWidth() / 2 - 0.007f * mRadius, 0.38f * mRadius);
        mMinuteHandPath.quadTo(getWidth() / 2, 0.36f * mRadius, getWidth() / 2 + 0.007f * mRadius, 0.38f * mRadius);
        mMinuteHandPath.lineTo(getWidth() / 2 + 0.014f * mRadius, getHeight() / 2 - 0.02f * mRadius);
        mMinuteHandPath.close();
        mPaint.setColor(ContextCompat.getColor(context, R.color.clock_minute_color));
        mCanvas.drawPath(mMinuteHandPath, mPaint);
        mCircleRectF.set(getWidth() / 2 - 0.025f * mRadius, getHeight() / 2 - 0.025f * mRadius, getWidth() / 2 + 0.025f * mRadius, getHeight() / 2 + 0.025f * mRadius);
        mPaint.setColor(ContextCompat.getColor(context, R.color.clock_middle_color));
        mCanvas.drawArc(mCircleRectF, 0, 360, false, mPaint);
        mCanvas.restore();
    }

    public void setScale(float factor) {
        setPivotX(0.5f);
        setPivotY(0);
        setScaleX(factor);
        setScaleY(factor);
    }


    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
        timeZone = TextUtils.isEmpty(zoneId) ? TimeZone.getDefault() : TimeZone.getTimeZone(zoneId);
    }


    private void animationClock() {
        connectImgBg.setVisibility(INVISIBLE);
        midImg.setVisibility(INVISIBLE);
        imgSuccess.setVisibility(VISIBLE);
        imgSecond.setVisibility(VISIBLE);
        getTimeDegree();
        PropertyValuesHolder h = PropertyValuesHolder.ofFloat("h", 10.17f, hour <= 10 ? hour + 12 : hour);
        PropertyValuesHolder m = PropertyValuesHolder.ofFloat("m", 10f, minute <= 10 ? minute + 60 : minute);
        PropertyValuesHolder s = PropertyValuesHolder.ofFloat("s", 0, second + 1);
        ValueAnimator ani = ValueAnimator
                .ofPropertyValuesHolder(h, m, s)
                .setDuration(1000);

        ani.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                second = (Float) valueAnimator.getAnimatedValue("s");
                minute = (Float) valueAnimator.getAnimatedValue("m");
                hour = (Float) valueAnimator.getAnimatedValue("h");
                setTimeDegree();
            }
        });
        ani.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                hasAnimationEnd = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ani.start();
    }


}
