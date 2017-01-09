package dev.jokr.memestagram.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jokr on 27.12.16..
 */

public class Meme implements Serializable {
    @Exclude
    public String $key;
    public String title;
    public long timestamp;
    public User user;

    public Meme() { }

    public Meme(String title) {
        this.title = title;
        this.timestamp = new Date().getTime();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("timestamp", timestamp);
        result.put("user", user.toSmallMap());

        return result;
    }

}
