package org.gmdev.reactivedemo.websocket.event;

import org.springframework.context.ApplicationEvent;

public class UserProfileCreatedEvent extends ApplicationEvent {

    public UserProfileCreatedEvent(Object source) {
        super(source);
    }

}
