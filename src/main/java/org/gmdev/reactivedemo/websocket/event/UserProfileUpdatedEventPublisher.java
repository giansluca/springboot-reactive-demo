package org.gmdev.reactivedemo.websocket.event;

import lombok.extern.slf4j.Slf4j;
import org.gmdev.reactivedemo.model.UserProfile;
import org.gmdev.reactivedemo.websocket.event.model.AppUserProfileUpdatedEvent;
import org.gmdev.reactivedemo.websocket.event.model.ReactiveUserProfileUpdatedEvent;
import org.gmdev.reactivedemo.websocket.event.model.ReactiveUserProfileUpdatedEvent.UserProfileUpdatedData;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.gmdev.reactivedemo.websocket.event.model.ReactiveEvent.EventType.USER_PROFILE_UPDATED;

@Slf4j
@Component
public class UserProfileUpdatedEventPublisher implements ApplicationListener<AppUserProfileUpdatedEvent>  {

    private final EventSinkService eventSinkService;

    public UserProfileUpdatedEventPublisher(EventSinkService eventSinkService) {
        this.eventSinkService = eventSinkService;
    }

    @Override
    public void onApplicationEvent(AppUserProfileUpdatedEvent event) {
        log.info("Received application event: AppUserProfileUpdatedEvent");

        UserProfile userProfile = (UserProfile) event.getSource();
        var reactiveEvent = new ReactiveUserProfileUpdatedEvent(
                UUID.randomUUID().toString(),
                USER_PROFILE_UPDATED,
                new UserProfileUpdatedData(userProfile.getId(), userProfile.getEmail(), LocalDateTime.now())
        );

        eventSinkService.onNext(reactiveEvent);
    }

}
