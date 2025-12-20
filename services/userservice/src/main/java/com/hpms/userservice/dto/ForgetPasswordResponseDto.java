package com.hpms.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ForgetPasswordResponseDto {

    @Email
    @NotNull(message = "Please provide a valid email Password")
    private String email;

    @NotNull(message = "Please provide a valid token")
    private String token;

}