package dev.jokr.memestagram.events;

import dev.jokr.memestagram.models.User;

/**
 * Created by jokr on 12.01.17..
 */

public class ShowUserEvent {

    private User user;

    public ShowUserEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
