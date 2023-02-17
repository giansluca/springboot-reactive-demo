package org.gmdev.reactivedemo;

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
        Flux<UserProfile> actual = underTest.all();

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
        // When
        Mono<UserProfile> profileMono = this.underTest.create("email@email.com");

        // Then
        StepVerifier
                .create(profileMono)
                .expectNextMatches(saved -> StringUtils.hasText(saved.getId()))
                .verifyComplete();
    }

    @Test
    public void itShouldDeleteProfileById() {
        // Given
        String test = "test";

        // When
        Mono<UserProfile> deleted = this.underTest.create(test)
                .flatMap(saved -> this.underTest.delete(saved.getId()));

        // Then
        StepVerifier
                .create(deleted)
                .expectNextMatches(profile -> profile.getEmail().equalsIgnoreCase(test))
                .verifyComplete();
    }

    @Test
    public void itShouldUpdateProfile() {
        // Given
        // When
        Mono<UserProfile> updated = this.underTest.create("test")
                .flatMap(p -> this.underTest.update(p.getId(), "test1"));

        // Then
        StepVerifier
                .create(updated)
                .expectNextMatches(p -> p.getEmail().equalsIgnoreCase("test1"))
                .verifyComplete();
    }

    @Test
    public void itShouldGetTemplateById() {
        // Given
        String test = UUID.randomUUID().toString();

        // When
        Mono<UserProfile> selected = this.underTest.create(test)
                .flatMap(saved -> this.underTest.get(saved.getId()));

        // Then
        StepVerifier
                .create(selected)
                .expectNextMatches(profile -> StringUtils.hasText(profile.getId()) &&
                        test.equalsIgnoreCase(profile.getEmail()))
                .verifyComplete();
    }




}