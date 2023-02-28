package org.gmdev.reactivedemo.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.gmdev.reactivedemo.websocket.event.EventSinkService;
import org.gmdev.reactivedemo.websocket.event.model.ReactiveEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class BaseWebSocketHandler implements WebSocketHandler {

    private final EventSinkService eventSinkService;
    private final ObjectMapper mapper;

    @Autowired
    public BaseWebSocketHandler(EventSinkService eventSinkService, ObjectMapper mapper) {
        this.eventSinkService = eventSinkService;
        this.mapper = mapper;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<ReactiveEvent> messages = eventSinkService.getMessages();
        Flux<WebSocketMessage> socketMessage = messages.map(event -> {
            try {
                if (event.getData() == null)
                    throw new IllegalStateException("Alt! event data cannot be null!");

                log.info("sending to client {} with id: {}", event.getEventType(), event.getEventId());
                String messagePayload = mapper.writeValueAsString(event);
                return session.textMessage(messagePayload);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return session.send(socketMessage).and(receive(session));
    }

    private Flux<WebSocketMessage> receive(WebSocketSession session) {
        return session.receive().map(webSocketMessage -> {
            String clientMessage = webSocketMessage.getPayloadAsText();

            log.info("message from the client: {}", clientMessage);
            return webSocketMessage;
        });
    }


}
