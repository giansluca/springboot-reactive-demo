package org.gmdev.reactivedemo.controller;

import org.gmdev.reactivedemo.controller.model.CreateUserProfileApiReq;
import org.gmdev.reactivedemo.controller.model.UpdateUserProfileApiReq;
import org.gmdev.reactivedemo.controller.model.UserProfileApiRes;
import org.gmdev.reactivedemo.model.UserProfile;
import org.gmdev.reactivedemo.setup.MongoDBTestContainerSetup;
import org.gmdev.reactivedemo.setup.UserProfileTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@AutoConfigureWebTestClient
class UserProfileHandlerTest extends MongoDBTestContainerSetup {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    UserProfileTestHelper userProfileTestHelper;

    @AfterEach
    void setUp() {
        userProfileTestHelper.cleanDb();
    }

    @Test
    public void itShouldFindAllUserProfiles() {
        // Given
        List<UserProfile> us = userProfileTestHelper.getFakeUserProfiles();
        userProfileTestHelper.saveUserProfileList(us).blockFirst();

        // When
        List<UserProfileApiRes> userProfiles = webTestClient
                .get()
                .uri("/user-profiles")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(UserProfileApiRes.class)
                .returnResult()
                .getResponseBody();

        // Then;
        assertThat(userProfiles).hasSize(3);
    }

    @Test
    public void itShouFindOneUserProfile() {
        // Given
        List<UserProfile> us = userProfileTestHelper.getFakeUserProfiles();
        userProfileTestHelper.saveUserProfileList(us).blockFirst();
        UserProfile userProfile1 = us.get(0);

        // When - Then
        webTestClient
                .get()
                .uri("/user-profiles/" + userProfile1.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(userProfile1.getId())
                .jsonPath("$.emailAddress").isEqualTo(userProfile1.getEmail());
    }

    @Test
    public void itShouldSaveNewUserProfile() {
        // Given
        String email = "email-created@email.com";
        CreateUserProfileApiReq bodyReq = new CreateUserProfileApiReq(email);

        // When - Then
        UserProfileApiRes userProfileCreated = webTestClient
                .post()
                .uri("/user-profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(bodyReq), CreateUserProfileApiReq.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UserProfileApiRes.class)
                .returnResult()
                .getResponseBody();

        assert userProfileCreated != null;
        Mono<UserProfile> selected = userProfileTestHelper.findUserProfileById(userProfileCreated.getId());

        StepVerifier
                .create(selected)
                .expectNextMatches(profile ->
                        profile.getId().equals(userProfileCreated.getId()) && profile.getEmail().equals(email))
                .verifyComplete();
    }

    @Test
    public void itShouldUpdateAUserProfile() {
        // Given
        List<UserProfile> us = userProfileTestHelper.getFakeUserProfiles();
        userProfileTestHelper.saveUserProfileList(us).blockFirst();
        UserProfile profileToUpdate = us.get(0);

        String email = "email-updated@email.com";
        UpdateUserProfileApiReq bodyReq = new UpdateUserProfileApiReq(email);

        // When - Then
        UserProfileApiRes userProfileUpdated = webTestClient
                .put()
                .uri("/user-profiles/" + profileToUpdate.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(bodyReq), UpdateUserProfileApiReq.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UserProfileApiRes.class)
                .returnResult()
                .getResponseBody();

        assert userProfileUpdated != null;
        Mono<UserProfile> selected = userProfileTestHelper.findUserProfileById(userProfileUpdated.getId());

        StepVerifier
                .create(selected)
                .expectNextMatches(profile ->
                        profile.getId().equals(userProfileUpdated.getId()) && profile.getEmail().equals(email))
                .verifyComplete();
    }

    @Test
    void itShouldDeleteAUserProfile() {
        // Given
        List<UserProfile> us = userProfileTestHelper.getFakeUserProfiles();
        userProfileTestHelper.saveUserProfileList(us).blockFirst();
        UserProfile profileToDelete = us.get(0);

        // When - Then
        UserProfileApiRes userProfileDeleted = webTestClient
                .delete()
                .uri("/user-profiles/" + profileToDelete.getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UserProfileApiRes.class)
                .returnResult()
                .getResponseBody();


        assert userProfileDeleted != null;
        assertThat(userProfileDeleted.getId()).isEqualTo(profileToDelete.getId());
        List<UserProfile> actualUserProfiles = userProfileTestHelper.findAllUserProfiles().collectList().block();
        assertThat(actualUserProfiles).hasSize(us.size() - 1);
    }



}