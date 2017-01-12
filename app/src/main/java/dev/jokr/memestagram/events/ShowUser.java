package dev.jokr.memestagram.events;

import dev.jokr.memestagram.models.User;

/**
 * Created by jokr on 12.01.17..
 */

public class ShowUser {

    private User user;

    public ShowUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
