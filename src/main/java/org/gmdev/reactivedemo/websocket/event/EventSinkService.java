package org.gmdev.reactivedemo.websocket.event;

import org.gmdev.reactivedemo.websocket.event.model.ReactiveEvent;
import reactor.core.publisher.Flux;

public interface EventSinkService {

    void onNext(ReactiveEvent next);

    Flux<ReactiveEvent> getMessages();

}
