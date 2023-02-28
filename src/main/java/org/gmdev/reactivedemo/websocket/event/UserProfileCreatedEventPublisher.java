package org.gmdev.reactivedemo.websocket.event;

import lombok.extern.slf4j.Slf4j;
import org.gmdev.reactivedemo.model.UserProfile;
import org.gmdev.reactivedemo.websocket.event.model.AppUserProfileCreatedEvent;
import org.gmdev.reactivedemo.websocket.event.model.ReactiveUserProfileCreatedEvent;
import org.gmdev.reactivedemo.websocket.event.model.ReactiveUserProfileCreatedEvent.UserProfileCreatedData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.gmdev.reactivedemo.websocket.event.model.ReactiveEvent.EventType.USER_PROFILE_CREATED;

@Slf4j
@Component
public class UserProfileCreatedEventPublisher implements ApplicationListener<AppUserProfileCreatedEvent> {

    private final EventSinkService eventSinkService;

    @Autowired
    public UserProfileCreatedEventPublisher(EventSinkService eventSinkService) {
        this.eventSinkService = eventSinkService;
    }

    @Override
    public void onApplicationEvent(AppUserProfileCreatedEvent event) {
        log.info("Received application event: AppUserProfileCreatedEvent");

        UserProfile userProfile = (UserProfile) event.getSource();
        var reactiveEvent = new ReactiveUserProfileCreatedEvent(
                UUID.randomUUID().toString(),
                USER_PROFILE_CREATED,
                new UserProfileCreatedData(userProfile.getId(), userProfile.getEmail())
        );

        eventSinkService.onNext(reactiveEvent);
    }


}
