package dev.jokr.memestagram.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jokr on 09.01.17..
 */

public class Comment {

    public String content;
    public String poster;
    public String posterKey;

    public Comment() { }

    public Comment(String content, String poster, String posterKey) {
        this.content = content;
        this.poster = poster;
        this.posterKey = posterKey;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("content", content);
        result.put("poster", poster);
        result.put("posterKey", posterKey);

        return result;
    }
}
