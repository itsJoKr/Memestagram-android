package dev.jokr.memestagram.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import dev.jokr.memestagram.R;

import static android.R.attr.scaleHeight;
import static android.R.attr.scaleWidth;

/**
 * Created by jokr on 03.02.17..
 */

public class PlebUsernameTag {

    public static void addTag(Context ctx, TextView t) {
        Drawable drawable = ContextCompat.getDrawable(ctx, R.drawable.ic_pleb_tag);
//        drawable.setBounds(0, 0, (int)(drawable.getIntrinsicWidth()*0.5),(int)(drawable.getIntrinsicHeight()*0.5));
//        ScaleDrawable sd = new ScaleDrawable(drawable, 0, scaleWidth, scaleHeight);
        t.setCompoundDrawables(drawable, null, null, null);
    }

}
