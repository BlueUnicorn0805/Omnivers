package hawaiiappbuilders.omniversapp.global;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import hawaiiappbuilders.omniversapp.R;

public class CountDownView extends View {

    public interface OnCountDownListener {
        public void countDownFinished();
    }

    //Ring color
    private int mRingColor;
    //Ring width
    private float mRingWidth;
    //Circle progress value text size
    private int mRingProgessTextSize;
    //Width
    private int mWidth;
    //Height
    private int mHeight;
    private Paint mPaint;
    //Rectangular area of torus
    private RectF mRectF;
    //
    private int mProgessTextColor;
    private int mCountdownTime;
    private float mCurrentProgress;


    ValueAnimator valueAnimator;

    /**
     *Listening events
     */
    private OnCountDownListener mListener;

    public CountDownView(Context context) {
        this(context, null);

    }

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);


    }

    public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // init();

        /**
         *Get related attribute values
         */
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CountDownView);
        mRingColor = typedArray.getColor(R.styleable.CountDownView_ringColor, context.getResources().getColor(R.color.colorAccent));
        mRingWidth = typedArray.getFloat(R.styleable.CountDownView_ringWidth, 40);
        mRingProgessTextSize = typedArray.getDimensionPixelSize(R.styleable.CountDownView_progressTextSize, 20);
        mProgessTextColor = typedArray.getColor(R.styleable.CountDownView_progressTextColor, context.getResources().getColor(R.color.colorAccent));
        mCountdownTime = typedArray.getInteger(R.styleable.CountDownView_countdownTime, 60);
        typedArray.recycle();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        this.setWillNotDraw(false);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        mRectF = new RectF(0 + mRingWidth / 2, 0 + mRingWidth / 2,
                mWidth - mRingWidth / 2, mHeight - mRingWidth / 2);
    }


    /**
     *Set countdown time unit seconds
     * @param mCountdownTime
     */
    public void setCountdownTime(int mCountdownTime) {
        this.mCountdownTime = mCountdownTime;

        invalidate();

    }

// public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
// super(context, attrs, defStyleAttr, defStyleRes);
// }

    /**
     *Animation
     * @param countdownTime
     * @return
     */
    private ValueAnimator getValueAnimator(long countdownTime) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 100);
        valueAnimator.setDuration(countdownTime);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(0);
        return valueAnimator;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         *Torus
         */
        //Color
        mPaint.setColor(mRingColor);
        //Hollow
        mPaint.setStyle(Paint.Style.STROKE);
        //Width
        mPaint.setStrokeWidth(mRingWidth);
        canvas.drawArc(mRectF, -90, mCurrentProgress - 360, false, mPaint);
        //Draw text
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        String text = mCountdownTime - (int) (mCurrentProgress / 360f * mCountdownTime) + "";
        textPaint.setTextSize(mRingProgessTextSize);
        textPaint.setColor(mProgessTextColor);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        //Text centered
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (int) ((mRectF.bottom + mRectF.top - fontMetrics.bottom - fontMetrics.top) / 2);
        canvas.drawText(text, mRectF.centerX(), baseline, textPaint);
    }


    /**
     *Start countdown
     */
    public void startCountDown() {
        valueAnimator = getValueAnimator(mCountdownTime * 1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float i = Float.valueOf(String.valueOf(animation.getAnimatedValue()));
                mCurrentProgress = (int) (360 * (i / 100f));
                invalidate();
            }
        });
        valueAnimator.start();
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //Countdown end callback
                if (mListener != null) {
                    mListener.countDownFinished();
                }
            }
        });
    }

    /**
     *Stop countdown
     */
    public void stopCountDdwn(){

        valueAnimator.cancel();


    }
    public void setOnCountDownListener(OnCountDownListener mListener) {
        this.mListener = mListener;
    }
}
