package org.gmdev.reactivedemo.websocket.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserProfileCreatedEventPublisher implements ApplicationListener<UserProfileCreatedEvent> {

    private final EventSinkService eventSinkService;

    @Autowired
    public UserProfileCreatedEventPublisher(EventSinkService eventSinkService) {
        this.eventSinkService = eventSinkService;
    }

    @Override
    public void onApplicationEvent(@Nullable UserProfileCreatedEvent event) {
        log.info("Received application event: UserProfileCreatedEvent");
        eventSinkService.onNext(event);
    }


}
