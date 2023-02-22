package org.gmdev.reactivedemo.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.gmdev.reactivedemo.controller.model.UserProfileApiRes;
import org.gmdev.reactivedemo.websocket.event.EventSinkService;
import org.gmdev.reactivedemo.websocket.event.UserProfileCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class UserProfileCreatedWSHandler implements WebSocketHandler {

    private final EventSinkService eventSinkService;
    private final ObjectMapper mapper;

    @Autowired
    public UserProfileCreatedWSHandler(EventSinkService eventSinkService, ObjectMapper mapper) {
        this.eventSinkService = eventSinkService;
        this.mapper = mapper;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<UserProfileCreatedEvent> messages = eventSinkService.getMessages();

        Flux<WebSocketMessage> socketMessage = messages.map(userProfileCreatedEvent -> {
            try {
                UserProfileApiRes userProfile = (UserProfileApiRes) userProfileCreatedEvent.getSource();
                String createdId = userProfile.getId();

                log.info("sending to client UserProfileCreatedEvent with id: {}", createdId);

                String messagePayload = mapper.writeValueAsString(userProfile);
                return session.textMessage(messagePayload);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return session.send(socketMessage).and(session.receive().map(webSocketMessage -> {
                    String clientMessage = webSocketMessage.getPayloadAsText();

                    log.info("message from the client: {}", clientMessage);
                    return webSocketMessage;
                })
        );
    }


}
