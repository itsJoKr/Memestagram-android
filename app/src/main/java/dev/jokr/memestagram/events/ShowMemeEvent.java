package dev.jokr.memestagram.events;

import android.graphics.drawable.Drawable;

import dev.jokr.memestagram.models.Meme;

/**
 * Created by jokr on 03.01.17..
 */

public class ShowMemeEvent {
    private Meme meme;
    private Drawable drawable;

    public ShowMemeEvent(Meme meme, Drawable drawable)
    {
        this.meme = meme;
        this.drawable = drawable;
    }

    public Meme getMeme() {
        return meme;
    }

    public Drawable getDrawable() {
        return drawable;
    }
}
