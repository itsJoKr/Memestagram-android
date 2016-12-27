package dev.jokr.memestagram.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jokr on 27.12.16..
 */

public class Meme {
    @Exclude
    public String $key;
    public String title;

    public Meme() { }

    public Meme(String title) {
        this.title = title;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);

        return result;
    }

}
