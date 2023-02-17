package org.gmdev.reactivedemo.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.gmdev.reactivedemo.event.UserProfileCreatedEvent;
import org.gmdev.reactivedemo.event.UserProfileCreatedEventPublisher;
import org.gmdev.reactivedemo.model.UserProfile;
import org.springframework.context.annotation.*;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.*;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Configuration
public class WebSocketConfiguration {

    @Bean
    public Executor executor() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public HandlerMapping handlerMapping(WebSocketHandler wsh) {
        return new SimpleUrlHandlerMapping() {
            {
                setUrlMap(Collections.singletonMap("/ws/profiles", wsh));
                setOrder(10);
            }
        };
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
            Flux<WebSocketMessage> messageFlux = publish.map(evt -> {
                try {
                    UserProfile userProfile = (UserProfile) evt.getSource();
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
