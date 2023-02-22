package org.gmdev.reactivedemo.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gmdev.reactivedemo.websocket.event.EventSinkService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ServerSentEventController {

    private final EventSinkService eventSinkService;
    private final ObjectMapper objectMapper;

    public ServerSentEventController(EventSinkService eventSinkService, ObjectMapper objectMapper) {
        this.eventSinkService = eventSinkService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(path = "/sse/user-profiles", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CrossOrigin(origins = "http://localhost:3000")
    public Flux<String> userProfiles() {
        return eventSinkService.getMessages().map(profileCreatedEvent -> {
            try {
                return objectMapper.writeValueAsString(profileCreatedEvent);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

}
