package dev.jokr.memestagram.views;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by JoKr on 10/26/2016.
 */


public class AnimatedLogoView extends View {

    private float phase;
    private DashPathEffect dashPathEffect;
    private boolean loading = false;

    private Paint textPaint;
    private Paint circlePaint;
    private float tw;
    private int size = 50;
    private float textSize = 5*size/7;
    private float[] dashIntervals;
    private AnimatorSet animSet;
    private float lineLength;

    public AnimatedLogoView(Context context) {
        super(context);
    }

    public AnimatedLogoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private void init() {
        textSize = 6*size/9;
        textPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "Billabong.woff");
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(tf);
        tw = textPaint.measureText("W");

        phase = 0;
        float perimeter = 2 * (size/2-5) * 3.14f;
        lineLength = perimeter/24;
        dashIntervals = new float[]{lineLength, lineLength};
        dashPathEffect = new DashPathEffect(dashIntervals, phase);
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setPathEffect(dashPathEffect);
        circlePaint.setStrokeWidth(5);
        circlePaint.setStrokeCap(Paint.Cap.BUTT); // Because butts are cool
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        size = resolveSizeAndState(30, widthMeasureSpec, 1);
        init();
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("M", (size/2 - tw/2)-(textSize*0.05f), 5*size/7, textPaint);
        canvas.drawCircle(size/2, size/2, (size/2-5), circlePaint);
    }

    public void startLoading() {
        loading = true;
        ObjectAnimator oneHalfAnim = ObjectAnimator.ofFloat(this, "phase", 0, lineLength*6);
        oneHalfAnim.setInterpolator(new AccelerateInterpolator());
        oneHalfAnim.setDuration(1100);
        oneHalfAnim.start();

        ObjectAnimator infiniteAnim = ObjectAnimator.ofFloat(this, "phase", 0, lineLength*6);
        infiniteAnim.setInterpolator(new LinearInterpolator());
        infiniteAnim.setDuration(500);
        infiniteAnim.setRepeatCount(ValueAnimator.INFINITE);
        animSet = new AnimatorSet();
        animSet.playSequentially(oneHalfAnim, infiniteAnim);
        animSet.start();
    }

    public void stopLoading() {
        loading = false;
        animSet.cancel();
    }

    public void toggleLoading() {
        if (loading)
            stopLoading();
        else
            startLoading();
    }

    public float getPhase() {
        return phase;
    }

    public void setPhase(float phase) {
        this.phase = phase;
        dashPathEffect = new DashPathEffect(dashIntervals, phase);
        circlePaint.setPathEffect(dashPathEffect);
        invalidate();
    }
}
