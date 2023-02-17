package org.gmdev.reactivedemo.controller;

import org.gmdev.reactivedemo.model.UserProfile;
import org.gmdev.reactivedemo.service.UserProfileService;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class UserProfileHandler {

    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileHandler(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    public Mono<ServerResponse> all(ServerRequest request) {
        return defaultReadResponse(this.userProfileService.all());
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        return defaultReadResponse(this.userProfileService.get(id(request)));
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Flux<UserProfile> flux = request
                .bodyToFlux(UserProfile.class)
                .flatMap(toWrite -> this.userProfileService.create(toWrite.getEmail()));

        return defaultWriteResponse(flux);
    }

    public Mono<ServerResponse> updateById(ServerRequest request) {
        Flux<UserProfile> id = request.bodyToFlux(UserProfile.class)
                .flatMap(p -> this.userProfileService.update(id(request), p.getEmail()));

        return defaultReadResponse(id);
    }

    public Mono<ServerResponse> deleteById(ServerRequest request) {
        return defaultReadResponse(this.userProfileService.delete(id(request)));
    }

    private static Mono<ServerResponse> defaultWriteResponse(Publisher<UserProfile> profiles) {
        return Mono
                .from(profiles)
                .flatMap(p -> ServerResponse
                        .created(URI.create("/profiles/" + p.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .build()
                );
    }

    private static Mono<ServerResponse> defaultReadResponse(Publisher<UserProfile> profiles) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(profiles, UserProfile.class);
    }

    private static String id(ServerRequest request) {
        return request.pathVariable("id");
    }
}
