package org.gmdev.reactivedemo.service;

import org.gmdev.reactivedemo.controller.model.CreateUserProfileApiReq;
import org.gmdev.reactivedemo.controller.model.UserProfileApiRes;
import org.gmdev.reactivedemo.event.UserProfileCreatedEvent;
import org.gmdev.reactivedemo.model.UserProfile;
import org.gmdev.reactivedemo.repository.UserProfileRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Transactional
@Service
public class UserProfileService {

    private final ApplicationEventPublisher publisher;
    private final UserProfileRepository repository;

    public UserProfileService(ApplicationEventPublisher publisher, UserProfileRepository repository) {
        this.publisher = publisher;
        this.repository = repository;
    }

    public Flux<UserProfileApiRes> all() {
        return repository.findAll().map(UserProfile::toApiRes);
    }

    public Mono<UserProfileApiRes> get(String id) {
        return repository.findById(id).map(UserProfile::toApiRes);
    }

    public Mono<String> create(CreateUserProfileApiReq bodyReq) {
        return repository.save(new UserProfile(UUID.randomUUID().toString(), bodyReq.getEmail()))
                .map(UserProfile::getId)
                .doOnSuccess(entity -> publisher.publishEvent(new UserProfileCreatedEvent(entity)));
    }

    public Mono<UserProfile> update(String id, String email) {
        return repository.findById(id)
                .map(p -> new UserProfile(p.getId(), email))
                .flatMap(repository::save);
    }

    public Mono<UserProfile> delete(String id) {
        return repository
                .findById(id)
                .flatMap(p -> repository.deleteById(p.getId()).thenReturn(p));
    }

}
