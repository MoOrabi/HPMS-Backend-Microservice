package com.hpms.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerRegisterRequest {

    @NotNull(message = "Username is required.")
    @Email(message = "Please provide a valid email address.")
    private String username;

    @NotNull(message = "Password is required.")
    @NotBlank(message = "Password cannot be blank.")
    private String password;

    @NotNull(message = "First name is required.")
    @NotBlank(message = "First name cannot be blank.")
    private String firstName;

    @NotNull(message = "Last name is required.")
    @NotBlank(message = "Last name cannot be blank.")
    private String lastName;

    @NotNull(message = "Job title is required.")
    @NotBlank(message = "Job title cannot be blank.")
    private String jobTitle;

}
