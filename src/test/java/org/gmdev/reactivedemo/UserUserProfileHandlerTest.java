package org.gmdev.reactivedemo;

import org.gmdev.reactivedemo.controller.model.CreateUserProfileApiReq;
import org.gmdev.reactivedemo.model.UserProfile;
import org.gmdev.reactivedemo.repository.UserProfileRepository;
import org.gmdev.reactivedemo.setup.MongoDBTestContainerSetup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@AutoConfigureWebTestClient
class UserUserProfileHandlerTest extends MongoDBTestContainerSetup {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    UserProfileRepository repository;

    @Test
    public void itShouldFindAllUserProfiles() {
        // Given
        when(repository.findAll()).thenReturn(Flux.just(
                new UserProfile("1", "email1@email.com"),
                new UserProfile("2", "email2@email.com")));

        // When - Then
        webTestClient
                .get()
                .uri("/user-profiles")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo("1")
                .jsonPath("$.[0].emailAddress").isEqualTo("email1@email.com")
                .jsonPath("$.[1].id").isEqualTo("2")
                .jsonPath("$.[1].emailAddress").isEqualTo("email2@email.com");
    }

    @Test
    public void itShouFindOneUserProfile() {
        // Given
        UserProfile data = new UserProfile("1", "email1@email.com");
        when(repository.findById(anyString())).thenReturn(Mono.just(data));

        // When - Then
        webTestClient
                .get()
                .uri("/user-profiles/" + data.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(data.getId())
                .jsonPath("$.emailAddress").isEqualTo(data.getEmail());
    }

    @Test
    public void itShouldSaveNewUserProfile() {
        // Given
        UserProfile fakeUserProfile = new UserProfile("123", "email@email.com");
        when(repository.save(any())).thenReturn(Mono.just(fakeUserProfile));

        CreateUserProfileApiReq bodyReq = new CreateUserProfileApiReq("email@email.com");

        // When - Then
        webTestClient
                .post()
                .uri("/user-profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(bodyReq), CreateUserProfileApiReq.class)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    public void itShouldUpdateAUserProfile() {
        // Given
        UserProfile data = new UserProfile("123", "email@email.com");
        UserProfile updatedData = new UserProfile("123", "updated@email.com");
        when(repository.findById(anyString())).thenReturn(Mono.just(data));
        when(repository.save(any())).thenReturn(Mono.just(updatedData));

        // When - Then
        webTestClient
                .put()
                .uri("/user-profiles/" + data.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updatedData), UserProfile.class)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void itShouldDeleteAUserProfile() {
        // Given
        UserProfile data = new UserProfile("123", "email@email.com");
        when(repository.findById(anyString())).thenReturn(Mono.just(data));
        when(repository.deleteById(anyString())).thenReturn(Mono.empty());

        // When - Then
       webTestClient
                .delete()
                .uri("/user-profiles/" + data.getId())
                .exchange()
                .expectStatus().isOk();
    }



}