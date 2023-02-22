package org.gmdev.reactivedemo.controller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@AllArgsConstructor
@Getter
public class CreateUserProfileApiReq {

    @NotBlank
    @Size(max = 64)
    private final String email;


}
