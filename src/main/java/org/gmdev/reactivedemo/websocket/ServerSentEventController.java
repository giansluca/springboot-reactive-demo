package org.gmdev.reactivedemo.websocket;

import lombok.extern.slf4j.Slf4j;
import org.gmdev.reactivedemo.websocket.event.EventSinkService;
import org.gmdev.reactivedemo.websocket.event.model.ReactiveEvent;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
public class ServerSentEventController {

    private final EventSinkService eventSinkService;

    public ServerSentEventController(EventSinkService eventSinkService) {
        this.eventSinkService = eventSinkService;
    }

    @GetMapping(path = "/sse/stream-flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ReactiveEvent> userProfiles() {
        try {
            return eventSinkService.getMessages();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
