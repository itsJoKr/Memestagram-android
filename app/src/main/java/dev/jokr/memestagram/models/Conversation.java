package dev.jokr.memestagram.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jokr on 11.01.17..
 */

@IgnoreExtraProperties
public class Conversation implements Serializable {

    public String otherGuy;
    public String convoKey;

    public Conversation() { }

    public Conversation(String otherGuy, String $convoKey) {
        this.otherGuy = otherGuy;
        this.convoKey = $convoKey;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("otherGuy", otherGuy);
        result.put("convoKey", convoKey);
        return result;
    }
}
