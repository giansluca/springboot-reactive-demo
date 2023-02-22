package org.gmdev.reactivedemo.websocket;

import org.gmdev.reactivedemo.model.UserProfile;
import org.gmdev.reactivedemo.setup.MongoDBTestContainerSetup;
import org.gmdev.reactivedemo.setup.UserProfileTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class UserProfileCreatedWSHandlerTest extends MongoDBTestContainerSetup {

    private final WebSocketClient socketClient = new ReactorNettyWebSocketClient();
    private final WebClient webClient = WebClient.builder().build();

    @Autowired
    UserProfileTestHelper userProfileTestHelper;

    @AfterEach
    void setUp() {
        userProfileTestHelper.cleanDb();
    }

    @Test
    public void testCreateUserProfileEventWebSocket() throws Exception {
        int count = 10;
        AtomicLong counter = new AtomicLong();
        URI uri = URI.create("ws://localhost:" + randomServerPort + "/ws/user-profile-created");

        socketClient.execute(uri, (WebSocketSession session) -> {
            Mono<WebSocketMessage> out = Mono.just(session.textMessage("message-test-123"));
            Flux<String> in = session.receive().map(WebSocketMessage::getPayloadAsText);

            return session.send(out)
                    .thenMany(in)
                    .doOnNext(str -> counter.incrementAndGet())
                    .then();

        }).subscribe();

        Flux.<UserProfile>generate(sink -> sink.next(generateRandomProfile()))
                .take(count)
                .flatMap(this::write)
                .blockLast();

        Thread.sleep(1000);
        assertThat(counter.get()).isEqualTo(count);
    }

    private Publisher<UserProfile> write(UserProfile p) {
        return webClient
                .post()
                .uri("http://localhost:" + randomServerPort + "/user-profiles")
                .body(BodyInserters.fromValue(p))
                .retrieve()
                .bodyToMono(String.class)
                .thenReturn(p);
    }

    private UserProfile generateRandomProfile() {
        return new UserProfile(UUID.randomUUID().toString(), UUID.randomUUID() + "@email.com");
    }


}