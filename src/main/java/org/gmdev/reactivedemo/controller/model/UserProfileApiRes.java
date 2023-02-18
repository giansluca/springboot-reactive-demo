package org.gmdev.reactivedemo.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Getter
public class UserProfileApiRes {

    private String id;
    private String emailAddress;

}
