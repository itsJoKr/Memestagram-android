package dev.jokr.memestagram.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jokr on 27.12.16..
 */

@IgnoreExtraProperties
public class User implements Serializable {
    public String $key;
    public String key;
    public String username;
    public int type;
    public HashMap<String, String> likes;
    public HashMap<String, String> followed;

    public User() {}

    public User(String $key, String username, int type) {
        this.$key = $key;
        this.username = username;
        this.type = type;
    }


    public String getKey() {
        if ($key != null) return $key;
        else return key;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("type", type);

        return result;
    }

    @Exclude
    public Map<String, Object> toSmallMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("type", type);
        result.put("key", getKey());

        return result;

    }

}
