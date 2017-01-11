package dev.jokr.memestagram.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jokr on 11.01.17..
 */

public class Message {

    public String content;
    public long timestamp;
    public String sender;

    public Message() {
    }

    public Message(String content, long timestamp, String senderKey) {
        this.content = content;
        this.timestamp = timestamp;
        this.sender = senderKey;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("content", content);
        result.put("timestamp", timestamp);
        result.put("sender", sender);

        return result;
    }
}
