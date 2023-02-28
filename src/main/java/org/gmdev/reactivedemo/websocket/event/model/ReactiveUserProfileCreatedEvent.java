package org.gmdev.reactivedemo.websocket.event.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class ReactiveUserProfileCreatedEvent implements ReactiveEvent {

    private final String eventId;
    private final EventType eventType;
    private final UserProfileCreatedData data;

    @AllArgsConstructor @Getter
    public static class UserProfileCreatedData {
        private final String userProfileId;
        private final String emailAddress;
    }

}
