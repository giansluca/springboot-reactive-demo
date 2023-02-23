package org.gmdev.reactivedemo.websocket.event.model;

public interface ReactiveEvent {

    String getEventId();
    EventType getEventType();
    Object getData();

    enum EventType {
        USER_PROFILE_CREATED,
        USER_PROFILE_UPDATED
    }

}
