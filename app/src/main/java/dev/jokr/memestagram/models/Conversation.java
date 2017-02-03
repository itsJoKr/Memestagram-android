package dev.jokr.memestagram.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

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
}
