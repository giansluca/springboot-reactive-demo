package org.gmdev.reactivedemo.websocket.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

@Slf4j
@Component
public class EventSinkServiceImp implements EventSinkService {

    private final Sinks.EmitFailureHandler emitFailureHandler =
            (signalType, emitResult) -> emitResult.equals(Sinks.EmitResult.FAIL_NON_SERIALIZED);
    private final Many<UserProfileCreatedEvent> sink;

    public EventSinkServiceImp() {
        sink = Sinks.many().multicast().directBestEffort();
    }

    @Override
    public void onNext(UserProfileCreatedEvent event) {
        sink.emitNext(event, emitFailureHandler);
    }

    @Override
    public Flux<UserProfileCreatedEvent> getMessages() {
        return sink.asFlux();
    }
}
