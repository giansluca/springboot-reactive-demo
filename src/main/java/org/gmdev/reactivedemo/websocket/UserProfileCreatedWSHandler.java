package org.gmdev.reactivedemo.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.gmdev.reactivedemo.controller.model.UserProfileApiRes;
import org.gmdev.reactivedemo.websocket.event.UserProfileCreatedEvent;
import org.gmdev.reactivedemo.websocket.event.UserProfileCreatedEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class UserProfileCreatedWSHandler implements WebSocketHandler {

    private final UserProfileCreatedEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserProfileCreatedWSHandler(UserProfileCreatedEventPublisher eventPublisher, ObjectMapper objectMapper) {
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<UserProfileCreatedEvent> publish = Flux.create(eventPublisher).share();

        Flux<WebSocketMessage> messages = publish.map(userProfileCreatedEvent -> {
            try {
                UserProfileApiRes userProfile = (UserProfileApiRes) userProfileCreatedEvent.getSource();
                String createdId = userProfile.getId();
                log.info("received UserProfileCreatedEvent with id: {}", createdId);

                Map<String, String> data = new HashMap<>();
                data.put("id", createdId);

                return objectMapper.writeValueAsString(data);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).map(messageString -> {
            log.info("sending message {}", messageString);
            return session.textMessage(messageString);
        });

        return session.send(messages);
    }

}
