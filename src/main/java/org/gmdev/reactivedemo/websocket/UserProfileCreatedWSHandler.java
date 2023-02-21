package org.gmdev.reactivedemo.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.gmdev.reactivedemo.controller.model.UserProfileApiRes;
import org.gmdev.reactivedemo.websocket.event.EventSinkService;
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

    private final EventSinkService eventSinkService;
    private final UserProfileCreatedEventPublisher eventPublisher;
    private final ObjectMapper mapper;

    @Autowired
    public UserProfileCreatedWSHandler(EventSinkService eventSinkService,
                                       UserProfileCreatedEventPublisher eventPublisher,
                                       ObjectMapper mapper) {

        this.eventSinkService = eventSinkService;
        this.eventPublisher = eventPublisher;
        this.mapper = mapper;
    }

//    @Override
//    public Mono<Void> handle2(WebSocketSession session) {
//        Flux<UserProfileCreatedEvent> userProfileCreatedEventFlux = Flux.create(eventPublisher).share();
//        Flux<WebSocketMessage> messages = userProfileCreatedEventFlux.map(userProfileCreatedEvent -> {
//            try {
//                UserProfileApiRes userProfile = (UserProfileApiRes) userProfileCreatedEvent.getSource();
//                String createdId = userProfile.getId();
//                log.info("received UserProfileCreatedEvent with id: {}", createdId);
//
//                Map<String, String> data = new HashMap<>();
//                data.put("id", createdId);
//
//                return mapper.writeValueAsString(data);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }).map(messageString -> {
//            log.info("sending message {}", messageString);
//            return session.textMessage(messageString);
//        });
//
//        return session.send(messages);
//    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<UserProfileCreatedEvent> messages = eventSinkService.getMessages();
        Flux<WebSocketMessage> socketMessage = messages.map(userProfileCreatedEvent -> {
            try {
                UserProfileApiRes userProfile = (UserProfileApiRes) userProfileCreatedEvent.getSource();
                String createdId = userProfile.getId();
                log.info("received UserProfileCreatedEvent with id: {}", createdId);

                Map<String, String> data = new HashMap<>();
                data.put("id", createdId);

                return mapper.writeValueAsString(data);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).map(messageString -> {
            log.info("sending message {}", messageString);
            return session.textMessage(messageString);
        });

        return session.send(socketMessage);
    }


}
