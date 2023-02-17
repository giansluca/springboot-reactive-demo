package org.gmdev.reactivedemo.service;

import org.gmdev.reactivedemo.event.UserProfileCreatedEvent;
import org.gmdev.reactivedemo.model.UserProfile;
import org.gmdev.reactivedemo.repository.UserProfileRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class UserProfileService {

    private final ApplicationEventPublisher publisher;
    private final UserProfileRepository repository;

    public UserProfileService(ApplicationEventPublisher publisher, UserProfileRepository repository) {
        this.publisher = publisher;
        this.repository = repository;
    }

    public Flux<UserProfile> all() {
        return repository.findAll();
    }

    public Mono<UserProfile> get(String id) {
        return repository.findById(id);
    }

    public Mono<UserProfile> update(String id, String email) {
        return repository.findById(id)
                .map(p -> new UserProfile(p.getId(), email))
                .flatMap(repository::save);
    }

    public Mono<UserProfile> create(String email) {
        return repository
                .save(new UserProfile(UUID.randomUUID().toString(), email))
                .doOnSuccess(entity -> publisher.publishEvent(new UserProfileCreatedEvent(entity)));
    }

    public Mono<UserProfile> delete(String id) {
        return repository
                .findById(id)
                .flatMap(p -> repository.deleteById(p.getId()).thenReturn(p));
    }

}
