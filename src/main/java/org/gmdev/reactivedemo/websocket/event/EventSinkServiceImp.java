package org.gmdev.reactivedemo.websocket.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

import static reactor.core.publisher.Sinks.EmitFailureHandler.*;

@Slf4j
@Component
public class EventSinkServiceImp implements EventSinkService {

    private final Many<UserProfileCreatedEvent> sink;

    public EventSinkServiceImp() {
        sink =  Sinks.many().multicast().directBestEffort();
    }

    @Override
    public void onNext(UserProfileCreatedEvent event) {
        sink.emitNext(event, FAIL_FAST);
    }

    @Override
    public Flux<UserProfileCreatedEvent> getMessages() {
        return sink.asFlux();
    }
}
