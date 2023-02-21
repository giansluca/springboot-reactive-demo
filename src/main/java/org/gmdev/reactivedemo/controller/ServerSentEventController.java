package org.gmdev.reactivedemo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gmdev.reactivedemo.websocket.event.UserProfileCreatedEvent;
import org.gmdev.reactivedemo.websocket.event.UserProfileCreatedEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
public class ServerSentEventController {

    private final Flux<UserProfileCreatedEvent> userProfileCreatedEventFlux;
    private final ObjectMapper objectMapper;

    public ServerSentEventController(UserProfileCreatedEventPublisher eventPublisher, ObjectMapper objectMapper) {
        this.userProfileCreatedEventFlux = Flux.create(eventPublisher).share();
        this.objectMapper = objectMapper;
    }

    @GetMapping(path = "/sse/user-profiles", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CrossOrigin(origins = "http://localhost:3000")
    public Flux<String> userProfiles() {
        return this.userProfileCreatedEventFlux.map(profileCreatedEvent -> {
            try {
                return objectMapper.writeValueAsString(profileCreatedEvent);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
