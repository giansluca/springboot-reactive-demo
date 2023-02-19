package org.gmdev.reactivedemo.websocket;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
public class UserProfileCreatedWSHandler implements WebSocketHandler {

    @Override
    public Mono<Void> handle(@Nullable WebSocketSession session) {
        if (session == null) return Mono.empty();

        session.receive().subscribe(message -> {
                // process message here
        });

        return Mono.empty();
    }

}
