package org.gmdev.reactivedemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.gmdev.reactivedemo.controller.model.CreateUserProfileApiReq;
import org.gmdev.reactivedemo.controller.model.UserProfileApiRes;
import org.gmdev.reactivedemo.model.UserProfile;
import org.gmdev.reactivedemo.service.UserProfileService;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
public class UserProfileHandler {

    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileHandler(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    public Mono<ServerResponse> all(ServerRequest request) {
        log.info("incoming call to path: '{}' with method: '{}'", request.path(), request.method());
        Flux<UserProfileApiRes> fluxResult = this.userProfileService.all();

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fluxResult, UserProfileApiRes.class);
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        log.info("incoming call to path: '{}' with method: '{}'", request.path(), request.method());
        String userProfileId = request.pathVariable("id");

        Mono<UserProfileApiRes> monoResult = userProfileService.get(userProfileId)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(monoResult, UserProfileApiRes.class);
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        log.info("incoming call to path: '{}' with method: '{}'", request.path(), request.method());

        Mono<String> monoResult = request
                .bodyToMono(CreateUserProfileApiReq.class)
                .flatMap(userProfileService::create);

        return ServerResponse
                .created(URI.create(String.format("/profiles/%s", monoResult)))
                .contentType(MediaType.APPLICATION_JSON)
                .build();
    }

    public Mono<ServerResponse> updateById(ServerRequest request) {
        String userProfileId = request.pathVariable("id");

        Flux<UserProfile> id = request.bodyToFlux(UserProfile.class)
                .flatMap(p -> this.userProfileService.update(userProfileId, p.getEmail()));

        return defaultReadResponse(id);
    }

    public Mono<ServerResponse> deleteById(ServerRequest request) {
        String userProfileId = request.pathVariable("id");

        return defaultReadResponse(this.userProfileService.delete(userProfileId));
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


}
