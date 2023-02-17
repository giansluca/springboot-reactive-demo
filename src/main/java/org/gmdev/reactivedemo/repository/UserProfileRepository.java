package org.gmdev.reactivedemo.repository;

import org.gmdev.reactivedemo.model.UserProfile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserProfileRepository extends ReactiveMongoRepository<UserProfile, String> {
}
