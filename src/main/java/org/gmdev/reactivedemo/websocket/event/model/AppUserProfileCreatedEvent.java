package org.gmdev.reactivedemo.websocket.event.model;

import org.springframework.context.ApplicationEvent;

public class AppUserProfileCreatedEvent extends ApplicationEvent {

    public AppUserProfileCreatedEvent(Object source) {
        super(source);
    }

}
