package dev.jokr.memestagram.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;

import dev.jokr.memestagram.util.PaintUtil;

/**
 * Created by jokr on 31.12.16..
 */

public class MemeDrawable extends Drawable {

    private final MemeView.Metadata topMeta;
    private final MemeView.Metadata botMeta;


    private Bitmap bmp;
    private boolean upperHalf;
    private Context ctx;

    private int width;
    private int height;
    private Paint strokePaint;
    private Paint fillPaint;
    private float scale;

    public MemeDrawable(Context ctx, Bitmap bmp, MemeView.Metadata topMeta, MemeView.Metadata botMeta) {
        this.ctx = ctx;
        this.bmp = bmp;

        this.topMeta = topMeta;
        this.botMeta = botMeta;
        strokePaint = PaintUtil.getStrokePaint(ctx);
        fillPaint = PaintUtil.getFillPaint(ctx);

        init();
    }


    private void init() {
        width = bmp.getWidth();
        height = bmp.getHeight();
//        this.setBounds(new Rect(0, 0, width, height));
    }


    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bmp, 0, 0, null);

        String[] lines = topMeta.text.toUpperCase().split("\n", -1);
        setPaintsSize(topMeta.textSize);
        for (int i=0; i<lines.length; i++) {
            float w = fillPaint.measureText(lines[i]);
            canvas.drawText(lines[i], (width/2 - w/2), topMeta.offset + topMeta.textSize*(i+1), fillPaint);
            canvas.drawText(lines[i], (width/2 - w/2), topMeta.offset + topMeta.textSize*(i+1), strokePaint);
        }

        setPaintsSize(botMeta.textSize);
        lines = botMeta.text.toUpperCase().split("\n", -1);
        for (int i=0; i<lines.length; i++) {
            float w = fillPaint.measureText(lines[i]);
            canvas.drawText(lines[i], (width / 2 - w / 2), botMeta.offset + botMeta.textSize * (i + 1), fillPaint);
            canvas.drawText(lines[i], (width / 2 - w / 2), botMeta.offset + botMeta.textSize * (i + 1), strokePaint);
        }
    }


    @Override
    protected void onBoundsChange(Rect bounds) {
        Log.d("USER", "onBoundsChange: W " + width + " boundW " + bounds.width()
                + "H " + height + " boundH " + bounds.height());

        if (bmp != null) {
            scale = bounds.width()/(float)width;
            height = (int)(bmp.getHeight()* scale);
            Log.d("USER", "onBounds scaled height: " + height);
            this.setBounds(new Rect(0, 0, width, height));
            this.bmp = Bitmap.createScaledBitmap(bmp, width, height, false);
            scaleCaptionMetadata(scale, topMeta);
            scaleCaptionMetadata(scale, botMeta);
            this.bmp = Bitmap.createScaledBitmap(bmp, width, height, false);

            init();
        }
    }

    private void scaleCaptionMetadata(float scale, MemeView.Metadata meta) {
        meta.textSize = (int)(scale * meta.textSize);
        meta.offset = scale*meta.offset;
    }

    private void setPaintsSize(int size) {
        fillPaint.setTextSize(size);
        strokePaint.setTextSize(size);
    }

    @Override
    public int getIntrinsicWidth() {
        return width;
    }

    @Override
    public int getIntrinsicHeight() {
        return height;
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}
