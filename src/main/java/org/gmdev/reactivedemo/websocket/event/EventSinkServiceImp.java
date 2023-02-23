package org.gmdev.reactivedemo.websocket.event;

import lombok.extern.slf4j.Slf4j;
import org.gmdev.reactivedemo.websocket.event.model.ReactiveEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

@Slf4j
@Component
public class EventSinkServiceImp implements EventSinkService {

    private final Sinks.EmitFailureHandler emitFailureHandler =
            (signalType, emitResult) -> emitResult.equals(Sinks.EmitResult.FAIL_NON_SERIALIZED);
    private final Many<ReactiveEvent> sink;

    public EventSinkServiceImp() {
        sink = Sinks.many().multicast().directBestEffort();
    }

    @Override
    public void onNext(ReactiveEvent event) {
        sink.emitNext(event, emitFailureHandler);
    }

    @Override
    public Flux<ReactiveEvent> getMessages() {
        return sink.asFlux();
    }
}
