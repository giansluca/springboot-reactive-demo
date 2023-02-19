package org.gmdev.reactivedemo.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.gmdev.reactivedemo.controller.model.UserProfileApiRes;
import org.gmdev.reactivedemo.event.UserProfileCreatedEvent;
import org.gmdev.reactivedemo.event.UserProfileCreatedEventPublisher;
import org.gmdev.reactivedemo.websocket.UserProfileCreatedWSHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class WebSocketConfiguration {

    private final UserProfileCreatedWSHandler userProfileCreatedWSHandler;

    @Autowired
    public WebSocketConfiguration(UserProfileCreatedWSHandler userProfileCreatedWSHandler) {
        this.userProfileCreatedWSHandler = userProfileCreatedWSHandler;
    }

    @Bean
    public HandlerMapping handlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/ws/user-profile-created", userProfileCreatedWSHandler);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);

        return handlerMapping;
    }

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    WebSocketHandler webSocketHandler(
            ObjectMapper objectMapper,
            UserProfileCreatedEventPublisher eventPublisher) {

        Flux<UserProfileCreatedEvent> publish = Flux.create(eventPublisher).share();

        return session -> {
            Flux<WebSocketMessage> messageFlux = publish.map(event -> {
                try {
                    UserProfileApiRes userProfile = (UserProfileApiRes) event.getSource();
                    Map<String, String> data = new HashMap<>();
                    data.put("id", userProfile.getId());
                    return objectMapper.writeValueAsString(data);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }).map(str -> {
                log.info("sending " + str);
                return session.textMessage(str);
            });

            return session.send(messageFlux);
        };
    }

}
