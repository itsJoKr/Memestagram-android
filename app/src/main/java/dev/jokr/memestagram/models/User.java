package dev.jokr.memestagram.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jokr on 27.12.16..
 */

@IgnoreExtraProperties
public class User {
    public String $key;
    public String username;
    public int type;

    public User() {}

    public User(String $key, String username, int type) {
        this.$key = $key;
        this.username = username;
        this.type = type;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("type", type);

        return result;
    }

}
