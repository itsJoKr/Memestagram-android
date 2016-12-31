package dev.jokr.memestagram.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Created by JoKr on 10/5/2016.
 */

public class PaintUtil {

    public static Paint getFillPaint(Context context) {
        Paint fillPaint;
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "impact.ttf");
        fillPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(Color.WHITE);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setTextSize(30);
        fillPaint.setTypeface(tf);

        return fillPaint;
    }

    public static Paint getStrokePaint(Context context) {
        Paint strokePaint;
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "impact.ttf");
        strokePaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(3);
        strokePaint.setTextSize(30);
        strokePaint.setTypeface(tf);
        return strokePaint;
    }

    public static Paint getShadowPaint() {
        Paint shadowPaint = new Paint();
        shadowPaint.setColor(Color.BLACK);
        shadowPaint.setAlpha(120);
        return shadowPaint;
    }

    public static Paint getTextRectPaint() {
        float[] dashIntervals = new float[]{10, 8};
        DashPathEffect dashPathEffect = new DashPathEffect(dashIntervals, 0);

        Paint dashPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
        dashPaint.setColor(Color.BLACK);
        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setPathEffect(dashPathEffect);
        dashPaint.setStrokeWidth(5);
        dashPaint.setStrokeCap(Paint.Cap.BUTT);
        return dashPaint;
    }
}
