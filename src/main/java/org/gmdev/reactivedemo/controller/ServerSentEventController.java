package org.gmdev.reactivedemo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gmdev.reactivedemo.event.UserProfileCreatedEvent;
import org.gmdev.reactivedemo.event.UserProfileCreatedEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
public class ServerSentEventController {

    private final Flux<UserProfileCreatedEvent> events;
    private final ObjectMapper objectMapper;

    public ServerSentEventController(UserProfileCreatedEventPublisher eventPublisher, ObjectMapper objectMapper) {
        this.events = Flux.create(eventPublisher).share();
        this.objectMapper = objectMapper;
    }

    @GetMapping(path = "/sse/user-profiles", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CrossOrigin(origins = "http://localhost:3000")
    public Flux<String> userProfiles() {
        return this.events.map(pce -> {
            try {
                return objectMapper.writeValueAsString(pce);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
