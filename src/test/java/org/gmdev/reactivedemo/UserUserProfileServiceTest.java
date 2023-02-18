package org.gmdev.reactivedemo;

import org.gmdev.reactivedemo.controller.model.CreateUserProfileApiReq;
import org.gmdev.reactivedemo.controller.model.UserProfileApiRes;
import org.gmdev.reactivedemo.model.UserProfile;
import org.gmdev.reactivedemo.repository.UserProfileRepository;
import org.gmdev.reactivedemo.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;
import reactor.core.publisher.*;
import reactor.test.StepVerifier;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(UserProfileService.class)
class UserUserProfileServiceTest {

    UserProfileService underTest;

    UserProfileRepository userProfileRepository;

    public UserUserProfileServiceTest(@Autowired UserProfileService underTest,
                                      @Autowired UserProfileRepository userProfileRepository) {

        this.underTest = underTest;
        this.userProfileRepository = userProfileRepository;
    }

    @Test
    public void itShouldGetAllProfiles() {
        // Given
        Flux<UserProfile> initData = userProfileRepository
                .deleteAll()
                .thenMany(
                        Flux.just(new UserProfile(null, "Josh"), new UserProfile(null, "Matt"), new UserProfile(null, "Jane"))
                        .flatMap(userProfileRepository::save)
                )
                .thenMany(userProfileRepository.findAll());

        List<UserProfile> allUserProfiles = initData.collectList().block();

        // When
        Flux<UserProfileApiRes> actual = underTest.all();

        // Then
        assertThat(allUserProfiles).isNotNull();
        StepVerifier
                .create(actual)
                .expectNextMatches(allUserProfiles::contains)
                .expectNextMatches(allUserProfiles::contains)
                .expectNextMatches(allUserProfiles::contains)
                .verifyComplete();
    }

    @Test
    public void itShouldSaveNewProfile() {
        // Given
        CreateUserProfileApiReq bodyReq = new CreateUserProfileApiReq("email@email.com");

        // When
        Mono<String> monoResult = this.underTest.create(bodyReq);

        // Then
        StepVerifier
                .create(monoResult)
                .expectNextMatches(StringUtils::hasText)
                .verifyComplete();
    }

    @Test
    public void itShouldDeleteProfileById() {
        // Given
        CreateUserProfileApiReq bodyReq = new CreateUserProfileApiReq("email@email.com");

        // When
        Mono<UserProfile> deleted = this.underTest.create(bodyReq)
                .flatMap(savedId -> this.underTest.delete(savedId));

        // Then
        StepVerifier
                .create(deleted)
                .expectNextMatches(profile -> profile.getEmail().equalsIgnoreCase("email@email.com"))
                .verifyComplete();
    }

    @Test
    public void itShouldUpdateProfile() {
        // Given
        CreateUserProfileApiReq bodyReq = new CreateUserProfileApiReq("email@email.com");

        // When
        Mono<UserProfile> updated = this.underTest.create(bodyReq)
                .flatMap(p -> this.underTest.update(p, "email-updated@email.com"));

        // Then
        StepVerifier
                .create(updated)
                .expectNextMatches(p -> p.getEmail().equalsIgnoreCase("email-updated@email.com"))
                .verifyComplete();
    }

    @Test
    public void itShouldGetById() {
        // Given
        String email = "email@email.com";
        CreateUserProfileApiReq bodyReq = new CreateUserProfileApiReq("email@email.com");

        // When
        Mono<UserProfileApiRes> selected = this.underTest.create(bodyReq)
                .flatMap(savedId -> this.underTest.get(savedId));

        // Then
        StepVerifier
                .create(selected)
                .expectNextMatches(profile -> StringUtils.hasText(profile.getId()) &&
                        email.equalsIgnoreCase(profile.getEmailAddress()))
                .verifyComplete();
    }




}