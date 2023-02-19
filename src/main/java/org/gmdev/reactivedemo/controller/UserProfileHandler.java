package org.gmdev.reactivedemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.gmdev.reactivedemo.controller.model.CreateUserProfileApiReq;
import org.gmdev.reactivedemo.controller.model.UpdateUserProfileApiReq;
import org.gmdev.reactivedemo.controller.model.UserProfileApiRes;
import org.gmdev.reactivedemo.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

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

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userProfileService.all(), UserProfileApiRes.class);
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        log.info("incoming call to path: '{}' with method: '{}'", request.path(), request.method());

        return userProfileService.get(request.pathVariable("id"))
                .flatMap(userProfile -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(userProfile), UserProfileApiRes.class)
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        log.info("incoming call to path: '{}' with method: '{}'", request.path(), request.method());

        return request.bodyToMono(CreateUserProfileApiReq.class)
                .flatMap(userProfile -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(userProfileService.create(userProfile), UserProfileApiRes.class)
                );
    }

    public Mono<ServerResponse> updateById(ServerRequest request) {
        log.info("incoming call to path: '{}' with method: '{}'", request.path(), request.method());

        return request.bodyToMono(UpdateUserProfileApiReq.class)
                .flatMap(userProfile -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(userProfileService.update(request.pathVariable("id"), userProfile), UserProfileApiRes.class)
                );
    }

    public Mono<ServerResponse> deleteById(ServerRequest request) {
        log.info("incoming call to path: '{}' with method: '{}'", request.path(), request.method());

        return userProfileService.delete(request.pathVariable("id"))
                .flatMap(userProfile -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(userProfile), UserProfileApiRes.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }


}
