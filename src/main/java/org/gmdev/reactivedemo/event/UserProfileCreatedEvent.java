package org.gmdev.reactivedemo.event;

import org.springframework.context.ApplicationEvent;

public class UserProfileCreatedEvent extends ApplicationEvent {

    public UserProfileCreatedEvent(Object source) {
        super(source);
    }

}
