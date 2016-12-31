package dev.jokr.memestagram.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import dev.jokr.memestagram.util.CaretToggleTask;
import dev.jokr.memestagram.util.PaintUtil;

/**
 * Created by JoKr on 10/6/2016.
 */

public class MemeView extends View implements CaretToggleTask.CaretToggleCallback {

    private Bitmap bmp;

    private EditText underEditText;
    private Paint strokePaint;
    private Paint fillPaint;
    private Paint shadowPaint;
    private Paint dashedPaint;

    private boolean initialized = false;
    private int width;
    private int height;
    private int textSize;
    private float offset = 0;
    private float startY;
    private float startX;
    private float residueX = 0;
    private float minHeight;
    private float maxHeight;
    private boolean upperHalf;

    private int minCapSize = 20;
    private int maxCapSize = 90;

    private boolean finalBitmapRequest = false;
    private CaretToggleTask caretTask;
    private boolean underscoreShown = false;
    private float scale;

    public MemeView(Context context) {
        super(context);
        this.setDrawingCacheEnabled(true);
    }

    public MemeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setDrawingCacheEnabled(true);
    }

    private void init() {
        initialized = true;
        width = bmp.getWidth();
        height = bmp.getHeight();
        strokePaint = PaintUtil.getStrokePaint(getContext());
        fillPaint = PaintUtil.getFillPaint(getContext());
        shadowPaint = PaintUtil.getShadowPaint();
        dashedPaint = PaintUtil.getTextRectPaint();
        if (upperHalf) {
            minHeight = 1;
            maxHeight = height/2;
        } else {
            minHeight = height/2;
            maxHeight = height;
            offset = maxHeight - (textSize+1);
        }
    }

    public void setBitmapAndInitialize(Bitmap bmp, boolean upperHalf) {
        this.upperHalf = upperHalf;
        this.bmp = bmp;

        // pre-init
        underEditText = new EditText(getContext());
        textSize = 30;
        caretTask = new CaretToggleTask(this);
        caretTask.execute();

        init();
        invalidate();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) return false;

        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            String currText = underEditText.getText().toString();
            underEditText.setText(currText + "\n");
            underEditText.setSelection(underEditText.getText().length());
        } else {
            underEditText.dispatchKeyEvent(event);
        }
        invalidate();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (bmp==null) return false;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startY = event.getY();
            startX = event.getX();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            setOffset((startY - event.getY()));
            setCaptionSizeFromSwipe(startX, event.getX());
            startY = event.getY();
            startX = event.getX();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            setOffset(-(startY-event.getY()));
            setCaptionSizeFromSwipe(startX, event.getX());
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            offset = 0;
            startX = 0;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (bmp != null) {
            Log.d("USER", "MemeView onMeasure " + bmp.getWidth() + "x" + bmp.getHeight());
            setMeasuredDimension(bmp.getWidth(), bmp.getHeight());
        } else {
            setMeasuredDimension(0, 0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int givenWidth;
        if (left < 0) {
            givenWidth = right - (-left);
        } else {
            givenWidth = right - left;
        }

        if (bmp != null && givenWidth != width) {
            scale = givenWidth/(float)bmp.getWidth();
            int height = (int)(bmp.getHeight()* scale);
            this.bmp = Bitmap.createScaledBitmap(bmp, givenWidth, height, false);
            init();
        }

        super.onLayout(changed, left, top, right, bottom);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int textRectWidth = 0;

        // Base bitmap
        if (bmp == null) return;
        canvas.drawBitmap(bmp, 0, 0, null);

        // Text caption
        String text = underEditText.getText().toString().toUpperCase();
        String[] lines = text.split("\n", -1);
        for (int i=0; i<lines.length; i++) {
            float w = fillPaint.measureText(lines[i]);
            if (w > textRectWidth) textRectWidth = (int)w;
            if (i==lines.length-1 && underscoreShown && !finalBitmapRequest) lines[i] += "_";
            canvas.drawText(lines[i], (width/2 - w/2), offset + textSize*(i+1), fillPaint);
            canvas.drawText(lines[i], (width/2 - w/2), offset + textSize*(i+1), strokePaint);
        }

        if (finalBitmapRequest) return; // Don't draw helper rects for final bitmap

        // Text rect
        textRectWidth++;
        canvas.drawRect(width/2-textRectWidth/2, offset,
                width/2+textRectWidth/2, offset + (textSize+1)*(lines.length), dashedPaint);

        // Shadow rect
        if (upperHalf)
            canvas.drawRect(0, height/2, width, height, shadowPaint);
        else
            canvas.drawRect(0, 0, width, height/2, shadowPaint);
    }

    private void setCaptionSizeFromSwipe(float fromX, float toX) {
        float deltaX = (toX - fromX) + residueX;
        float percentage = deltaX/width;


        int size = (int)(textSize * (1+percentage));
        if (size == textSize) {
            residueX = deltaX;
        }
        setCaptionSize(size);
    }

    public void setCaptionSize(int size) {
        if (size > maxCapSize) size = maxCapSize;
        else if (size < minCapSize) size = minCapSize;
        fillPaint.setTextSize(size);
        strokePaint.setTextSize(size);
        textSize = size;
        invalidate();
    }

    private void setOffset(float delta) {
        float possibleOffset = offset - delta;

        if (possibleOffset < minHeight)
            offset = minHeight;
        else if (possibleOffset >= maxHeight-textSize)
            offset = maxHeight-(textSize+1);
        else
            offset = possibleOffset;
    }

    @Override
    public void toggleCaret() {
        invalidate();
        underscoreShown = !underscoreShown;
    }

    public void setInitialScale(float scale) {
        this.scale = scale;
    }

    public Bitmap getFinalBitmap(){
        finalBitmapRequest = true;
        invalidate();
        return this.getDrawingCache();
    }

    public float getScale() {
        return scale;
    }

    public Metadata getMetadata() {
        scaleToOriginalValues();
        return new Metadata(underEditText.getText().toString(), textSize, offset);
    }


    private void scaleToOriginalValues() {
        textSize = (int)(textSize/scale);
        offset = offset/scale;
    }

    public class Metadata {
        String text;
        int textSize;
        float offset;

        public Metadata(String text, int textSize, float offset) {
            this.text = text;
            this.textSize = textSize;
            this.offset = offset;
        }

        public String getText() {
            return text;
        }

        public int getTextSize() {
            return textSize;
        }

        public float getOffset() {
            return offset;
        }
    }
}
