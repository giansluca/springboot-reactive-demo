package org.gmdev.reactivedemo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "UserProfile")
@Getter @Setter
@AllArgsConstructor
public class UserProfile {

    @Id
    private String id;

    private String email;

}
