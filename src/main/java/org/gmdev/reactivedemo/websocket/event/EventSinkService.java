package org.gmdev.reactivedemo.websocket.event;

import reactor.core.publisher.Flux;

public interface EventSinkService {

    void onNext(UserProfileCreatedEvent next);

    Flux<UserProfileCreatedEvent> getMessages();

}
