package dev.jokr.memestagram.events;

import dev.jokr.memestagram.models.Meme;

/**
 * Created by jokr on 03.01.17..
 */

public class ShowMeme {
    private Meme meme;

    public ShowMeme(Meme meme) {
        this.meme = meme;
    }

    public Meme getMeme() {
        return meme;
    }
}
