package org.gmdev.reactivedemo.websocket.event.model;

import org.springframework.context.ApplicationEvent;

public class AppUserProfileUpdatedEvent extends ApplicationEvent {

    public AppUserProfileUpdatedEvent(Object source) {
        super(source);
    }

}
