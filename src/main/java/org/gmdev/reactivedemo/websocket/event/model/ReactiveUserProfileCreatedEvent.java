package org.gmdev.reactivedemo.websocket.event.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class ReactiveUserProfileCreatedEvent implements ReactiveEvent {

    private final String eventId;
    private final EventType eventType;
    private final Object data;

    @AllArgsConstructor @Getter
    public static class Data {
        private final String userProfileId;
        private final String emailAddress;
    }

}
