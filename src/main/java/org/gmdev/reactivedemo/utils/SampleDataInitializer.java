package org.gmdev.reactivedemo.utils;

import lombok.extern.slf4j.Slf4j;
import org.gmdev.reactivedemo.model.UserProfile;
import org.gmdev.reactivedemo.repository.UserProfileRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Slf4j
@Component
@Profile("demo")
public class SampleDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final UserProfileRepository userProfileRepository;

    public SampleDataInitializer(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        userProfileRepository
                .deleteAll()
                .thenMany(Flux.just("A", "B", "C", "D")
                        .map(name -> new UserProfile(UUID.randomUUID().toString(), name + "@email.com"))
                        .flatMap(userProfileRepository::save)
                )
                .thenMany(userProfileRepository.findAll())
                .subscribe(userProfile -> log.info("saving " + userProfile.toString()));
    }


}
