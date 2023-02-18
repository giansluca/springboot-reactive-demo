package org.gmdev.reactivedemo.model;

import lombok.*;
import org.gmdev.reactivedemo.controller.model.UserProfileApiRes;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "UserProfile")
@Getter @Setter
@AllArgsConstructor
public class UserProfile {

    @Id
    private String id;

    private String email;

    public UserProfileApiRes toApiRes() {
        return new UserProfileApiRes(id, email);
    }

}
