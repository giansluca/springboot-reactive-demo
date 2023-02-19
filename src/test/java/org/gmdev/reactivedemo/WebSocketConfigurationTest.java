package org.gmdev.reactivedemo;

import org.gmdev.reactivedemo.model.UserProfile;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class WebSocketConfigurationTest {

    private final WebSocketClient socketClient = new ReactorNettyWebSocketClient();
    private final WebClient webClient = WebClient.builder().build();

    private UserProfile generateRandomProfile() {
        return new UserProfile(UUID.randomUUID().toString(), UUID.randomUUID() + "@email.com");
    }

//    @Test
//    public void testNotificationsOnUpdates() throws Exception {
//        int count = 10;
//        AtomicLong counter = new AtomicLong();
//        URI uri = URI.create("ws://localhost:8080/ws/profiles");
//
//        socketClient.execute(uri, (WebSocketSession session) -> {
//            Mono<WebSocketMessage> out = Mono.just(session.textMessage("test"));
//
//            Flux<String> in = session
//                    .receive()
//                    .map(WebSocketMessage::getPayloadAsText);
//
//            return session
//                    .send(out)
//                    .thenMany(in)
//                    .doOnNext(str -> counter.incrementAndGet())
//                    .then();
//
//        }).subscribe();
//
//        Flux.<UserProfile>generate(sink -> sink.next(generateRandomProfile()))
//                .take(count)
//                .flatMap(this::write)
//                .blockLast();
//
//        Thread.sleep(1000);
//        assertThat(counter.get()).isEqualTo(count);
//    }
//
//    private Publisher<UserProfile> write(UserProfile p) {
//        return webClient
//                .post()
//                .uri("http://localhost:8080/profiles")
//                .body(BodyInserters.fromValue(p))
//                .retrieve()
//                .bodyToMono(String.class)
//                .thenReturn(p);
//    }


}