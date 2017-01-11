package dev.jokr.memestagram.events;

import dev.jokr.memestagram.models.Conversation;

/**
 * Created by jokr on 11.01.17..
 */

public class ShowConversation {

    private Conversation conversation;

    public ShowConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public Conversation getConversation() {
        return conversation;
    }
}
