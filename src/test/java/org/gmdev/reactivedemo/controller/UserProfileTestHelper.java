package org.gmdev.reactivedemo.controller;


import org.gmdev.reactivedemo.model.UserProfile;
import org.gmdev.reactivedemo.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
@Transactional
public class UserProfileTestHelper {

    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserProfileTestHelper(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public void cleanDb() {
        userProfileRepository.deleteAll().block();
    }

    public Flux<UserProfile> saveUserProfileList(List<UserProfile> userProfiles) {
        return userProfileRepository.saveAll(userProfiles);
    }

    public Mono<UserProfile> findUserProfileById(String carId) {
        return userProfileRepository.findById(carId);
    }

    public Flux<UserProfile> findAllUserProfiles() {
        return userProfileRepository.findAll();
    }

    public List<UserProfile> getFakeUserProfiles() {
        UserProfile userProfile1 = new UserProfile(UUID.randomUUID().toString(), "email-1@email.com");
        UserProfile userProfile2 = new UserProfile(UUID.randomUUID().toString(), "email-2@email.com");
        UserProfile userProfile3 = new UserProfile(UUID.randomUUID().toString(), "email-3@email.com");

        return List.of(userProfile1, userProfile2, userProfile3);
    }



}
