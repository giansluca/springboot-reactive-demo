package org.gmdev.reactivedemo.controller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
public class UpdateUserProfileApiReq {

    @NotBlank
    @Size(max = 64)
    private final String email;

}
