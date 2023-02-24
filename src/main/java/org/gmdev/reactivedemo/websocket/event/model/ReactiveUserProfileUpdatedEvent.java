package org.gmdev.reactivedemo.websocket.event.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ReactiveUserProfileUpdatedEvent implements ReactiveEvent {

    private final String eventId;
    private final EventType eventType;
    private final Object data;

    @AllArgsConstructor @Getter
    public static class UserProfileUpdatedData {
        private final String userProfileId;
        private final String emailAddress;
        private final LocalDateTime updatedAt;
    }

}
